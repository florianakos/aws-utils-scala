package com.flrnks.app

import java.util
import java.util.concurrent.Callable
import com.flrnks.utils.SsmAutomationHelper
import com.typesafe.scalalogging.LazyLogging
import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import picocli.CommandLine
import picocli.CommandLine.{Command, Option, Parameters}


@Command(name = "SsmHelperApp",  version = Array("SsmHelper Scala App v0.0.1"),  mixinStandardHelpOptions = true,
  description = Array("Scala CLI app that can run SSM Automation documents"))
class SsmCliParser extends Callable[Unit] with LazyLogging {

  @Option(names = Array("-P", "--profile"),
    description = Array("Name of the AWS profile to use"))
  private var profileName = new String

  @Option(names = Array("-D", "--document"),
    description = Array("Name of the SSM Automation document to execute"))
  private var documentName = new String

  @Parameters(index = "0..*", 
    arity = "0..*", 
    paramLabel = "<parameter1=value1> <parameter2=value2>",
    description = Array("Key=Value parameters to pass to AWS SSM Document as document inputs"))
  private val parameters: util.ArrayList[String] = null

  private def process(params: util.ArrayList[String]): util.Map[String, util.List[String]] = {
    params.asScala
      .map(_.split('='))
      .collect { case Array(key, value) => key -> value }
      .groupBy(_._1)
      .mapValues(_.map(_._2).asJava).asJava
  }
  
  def call(): Unit = {
    val startTime = System.currentTimeMillis
    
    if (profileName == "" | documentName == "") {
      logger.error("ERROR: missing profile(P) and/or document(D)")
      sys.exit(1)
    }
    
    try {
      Await.result(SsmAutomationHelper(profileName).startDocumentWithParameters(documentName, process(parameters)), 10.minutes)
    } catch {
      case e: Exception =>
        logger.error(s"SSM Execution failed with error message: ${e.getMessage}")
        throw e
    }
    
    logger.info(s"SSM execution run took ${(System.currentTimeMillis - startTime) / 1000} seconds")
  }
}


object App extends App with LazyLogging  {

  System.exit(new CommandLine(new SsmCliParser()).execute(args: _*))
  
}
