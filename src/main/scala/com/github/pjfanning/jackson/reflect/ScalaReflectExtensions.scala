package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.core.{JsonParser, TreeNode}
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.{JavaType, MappingIterator, ObjectMapper, ObjectReader, ObjectWriter}
import com.fasterxml.jackson.module.scala.JavaTypeable
import com.fasterxml.jackson.module.scala.introspect.{BeanIntrospector, PropertyDescriptor, ScalaAnnotationIntrospectorModule}
import com.fasterxml.jackson.databind.`type`.{ArrayType, CollectionLikeType, ReferenceType}
import com.fasterxml.jackson.module.scala.util.ClassW

import java.io.{File, InputStream, Reader}
import java.lang.reflect.Method
import java.net.URL
import scala.reflect.ClassTag

object ScalaReflectExtensions {
  def ::(o: JsonMapper) = new Mixin(o)

  final class Mixin private[ScalaReflectExtensions](mapper: JsonMapper)
    extends JsonMapper(mapper.rebuild().build()) with ScalaReflectExtensions

  def registerInnerTypes(cls: Class[_]): Unit = {
    registerInnerTypes(cls, Set.empty)
  }

  private def registerInnerTypes(cls: Class[_], registered: Set[Class[_]]): Unit = {
    if (cls != null && !registered.contains(cls) && ClassW(cls).extendsScalaClass) {
      ErasureHelper.erasedOptionalPrimitives(cls).foreach { case (name, valueClass) =>
        ScalaAnnotationIntrospectorModule.registerReferencedValueType(cls, name, valueClass)
      }
      val beanDesc = BeanIntrospector(cls)
      val newSet = registered + cls
      beanDesc.properties.foreach { prop =>
        classForProperty(prop).foreach { propClass =>
          registerInnerTypes(propClass, newSet)
        }
      }
    }
  }

  private def classForProperty(prop: PropertyDescriptor): Option[Class[_]] = {
    prop.field match {
      case Some(field) => Some(field.getType)
      case _ => {
        classForProperty(prop.beanSetter) match {
          case Some(cls) => Some(cls)
          case _ => {
            classForProperty(prop.setter) match {
              case Some(cls) => Some(cls)
              case _ => {
                prop.param.flatMap { param =>
                  val paramTypes = param.constructor.getParameterTypes
                  if (param.index >= 0 && paramTypes.size > param.index) {
                    Some(paramTypes(param.index))
                  } else {
                    None
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private def classForProperty(method: Option[Method]): Option[Class[_]] = {
    method.flatMap { m =>
      m.getParameterTypes.headOption
    }
  }
}

trait ScalaReflectExtensions {
  self: ObjectMapper =>

  private val registeredClasses = scala.collection.mutable.HashSet[Class[_]]()

  /**
   * Method to deserialize JSON content into a Java type, reference
   * to which is passed as argument. Type is passed using so-called
   * "super type token" (see )
   * and specifically needs to be used if the root type is a
   * parameterized (generic) container type.
   */
  def readValue[T: JavaTypeable](jp: JsonParser): T = {
    readValue(jp, constructType[T])
  }


  /**
   * Method for reading sequence of Objects from parser stream.
   * Sequence can be either root-level "unwrapped" sequence (without surrounding
   * JSON array), or a sequence contained in a JSON Array.
   * In either case [[com.fasterxml.jackson.core.JsonParser]] must point to the first token of
   * the first element, OR not point to any token (in which case it is advanced
   * to the next token). This means, specifically, that for wrapped sequences,
   * parser MUST NOT point to the surrounding <code>START_ARRAY</code> but rather
   * to the token following it.
   * <p>
   * Note that [[com.fasterxml.jackson.databind.ObjectReader]] has more complete set of variants.
   */
  def readValues[T: JavaTypeable](jp: JsonParser): MappingIterator[T] = {
    readValues(jp, constructType[T])
  }

  def treeToValue[T: JavaTypeable](n: TreeNode): T = {
    treeToValue(n, constructType[T])
  }

  def readValue[T: JavaTypeable](src: File): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](src: URL): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](content: String): T = {
    readValue(content, constructType[T])
  }

  def readValue[T: JavaTypeable](src: Reader): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](src: InputStream): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](src: Array[Byte]): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](src: Array[Byte], offset: Int, len: Int): T = {
    readValue(src, offset, len, constructType[T])
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: File): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: URL): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, content: String): T = {
    objectReaderFor(valueToUpdate).readValue(content)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: Reader): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: InputStream): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: Array[Byte]): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: Array[Byte], offset: Int, len: Int): T = {
    objectReaderFor(valueToUpdate).readValue(src, offset, len)
  }

  private def objectReaderFor[T: JavaTypeable](valueToUpdate: T): ObjectReader = {
    readerForUpdating(valueToUpdate).forType(constructType[T])
  }

  /*
   **********************************************************
   * Extended Public API: constructing ObjectWriters
   * for more advanced configuration
   **********************************************************
   */

  /**
   * Factory method for constructing [[com.fasterxml.jackson.databind.ObjectWriter]] that will
   * serialize objects using specified JSON View (filter).
   */
  def writerWithView[T: ClassTag]: ObjectWriter = {
    writerWithView(classFor[T])
  }

  /**
   * Factory method for constructing {@link com.fasterxml.jackson.databind.ObjectWriter} that will
   * serialize objects using specified root type, instead of actual
   * runtime type of value. Type must be a super-type of runtime type.
   * <p>
   * Main reason for using this method is performance, as writer is able
   * to pre-fetch serializer to use before write, and if writer is used
   * more than once this avoids addition per-value serializer lookups.
   */
  def writerFor[T: JavaTypeable]: ObjectWriter = {
    writerFor(constructType[T])
  }

  /*
   **********************************************************
   * Extended Public API: constructing ObjectReaders
   * for more advanced configuration
   **********************************************************
   */

  /**
   * Factory method for constructing [[com.fasterxml.jackson.databind.ObjectReader]] that will
   * read or update instances of specified type
   */
  def readerFor[T: JavaTypeable]: ObjectReader = {
    readerFor(constructType[T])
  }

  /**
   * Factory method for constructing [[com.fasterxml.jackson.databind.ObjectReader]] that will
   * deserialize objects using specified JSON View (filter).
   */
  def readerWithView[T: ClassTag]: ObjectReader = {
    readerWithView(classFor[T])
  }

  /*
   **********************************************************
   * Extended Public API: convenience type conversion
   **********************************************************
   */

  /**
   * Convenience method for doing two-step conversion from given value, into
   * instance of given value type. This is functionality equivalent to first
   * serializing given value into JSON, then binding JSON data into value
   * of given type, but may be executed without fully serializing into
   * JSON. Same converters (serializers, deserializers) will be used as for
   * data binding, meaning same object mapper configuration works.
   *
   * @throws IllegalArgumentException If conversion fails due to incompatible type;
   *                                  if so, root cause will contain underlying checked exception data binding
   *                                  functionality threw
   */
  def convertValue[T: JavaTypeable](fromValue: Any): T = {
    convertValue(fromValue, constructType[T])
  }

  def constructType[T: JavaTypeable]: JavaType = {
    val javaType = implicitly[JavaTypeable[T]].asJavaType(getTypeFactory)
    javaType match {
      case rt: ReferenceType =>
      case at: ArrayType =>
      case ct: CollectionLikeType =>
      case _ => {
        val clazz = javaType.getRawClass
        if (!registeredClasses.contains(clazz)) {
          ScalaReflectExtensions.registerInnerTypes(clazz)
        }
      }
    }
    javaType
  }

  private def classFor[T: ClassTag]: Class[T] = {
    implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
  }
}
