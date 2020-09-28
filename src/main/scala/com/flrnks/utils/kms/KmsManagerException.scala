package com.flrnks.utils.kms

case class KmsManagerException(status: String, reason: String) extends RuntimeException(reason) {

  override def toString: String = {
    s"Secrets Manager request failed with status [$status]. Reason was :: $reason ::"
  }
  
}
