package com.flrnks.it.secretsmanager

import cloud.localstack.Localstack
import cloud.localstack.docker.annotation.LocalstackDockerConfiguration
import com.flrnks.utils.JsonSerialization
import com.flrnks.utils.secretsmanager.SecretsManager
import com.flrnks.utils.kms.KmsManager
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll, Matchers}
import scala.collection.JavaConversions._
import scala.util.Try

class SecretsManagerTest extends AsyncFunSuite 
  with LazyLogging 
  with Matchers
  with JsonSerialization
  with BeforeAndAfterAll 
  with SecretsRepositoryHelper {
  
  private val localstackDocker: Localstack = Localstack.INSTANCE
  private val conf: Config = ConfigFactory.load()
  
  private val smClient = SecretsManager.newInstance(conf)
  
  private val kmsClient =  KmsManager.newInstance(conf)
  
  override def beforeAll() {
    logger.info("Starting localstack ...")
    localstackDocker.startup(LocalstackDockerConfiguration.builder()
      .environmentVariables(Map("SERVICES" -> "secretsmanager,kms", "DEBUG" -> "1"))
      .imageTag("latest").build()
    )
    logger.info("Creating KMS key ...")
    val kmsKey = kmsClient.getNewKmsKey
    logger.info("Creating dummy Secrets (plain vs json) ...")
    smClient.createSecretValue(conf.getString("secret.plain.name"), conf.getString("secret.plain.value"), kmsKey)
    smClient.createSecretValue(conf.getString("secret.json.name"), conf.getString("secret.json.value"), kmsKey)
  }
  
  override def afterAll(): Unit = {
    logger.info("Closing SecretsManager Client ...")
    Try(smClient.close())
    logger.info("Closing KMSManager Client ...")
    Try(kmsClient.close())
    logger.info("Stopping Localstack ...")
    Try(localstackDocker.stop())
  }

  test("retrieve plain secret") {
    smClient.getSecretString("localstack/secret/plain") should be ("PLAIN_SECRET_123")
  }

  test("retrieve json secret") {
    smClient.getSecretString("localstack/secret/json")
      .toObject[SecretValue].SECRET should be ("VALUE")
  }  
}
