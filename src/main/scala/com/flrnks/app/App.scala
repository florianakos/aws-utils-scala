package com.flrnks.app

import com.flrnks.utils.SsmAutomationHelper
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt


object App extends App with LazyLogging  {

  private val startTime = System.currentTimeMillis
  private val profile = "rivendell"
  private val startInstanceDocument = "AWS-StartEC2Instance"
  private val stopInstanceDocument = "AWS-StopEC2Instance"
  private val terminateInstanceDocument = "AWS-TerminateEC2Instance"
  private val instanceOperationParams = mutable.HashMap(
    "InstanceId" -> List("i-00db7f7ff518d6473").asJavaCollection,
    "AutomationAssumeRole" -> List("arn:aws:iam::461491260158:role/SSMTestRole").asJavaCollection).asJava

  logger.info(s"Booting SSM Automation Helper with arguments: ${args.foldLeft("")((a, b) => a + b)}")
  
  logger.info(s"${args.foreach(println)}")

  val ssmHelper = SsmAutomationHelper(profile)
  
//  logger.info(s"Starting document: $startInstanceDocument")
//  Await.result(
//    SsmAutomationHelper(profile).startDocumentWithParameters(startInstanceDocument, instanceOperationParams), 
//    10.minutes
//  )

//  logger.info(s"Starting document: $stopInstanceDocument")
//  Await.result(
//    SsmAutomationHelper(profile).startDocumentWithParameters(stopInstanceDocument, instanceOperationParams),
//    10.minutes
//  )

//  logger.info(s"Starting document: $terminateInstanceDocument")
//  Await.result(
//    SsmAutomationHelper(profile).startDocumentWithParameters(terminateInstanceDocument, instanceOperationParams),
//    10.minutes
//  )
  logger.info(s"Execution took ${(System.currentTimeMillis - startTime) / 1000} seconds. Shutting down...!")
}
