package com.flrnks.utils.ssm

import com.typesafe.scalalogging.LazyLogging
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.DescribeStackResourcesRequest
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.{AutomationExecutionStatus, GetAutomationExecutionRequest, SsmException, StartAutomationExecutionRequest}
import scala.concurrent.{ExecutionContext, Future}

case class SsmAutomation(profile: String)(implicit ec: ExecutionContext) extends LazyLogging {
  
  private val ssmClient =  SsmClient.builder()
      .credentialsProvider(ProfileCredentialsProvider.create(profile))
      .region(Region.EU_WEST_1)
      .build()
  
  private val cfClient = CloudFormationClient.builder()
        .credentialsProvider(ProfileCredentialsProvider.create(profile))
        .region(Region.EU_WEST_1)
        .build()
  
  def startDocumentWithParameters(documentName: String, documentParameters: java.util.Map[String, java.util.List[String]]): Future[Unit] = {

    executeAutomation(documentName, documentParameters)
      .flatMap(waitForAutomationToFinish)
      .map(_ => ())
      .recover {
        case e: SsmAutomationExecutionException =>
          logger.warn(s"The automation execution failed: ${e.getMessage}")
        case e: SsmException =>
          logger.warn(s"Failed to execute document, error message: ${e.getMessage}")
        case e: SdkClientException =>
          logger.warn(s"SDK failure, error message: ${e.getMessage}")
    }
  }

  private def executeAutomation(documentName: String, parameters: java.util.Map[String,java.util.List[String]]): Future[String] = {
    logger.info(s"Going to kick off SSM orchestration document: $documentName")
    
    val startAutomationRequest = StartAutomationExecutionRequest.builder()
      .documentName(documentName)
      .parameters(parameters)
      .build()
    
    Future {
      val executionResponse = ssmClient.startAutomationExecution(startAutomationRequest)
      logger.info(s"Execution id: ${executionResponse.automationExecutionId()}")
      executionResponse.automationExecutionId()
    }
  }
  
  def getOrchestrationDocumentName(stackName: String): String = {
    cfClient.describeStackResources(DescribeStackResourcesRequest.builder()
      .stackName(stackName)
      .logicalResourceId("OrchestrationDocument")
      .build()
    ).stackResources().get(0).physicalResourceId()
  }

  private def waitForAutomationToFinish(executionId: String): Future[String] = {
    
    val getExecutionRequest = GetAutomationExecutionRequest.builder()
      .automationExecutionId(executionId).build()
    var status = AutomationExecutionStatus.IN_PROGRESS

    Future {
      var retries = 0
      while (status != AutomationExecutionStatus.SUCCESS) {
        val automationExecutionResponse = ssmClient.getAutomationExecution(getExecutionRequest)
        status = automationExecutionResponse.automationExecution.automationExecutionStatus()
        status match {
          case AutomationExecutionStatus.CANCELLED | AutomationExecutionStatus.FAILED | AutomationExecutionStatus.TIMED_OUT =>
            throw SsmAutomationExecutionException(status, automationExecutionResponse.automationExecution.failureMessage)
          case AutomationExecutionStatus.SUCCESS =>
            logger.info(s"Query finished with status: $status")
          case status: AutomationExecutionStatus =>
            logger.info(s"SSM Automation execution status: $status, check #$retries.")
            Thread.sleep(if (retries <= 3) 2500 else if (retries <= 10) 5000 else 15000)
        }
        retries += 1
      }
    }.map(_ => executionId)
  }
  
  def close(): Unit = ssmClient.close()

}
