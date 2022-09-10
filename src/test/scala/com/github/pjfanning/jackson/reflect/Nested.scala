package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.annotation.JsonTypeInfo

object Nested {
  case class OptionSeqLong(values: Option[Seq[Long]])

  case class Car(make: String, color: Color)

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  sealed trait Color

  case object Red extends Color

  case object Green extends Color

  case object Blue extends Color
}
