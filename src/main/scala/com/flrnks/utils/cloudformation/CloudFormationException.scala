package com.flrnks.utils.cloudformation

case class CloudFormationException(status: String, reason: String) extends RuntimeException(reason) {

  override def toString: String = {
    s"Secrets Manager request failed with status [$status]. Reason was :: $reason ::"
  }
}
