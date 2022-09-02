package com.github.pjfanning.jackson.reflect

final case class NestedLong(var id: Option[Long])
final case class ParentWithNested(var nested: NestedLong)
final case class ParentWithNestedOption(var nested: Option[NestedLong])