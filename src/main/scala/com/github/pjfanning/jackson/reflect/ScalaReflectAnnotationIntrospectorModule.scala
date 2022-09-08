package com.github.pjfanning.jackson.reflect

trait ScalaReflectAnnotationIntrospectorModule extends JacksonModule {
  this += { _.appendAnnotationIntrospector(new ScalaReflectAnnotationIntrospector) }
}

object ScalaReflectAnnotationIntrospectorModule extends ScalaReflectAnnotationIntrospectorModule
