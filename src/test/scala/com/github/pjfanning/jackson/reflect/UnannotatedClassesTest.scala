package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.jackson.reflect.unannotated._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UnannotatedClassesTest extends AnyFlatSpec with Matchers {
  "ScalaReflectAnnotationIntrospectorModule" should "serialize Car with unannotated.Color" in {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val car = Car("Samand", Blue)
    mapper.writeValueAsString(car) shouldEqual """{"make":"Samand","color":{"@type":"Blue$"}}"""
  }

  it should "deserialize Car with unannotated.Color" in {
    // test no longer needs ScalaObjectDeserializerModule as it is now included in DefaultScalaModule since Jackson 2.16.0
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val car = Car("Samand", Red)
    val json = mapper.writeValueAsString(car)
    val car2 = mapper.readValue(json, classOf[Car])
    car2.color shouldEqual car.color
    car2.make shouldEqual car.make
  }

  it should "fail to deserialize Car with unannotated.Color (inference disabled)" in {
    val introspectorModule = new ScalaReflectAnnotationIntrospectorModule {
      override def supportInferenceOfJsonTypeInfo(): Boolean = false
    }
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(introspectorModule)
      .build()
    val car = Car("Samand", Red)
    val json = mapper.writeValueAsString(car)
    // we expect an InvalidDefinitionException because the unannotated.Color has no JsonTypeInfo annotation
    intercept[InvalidDefinitionException] {
      mapper.readValue(json, classOf[Car])
    }
  }

  it should "serialize unannotated.Country" in {
    // we do need infer a JsonTypeInfo annotation for the unannotated.Country class
    // because the class itself can be serialized in its own right
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val country = new Country("Andorra")
    mapper.writeValueAsString(country) shouldEqual """{"name":"Andorra"}"""
  }
}
