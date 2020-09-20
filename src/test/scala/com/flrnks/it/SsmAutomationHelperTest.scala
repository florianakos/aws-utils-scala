package com.flrnks.it

import com.flrnks.utils.SsmAutomationHelper
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll, Matchers}
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

class SsmAutomationHelperTest extends AsyncFunSuite with Matchers with BeforeAndAfterAll {

  private val profile: String = "rivendell"
  private val credentialsProvider = ProfileCredentialsProvider.create(profile)
 
  private val ssmClient = SsmClient.builder()
    .region(Region.EU_WEST_1)
    .credentialsProvider(credentialsProvider)
    .build()
  
  private val ssmHelper = SsmAutomationHelper(profile)

  override def afterAll(): Unit = {
    Try(ssmClient.close())
    Try(ssmHelper.close())
  }

  test("able to craft start automation request") {
    val documentParameters = mutable.HashMap(
      "SourceAmiId" -> List("ami-04137ed1a354f54c4").asJava,
      "IamInstanceProfileName" -> List("ManagedInstanceProfile").asJava,
      "AutomationAssumeRole" -> List("arn:aws:iam::{{global:ACCOUNT_ID}}:role/AutomationServiceRole").asJava,
      "TargetAmiName" -> List("UpdateLinuxAmi_from_{{SourceAmiId}}_on_{{global:DATE_TIME}}").asJava,
      "InstanceType" -> List("t2.micro").asJava,
      "SubnetId" -> List("").asJava,
      "PreUpdateScript" -> List("none").asJava,
      "PostUpdateScript" -> List("none").asJava,
      "IncludePackages" -> List("all").asJava,
      "ExcludePackages" -> List("none").asJava).asJava
    ssmHelper.startDocumentWithParameters("AWS-UpdateLinuxAmi", documentParameters) map { execId =>
      execId.toString should fullyMatch regex """\b[0-9a-f]{8}\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\b[0-9a-f]{12}\b"""
    }
  }
  
}
