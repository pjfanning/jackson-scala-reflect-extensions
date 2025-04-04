package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.introspect.{Annotated, AnnotatedClass, AnnotatedMethod, JacksonAnnotationIntrospector}
import com.fasterxml.jackson.databind.jsontype.NamedType
import org.slf4j.LoggerFactory

import java.util
import scala.collection.JavaConverters._
import scala.reflect.runtime.universe.ClassSymbol
import scala.reflect.runtime.{universe => ru}
import scala.util.control.NonFatal

class ScalaReflectAnnotationIntrospector extends JacksonAnnotationIntrospector {
  private val logger = LoggerFactory.getLogger(classOf[ScalaReflectAnnotationIntrospector])

  override def version(): Version = JacksonModule.version

  override def findSubtypes(ann: Annotated): util.List[NamedType] = ann match {
    case ac: AnnotatedClass =>
      try {
        val mirror = ru.runtimeMirror(Thread.currentThread().getContextClassLoader)
        getNamedTypes(mirror.classSymbol(ac.getRawType), mirror)
      } catch {
        case NonFatal(t) => {
          logger.warn(s"Failed to findSubtypes in ${ac.getRawType}: $t")
          None.orNull
        }
        case error: NoClassDefFoundError => {
          logger.warn(s"Failed to findSubtypes in ${ac.getRawType}: $error")
          None.orNull
        }
      }
    case _ => None.orNull
  }

  private def getNamedTypes(classSymbol: ClassSymbol, mirror: ru.Mirror): util.List[NamedType] = {
    if (classSymbol.isJava) {
      None.orNull
    } else {
      val symbol = companionOrSelf(classSymbol)
      if (symbol.isClass) {
        val classes = symbol.asClass.knownDirectSubclasses
          .toSeq
          .sortBy(_.info.toString)
          .flatMap(s => if (s.isClass) Some(s.asClass) else None)
          .map(c => mirror.runtimeClass(c))
        classes.map(c => new NamedType(c)).asJava
      } else {
        None.orNull
      }
    }
  }

  private def companionOrSelf(sym: ru.Symbol): ru.Symbol = {
    if (sym.companion == ru.NoSymbol) sym else sym.companion
  }

}
