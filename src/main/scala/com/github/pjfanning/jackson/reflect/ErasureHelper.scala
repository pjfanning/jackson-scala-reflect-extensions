package com.github.pjfanning.jackson.reflect

import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.reflect.runtime.universe
import scala.util.Try
import scala.util.control.NonFatal

/**
 * Based on the equivalent class in https://github.com/swagger-akka-http/swagger-scala-module.
 * That code was largely written by Sam Theisens.
 */
private[reflect] object ErasureHelper {
  private val logger = LoggerFactory.getLogger(ErasureHelper.getClass)

  def erasedOptionalPrimitives(cls: Class[_]): Map[String, Class[_]] = {
    try {
      val mirror = universe.runtimeMirror(cls.getClassLoader)
      val properties = getReflectProps(mirror, cls)

      properties.flatMap { prop =>
        val maybeClass = getPropClass(mirror, prop)
        val mapping: Option[(String, Class[_])] = maybeClass.flatMap { innerClass =>
          if (innerClass.isPrimitive) {
            Some(prop.name.toString.trim -> innerClass)
          } else {
            None
          }
        }
        mapping
      }.toMap
    } catch {
      case NonFatal(t) => {
        if (logger.isDebugEnabled) {
          //use this form because of Scala 2.11 & 2.12 compile issue
          logger.debug(s"Unable to get type info ${Option(cls.getName).getOrElse("null")}", t)
        } else {
          logger.info("Unable to get type info {}", Option(cls.getName).getOrElse("null"))
        }
        Map.empty[String, Class[_]]
      }
    }
  }

  def getParamReferenceType(cls: Class[_], propertyName: String): Option[Class[_]] = {
    try {
      val mirror = universe.runtimeMirror(cls.getClassLoader)
      val properties = getReflectProps(mirror, cls)
      val propOpt = properties.find { prop: universe.Symbol =>
        prop.name.toString.trim == propertyName
      }
      propOpt.flatMap { prop => getPropClass(mirror, prop) }
    } catch {
      case NonFatal(t) => {
        if (logger.isDebugEnabled) {
          //use this form because of Scala 2.11 & 2.12 compile issue
          logger.debug(s"Unable to get type info ${Option(cls.getName).getOrElse("null")}", t)
        } else {
          logger.info("Unable to get type info {}", Option(cls.getName).getOrElse("null"))
        }
        None
      }
    }
  }

  private def getReflectProps(mirror: universe.Mirror, cls: Class[_]): Iterable[universe.Symbol] = {
    val moduleSymbol = mirror.moduleSymbol(Class.forName(cls.getName, true, Thread.currentThread.getContextClassLoader))
    val ConstructorName = "apply"
    val companion: universe.Symbol = moduleSymbol.typeSignature.member(universe.TermName(ConstructorName))
    Try(companion.asTerm.alternatives.head.asMethod.paramLists.flatten).getOrElse {
      val sym = mirror.staticClass(cls.getName)
      sym.selfType.members
        .filterNot(_.isMethod)
        .filterNot(_.isClass)
    }
  }

  private def getPropClass(mirror: universe.Mirror, prop: universe.Symbol): Option[Class[_]] = {
    prop.typeSignature.typeArgs.headOption.flatMap { signature =>
      if (signature.typeSymbol.isClass) {
        signature.typeArgs.headOption match {
          case Some(typeArg) => Option(mirror.runtimeClass(nestedTypeArg(typeArg)))
          case _ => Option(mirror.runtimeClass(signature))
        }
      } else {
        None
      }
    }
  }

  @tailrec
  private def nestedTypeArg(typeArg: universe.Type): universe.Type = {
    typeArg.typeArgs.headOption match {
      case Some(innerArg) => nestedTypeArg(innerArg)
      case _ => typeArg
    }
  }
}
