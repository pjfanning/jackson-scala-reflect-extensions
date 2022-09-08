package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver
import com.fasterxml.jackson.databind.json.JsonMapper
import com.github.pjfanning.jackson.reflect.annotated.{Blue, Green, Red}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.JavaConverters._

class ScalaReflectAnnotationIntrospectorTest extends AnyFlatSpec with Matchers {
  "ScalaReflectAnnotationIntrospector" should "find sub types for unannotated.Color" in {
    val introspector = new ScalaReflectAnnotationIntrospector
    val mapper = JsonMapper.builder().build()
    val colorType = mapper.constructType(classOf[annotated.Color])
    val annotatedColor = AnnotatedClassResolver.resolve(
      mapper.getDeserializationConfig, colorType, mapper.getDeserializationConfig)
    val subtypes = introspector.findSubtypes(annotatedColor).asScala.toSeq.map(_.getType)
    subtypes should have size 3
    subtypes(0) shouldEqual Blue.getClass
    subtypes(1) shouldEqual Green.getClass
    subtypes(2) shouldEqual Red.getClass
  }

  it should "return version" in {
    val introspector = new ScalaReflectAnnotationIntrospector
    introspector.version() shouldEqual JacksonModule.version
  }
}
