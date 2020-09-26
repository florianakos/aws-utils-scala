package com.flrnks.it

import com.flrnks.utils.ssm.SsmAutomation
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll, Matchers}
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

class SsmAutomationTest extends AsyncFunSuite with LazyLogging with Matchers with BeforeAndAfterAll {
  
  private val documentName = "AWS-StartEC2Instance"
  private val parameters = mutable.HashMap(
    "InstanceId" -> List("i-0fe1f2bce829812a9").asJava,
    "AutomationAssumeRole" -> List("arn:aws:iam::{{global:ACCOUNT_ID}}:role/AutomationServiceRole").asJava).asJava

  private val ssmHelper = SsmAutomation("rivendell")
  
  override def afterAll(): Unit = {
    Try(ssmHelper.close())
  }

  test("able to start automation request") {
    ssmHelper.startDocumentWithParameters(documentName, parameters) map { execId =>
      execId.toString should fullyMatch regex """\b[0-9a-f]{8}\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\b[0-9a-f]{12}\b"""
    }
  }
}
