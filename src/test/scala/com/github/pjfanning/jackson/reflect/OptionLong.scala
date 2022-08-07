package com.github.pjfanning.jackson.reflect

case class OptionLong(valueLong: Option[Long])
case class WrappedOptionLong(text: String, wrappedLong: OptionLong)
