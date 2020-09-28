package com.flrnks.utils.secretsmanager

import java.net.URI
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, ProfileCredentialsProvider, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.{DecryptionFailureException, GetSecretValueRequest, InternalServiceErrorException, InvalidParameterException, InvalidRequestException, ResourceNotFoundException}

class SecretsManager(profile: String, val apiEndpoint: Option[String]) extends LazyLogging {

    private[flrnks] val smClient = apiEndpoint match {
        case None => SecretsManagerClient.builder()
          .credentialsProvider(ProfileCredentialsProvider.create(profile))
          .region(Region.EU_WEST_1)
          .build()
        case Some(localstackEndpoint) => SecretsManagerClient.builder()
          .credentialsProvider(StaticCredentialsProvider
            .create(AwsBasicCredentials.create("foo", "bar")))
          .endpointOverride(URI.create(localstackEndpoint))
          .build()
    }
    
    private def getSecret(secretName: String): String = {
        try {
            smClient.getSecretValue(GetSecretValueRequest.builder
              .secretId(secretName).build)
              .secretString
        }
        catch {
            case e: Exception =>
                throw SecretsManagerException("The resource asked for could not be found", e.getMessage)
        }
    }

    def getSecretString(secretName: String): String = getSecret(secretName)

    def close(): Unit = smClient.close()
}

object SecretsManager {
    def newInstance(conf: Config): SecretsManager = {
        
        val apiEndpoint = conf.getString("localstack.api.endpoint")
        
        new SecretsManager(
            conf.getString("aws.profile"),
            if (apiEndpoint.trim.isEmpty) None else Some(apiEndpoint.trim)
        )
    }
}
