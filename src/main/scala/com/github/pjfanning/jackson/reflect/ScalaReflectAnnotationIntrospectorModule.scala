package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.core.Version

trait ScalaReflectAnnotationIntrospectorModule extends JacksonModule {
  this += { _.appendAnnotationIntrospector(new ScalaReflectAnnotationIntrospector) }
}

object ScalaReflectAnnotationIntrospectorModule extends ScalaReflectAnnotationIntrospectorModule
