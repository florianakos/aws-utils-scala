package com.flrnks.utils.kms

import java.net.URI
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, ProfileCredentialsProvider, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.CreateKeyRequest

class KmsManager(profile: String, val apiEndpoint: Option[String]) extends LazyLogging {

    private[flrnks] val kmsClient = apiEndpoint match {
        case None => KmsClient.builder()
          .credentialsProvider(ProfileCredentialsProvider.create(profile))
          .region(Region.EU_WEST_1)
          .build()
        case Some(localstackEndpoint) => KmsClient.builder()
          .credentialsProvider(StaticCredentialsProvider
            .create(AwsBasicCredentials.create("foo", "bar")))
          .endpointOverride(URI.create(localstackEndpoint))
          .build()
    }
    
    private def createKey: String = {
        try {
            kmsClient.createKey(CreateKeyRequest.builder().build())
              .keyMetadata
              .arn
        }
        catch {
            case e: Exception =>
                throw KmsManagerException("The key could not be created!", e.getMessage)
        }
    }

    def getNewKmsKey: String = createKey

    def close(): Unit = kmsClient.close()
}

object KmsManager {
    def newInstance(conf: Config): KmsManager = {

        val apiEndpoint = conf.getString("localstack.api.endpoint")

        new KmsManager(
            conf.getString("aws.profile"),
            if (apiEndpoint.trim.isEmpty) None else Some(apiEndpoint.trim)
        )
    }
}
