package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.jackson.reflect.annotated.{Blue, Car}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaReflectAnnotationIntrospectorModuleTest extends AnyFlatSpec with Matchers {
  "ScalaReflectAnnotationIntrospectorModule" should "serialize Car with annotated.Color" in {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      //.addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val car = Car("Samand", Blue)
    mapper.writeValueAsString(car) shouldEqual """{"make":"Samand","color":{"type":"Blue$"}}"""
  }
}
