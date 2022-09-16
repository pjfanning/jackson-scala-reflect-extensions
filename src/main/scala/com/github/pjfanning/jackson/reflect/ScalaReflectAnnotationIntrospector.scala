package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.introspect.{Annotated, JacksonAnnotationIntrospector}
import com.fasterxml.jackson.databind.jsontype.NamedType
import org.slf4j.LoggerFactory

import java.util
import scala.collection.JavaConverters._
import scala.reflect.runtime.{universe => ru}
import scala.util.control.NonFatal

class ScalaReflectAnnotationIntrospector extends JacksonAnnotationIntrospector {
  private val logger = LoggerFactory.getLogger(classOf[ScalaReflectAnnotationIntrospector])

  override def version(): Version = JacksonModule.version

  override def findSubtypes(a: Annotated): util.List[NamedType] = {
    try {
      val mirror = ru.runtimeMirror(Thread.currentThread().getContextClassLoader)
      val classSymbol = mirror.classSymbol(a.getRawType)
      lazy val symbol = companionOrSelf(classSymbol)
      if (classSymbol.isJava) {
        None.orNull
      } else if (symbol.isClass) {
        val clazzes = symbol.asClass.knownDirectSubclasses
          .toSeq
          .sortBy(_.info.toString)
          .flatMap(s => if (s.isClass) Some(s.asClass) else None)
          .map(c => mirror.runtimeClass(c).getName)
        clazzes.map(cn => new NamedType(Class.forName(cn, true, Thread.currentThread().getContextClassLoader))).asJava
      } else {
        None.orNull
      }
    } catch {
      case NonFatal(t) => {
        logger.warn(s"Failed to findSubtypes in ${a.getRawType}: $t")
        None.orNull
      }
    }
  }

  private def companionOrSelf(sym: ru.Symbol): ru.Symbol = {
    if (sym.companion == ru.NoSymbol) sym else sym.companion
  }

}
