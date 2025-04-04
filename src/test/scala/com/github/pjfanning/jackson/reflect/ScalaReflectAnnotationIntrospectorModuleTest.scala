package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.jackson.reflect.annotated.{Blue, Car, Cat, PetOwner, Red}
import com.github.pjfanning.jackson.reflect.family.{Tiger, TrackedAnimal}
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

  it should "deserialize Nested.Car with Nested.Color" in {
    // test no longer needs ScalaObjectDeserializerModule as it is now included in DefaultScalaModule since Jackson 2.16.0
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val car = Nested.Car("Samand", Nested.Red)
    val json = mapper.writeValueAsString(car)
    val car2 = mapper.readValue(json, classOf[Nested.Car])
    car2.color shouldEqual car.color
    car2.make shouldEqual car.make
  }

  it should "deserialize PetOwner with annotated.Animal" in {
    // test no longer needs ScalaObjectDeserializerModule as it is now included in DefaultScalaModule since Jackson 2.16.0
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

  it should "deserialize TrackedAnimal with family.Animal" in {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .addModule(ScalaReflectAnnotationIntrospectorModule)
      .build()
    val tracked = TrackedAnimal("Tigger", new Tiger())
    val json = mapper.writeValueAsString(tracked)
    val tracked2 = mapper.readValue(json, classOf[TrackedAnimal])
    tracked2.name shouldEqual tracked.name
    tracked2.animalType.species shouldEqual tracked.animalType.species
    tracked2.animalType.familyType shouldEqual tracked.animalType.familyType
  }

  it should "return version" in {
    ScalaReflectAnnotationIntrospectorModule.version() shouldEqual JacksonModule.version
  }
}
