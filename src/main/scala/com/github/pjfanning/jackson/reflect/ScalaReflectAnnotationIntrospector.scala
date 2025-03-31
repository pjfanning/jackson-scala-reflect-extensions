package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.{Annotated, AnnotatedClass, AnnotatedMember, JacksonAnnotationIntrospector}
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.module.scala.util.ClassW
import org.slf4j.LoggerFactory

import java.util
import scala.collection.JavaConverters._
import scala.reflect.runtime.universe.ClassSymbol
import scala.reflect.runtime.{universe => ru}
import scala.util.control.NonFatal

class ScalaReflectAnnotationIntrospector extends JacksonAnnotationIntrospector {
  private val logger = LoggerFactory.getLogger(classOf[ScalaReflectAnnotationIntrospector])

  override def version(): Version = JacksonModule.version

  override def findPolymorphicTypeInfo(config: MapperConfig[_], ann: Annotated): JsonTypeInfo.Value = {
    ann match {
      case ac: AnnotatedClass if isScala(ac) =>
        val t = _findAnnotation(ac, classOf[JsonTypeInfo])
        if (t == null) {
          try {
            val mirror = ru.runtimeMirror(Thread.currentThread().getContextClassLoader)
            val classSymbol = mirror.classSymbol(ac.getRawType)
            lazy val symbol = companionOrSelf(classSymbol)
            if (classSymbol.isJava) {
              None.orNull
            } else if (symbol.isClass) {
              val clz = symbol.asClass
              // It is possible that other classes might have subclasses
              // but the risk is that someone wants to serialize the base class
              // without including type information -- at the moment, users will still
              // need to add @JsonTypeInfo to the base class
              val isLikelyBaseClass = clz.isAbstract || clz.isTrait || clz.isSealed
              if (isLikelyBaseClass && clz.knownDirectSubclasses.nonEmpty) {
                JsonTypeInfo.Value.construct(JsonTypeInfo.Id.NAME, JsonTypeInfo.As.PROPERTY,
                  None.orNull, None.orNull, true, None.orNull)
              } else {
                None.orNull
              }
            } else {
              None.orNull
            }
          } catch {
            case NonFatal(t) => {
              logger.warn(s"Failed to findPolymorphicTypeInfo in ${ac.getRawType}: $t")
              None.orNull
            }
            case error: NoClassDefFoundError => {
              logger.warn(s"Failed to findPolymorphicTypeInfo in ${ac.getRawType}: $error")
              None.orNull
            }
          }
        } else {
          JsonTypeInfo.Value.from(t)
        }
      case _ => None.orNull
    }
  }

  override def findSubtypes(ann: Annotated): util.List[NamedType] = ann match {
    case ac: AnnotatedClass if isScala(ac) =>
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

  private def isScala(ann: Annotated): Boolean = {
    val cw = ClassW(ann.getRawType)
    cw.extendsScalaClass(false) || cw.hasSignature
  }

}
