package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.jackson.reflect.annotated.Red
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaReflectAnnotationIntrospectorModuleTest extends AnyFlatSpec with Matchers {
  "ScalaReflectAnnotationIntrospectorModule" should "find sub types for unannotated.Color" ignore {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    mapper.writeValueAsString(Red) shouldEqual "Red"
  }
}
