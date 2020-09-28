package com.flrnks.utils.cloudformation

import java.net.URI

import com.flrnks.utils.secretsmanager.SecretsManager
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, ProfileCredentialsProvider, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.DescribeStackResourcesRequest

class CloudFormationHelper(profile: String, val apiEndpoint: Option[String]) extends LazyLogging {
  
  private val cfClient =  apiEndpoint match {
    case None => CloudFormationClient.builder()
      .credentialsProvider(ProfileCredentialsProvider.create(profile))
      .region(Region.EU_WEST_1)
      .build()
    case Some(localstackEndpoint) => CloudFormationClient.builder()
      .credentialsProvider(StaticCredentialsProvider
        .create(AwsBasicCredentials.create("foo", "bar")))
      .endpointOverride(URI.create(localstackEndpoint))
      .build()
  }
  
  def getOrchestrationDocumentName(stackName: String): String = {
    cfClient.describeStackResources(DescribeStackResourcesRequest.builder()
      .stackName(stackName)
      .logicalResourceId("OrchestrationDocument")
      .build()
    ).stackResources().get(0).physicalResourceId()
  }
  
  def close(): Unit = cfClient.close()
}

object CloudFormationHelper {
  def newInstance(conf: Config): CloudFormationHelper = {

    val apiEndpoint = conf.getString("localstack.api.endpoint")

    new CloudFormationHelper(
      conf.getString("aws.profile"),
      if (apiEndpoint.trim.isEmpty) None else Some(apiEndpoint.trim)
    )
  }
}
