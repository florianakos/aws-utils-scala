package com.flrnks.it.secretsmanager

import com.flrnks.utils.secretsmanager.SecretsManager
import com.typesafe.scalalogging.LazyLogging
import software.amazon.awssdk.services.secretsmanager.model.{CreateSecretRequest, SecretsManagerException}

trait SecretsRepositoryHelper {
  
  implicit class SecretsRepositoryHelper(secretsRepo: SecretsManager) extends LazyLogging{
    
    def createSecretValue(secretName: String, secretString: String, kmsKeyId: String): Unit = {
      try {
        secretsRepo.smClient.createSecret(CreateSecretRequest.builder()
          .name(secretName)
          .secretString(secretString)
          .kmsKeyId(kmsKeyId)
          .build()
        )
      } catch {
        case e: SecretsManagerException =>
          logger.error("Could not create the secret with value as requested...", e)
          throw e
      }
    }
    
  }
  
}
