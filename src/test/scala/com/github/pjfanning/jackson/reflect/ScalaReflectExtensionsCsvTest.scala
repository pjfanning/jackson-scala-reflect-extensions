package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.{CsvMapper, CsvSchema}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.introspect.ScalaAnnotationIntrospectorModule
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object CsvMapperScalaReflectExtensions {
  def ::(mapper: CsvMapper) = new Mixin(mapper)

  final class Mixin private[CsvMapperScalaReflectExtensions](mapper: CsvMapper)
    extends CsvMapper(mapper) with ScalaReflectExtensions
}

class ScalaReflectExtensionsCsvTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    ScalaAnnotationIntrospectorModule.clearRegisteredReferencedTypes()
  }

  override def afterEach(): Unit = {
    ScalaAnnotationIntrospectorModule.clearRegisteredReferencedTypes()
  }

  "A CsvMapper with ScalaReflectExtensions mixin" should "deserialize WrappedSeqLong" in {
    val csvMapper = newCsvMapperWithScalaReflectExtensions
    val javaType = csvMapper.constructType[OptionLong]
    val schema = csvMapper.schemaFor(javaType).withHeader()
    val w1 = OptionLong(Some(100L))
    val t1 = csvMapper.writer(schema).writeValueAsString(w1)
    val readerSchema = CsvSchema.builder().setUseHeader(true).setReorderColumns(true).build()
    val readerIterator: MappingIterator[OptionLong] = csvMapper.readerFor(javaType).`with`(readerSchema).readValues(t1)
    readerIterator.hasNext shouldEqual true
    val v1 = readerIterator.next()
    v1 shouldEqual w1
    useOptionLong(v1.valueLong) shouldEqual 200L
    readerIterator.hasNext shouldEqual false
  }

  private def newCsvMapperWithScalaReflectExtensions: CsvMapper with ScalaReflectExtensions = {
    //CsvMapper.builder().addModule(DefaultScalaModule).build() :: CsvMapperScalaReflectExtensions
    val mapper = new CsvMapper() with ScalaReflectExtensions
    mapper.registerModule(DefaultScalaModule)
    mapper
  }

  private def useOptionLong(v: Option[Long]): Long = v.map(_ * 2).getOrElse(0L)
}
