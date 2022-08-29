package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object CsvMapperScalaReflectExtensions {
  def ::(mapper: CsvMapper) = new Mixin(mapper)

  final class Mixin private[CsvMapperScalaReflectExtensions](mapper: CsvMapper)
    extends CsvMapper(mapper) with ScalaReflectExtensions
}

class ScalaReflectExtensionsCsvTest extends AnyFlatSpec with Matchers {
  "A CsvMapper with ScalaReflectExtensions mixin" should "deserialize WrappedSeqLong" in {
    val mapper = newCsvMapperWithScalaReflectExtensions
    val schema = mapper.schemaFor(mapper.constructType[OptionLong])
    val w1 = OptionLong(Some(100L))
    val t1 = mapper.writer(schema).writeValueAsString(w1)
    val v1 = mapper.readValue[OptionLong](t1)
    v1 shouldEqual w1
    useOptionLong(v1.valueLong) shouldEqual 200L
  }

  private def newCsvMapperWithScalaReflectExtensions: CsvMapper with ScalaReflectExtensions = {
    CsvMapper.builder().addModule(DefaultScalaModule).build() :: CsvMapperScalaReflectExtensions
  }

  private def useOptionLong(v: Option[Long]): Long = v.map(_ * 2).getOrElse(0L)
}
