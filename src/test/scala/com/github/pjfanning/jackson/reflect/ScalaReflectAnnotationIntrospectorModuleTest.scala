package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.deser.ScalaObjectDeserializerModule
import com.github.pjfanning.jackson.reflect.annotated.{Blue, Car, Cat, PetOwner, Red}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaReflectAnnotationIntrospectorModuleTest extends AnyFlatSpec with Matchers {
  "ScalaReflectAnnotationIntrospectorModule" should "serialize Car with annotated.Color" in {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val car = Car("Samand", Blue)
    mapper.writeValueAsString(car) shouldEqual """{"make":"Samand","color":{"type":"Blue$"}}"""
  }

  it should "deserialize Car with annotated.Color" in {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaObjectDeserializerModule) //this non-default module prevents duplicate scala objects being created
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val car = Car("Samand", Red)
    val json = mapper.writeValueAsString(car)
    val car2 = mapper.readValue(json, classOf[Car])
    car2.color shouldEqual car.color
    car2.make shouldEqual car.make
  }

  it should "deserialize PetOwner with annotated.Animal" in {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaObjectDeserializerModule) //this non-default module prevents duplicate scala objects being created
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
