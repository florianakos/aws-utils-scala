package com.flrnks.utils

import software.amazon.awssdk.services.ssm.model.AutomationExecutionStatus

case class SsmAutomationExecutionException(status: AutomationExecutionStatus, reason: String) extends Exception {

  override def toString: String = {
    s"Automation execution failed with status [$status]. Reason: << $reason >>"
  }
}
