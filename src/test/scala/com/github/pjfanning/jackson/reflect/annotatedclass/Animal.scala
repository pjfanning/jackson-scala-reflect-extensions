package com.github.pjfanning.jackson.reflect.annotatedclass

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
sealed abstract class Animal(val animalType: String) {
  val name: String
}

class Dog(val name: String) extends Animal("Dog")

class Cat(val name: String) extends Animal("Cat")

case class PetOwner(owner: String, pet: Animal)
