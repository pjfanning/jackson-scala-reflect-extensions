package com.github.pjfanning.jackson.reflect.family

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
sealed abstract class Animal(val familyType: String) {
  val species: String
}

abstract class CatFamily extends Animal("Cat")

class Lion extends CatFamily {
  override val species: String = "Lion"
}

class Tiger extends CatFamily {
  override val species: String = "Tiger"
}

case class TrackedAnimal(name: String, animalType: Animal)
