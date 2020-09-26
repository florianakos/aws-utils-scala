package com.flrnks.utils.ssm

import software.amazon.awssdk.services.ssm.model.AutomationExecutionStatus

case class SsmAutomationExecutionException(status: AutomationExecutionStatus, reason: String) 
  extends RuntimeException(reason) {

  override def toString: String = {
    s"Automation execution failed with status [$status]. Reason was :: $reason ::"
  }
}
