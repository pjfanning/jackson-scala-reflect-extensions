package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.jackson.reflect.annotatedclass._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AnnotatedClassTypeTest extends AnyFlatSpec with Matchers {
  "ScalaReflectAnnotationIntrospectorModule" should "deserialize PetOwner with annotatedclass.Animal" in {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val petOwner = PetOwner("Seoirse", new Cat("Trixie"))
    val json = mapper.writeValueAsString(petOwner)
    val petOwner2 = mapper.readValue(json, classOf[PetOwner])
    petOwner2.owner shouldEqual petOwner.owner
    petOwner2.pet.animalType shouldEqual petOwner.pet.animalType
    petOwner2.pet.name shouldEqual petOwner.pet.name
  }
}
