package com.flrnks.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

trait JsonSerialization {

    implicit class JsonString(json: String) {
        def toObject[T](implicit m: Manifest[T]): T = Mapper.mapper.readValue[T](json)
    }
}

private object Mapper{
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
}
