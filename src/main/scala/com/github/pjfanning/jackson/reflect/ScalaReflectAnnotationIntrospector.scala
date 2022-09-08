package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.introspect.{Annotated, JacksonAnnotationIntrospector}
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.module.scala.util.ClassW

import java.util
import scala.collection.JavaConverters._
import scala.reflect.runtime.{universe => ru}

class ScalaReflectAnnotationIntrospector extends JacksonAnnotationIntrospector {
  override def version(): Version = JacksonModule.version

  override def findSubtypes(a: Annotated): util.List[NamedType] = {
    if (ClassW(a.getRawType).extendsScalaClass) {
      val mirror = ru.runtimeMirror(Thread.currentThread().getContextClassLoader)
      val moduleSymbol = mirror.moduleSymbol(a.getRawType)
      moduleSymbol.typeSignature.typeSymbol.asClass.knownDirectSubclasses
        .map(_.asClass.getClass)
        .map(new NamedType(_))
        .toSeq
        .asJava
    } else {
      None.orNull
    }
  }
}
