package com.github.pjfanning.jackson.reflect

trait ScalaReflectAnnotationIntrospectorModule extends JacksonModule {


  /**
   * Whether this introspector supports type infers for polymorphic JSONTypeInfo
   * for sealed traits/classes.
   * @since 2.18
   */
  def supportInferenceOfJsonTypeInfo(): Boolean = true

  this += { _.appendAnnotationIntrospector(
    new ScalaReflectAnnotationIntrospector(supportInferenceOfJsonTypeInfo()))
  }
}

object ScalaReflectAnnotationIntrospectorModule extends ScalaReflectAnnotationIntrospectorModule
