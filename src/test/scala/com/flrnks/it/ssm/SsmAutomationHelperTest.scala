package com.flrnks.it.ssm

import com.flrnks.utils.ssm.SsmAutomationHelper
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll, Matchers, Tag}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

class SsmAutomationHelperTest extends AsyncFunSuite with LazyLogging with Matchers with BeforeAndAfterAll {
  
  private val ssmHelper = new SsmAutomationHelper("rivendell", None)

  private val parameters = mutable.HashMap(
    "InstanceId" -> List("i-0ed4574c5ba94c877").asJava,
    "AutomationAssumeRole" -> List("arn:aws:iam::{{global:ACCOUNT_ID}}:role/AutomationServiceRole").asJava).asJava
  
  override def afterAll(): Unit = {
    Try(ssmHelper.close())
  }

  test("can run automation document to start EC2 instance", Tag("AWS")) {
    Await.result(ssmHelper.runDocumentWithParameters("AWS-StartEC2Instance", parameters),
      15.minutes) should be ()
  }

  test("can run automation document to stop EC2 instance", Tag("AWS")) {
    Await.result(ssmHelper.runDocumentWithParameters("AWS-StopEC2Instance", parameters),
      15.minutes) should be ()
  }
}
