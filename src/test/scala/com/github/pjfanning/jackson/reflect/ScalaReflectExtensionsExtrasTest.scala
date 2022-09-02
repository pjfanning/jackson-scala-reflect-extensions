package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.introspect.ScalaAnnotationIntrospectorModule
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaReflectExtensionsExtrasTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    ScalaAnnotationIntrospectorModule.clearRegisteredReferencedTypes()
  }

  override def afterEach(): Unit = {
    ScalaAnnotationIntrospectorModule.clearRegisteredReferencedTypes()
  }

  "An ObjectMapper with ScalaReflectExtensions mixin" should "deserialize WrappedOptionLong" in {
    val mapper = newMapperWithScalaReflectExtensions
    val v1 = mapper.readValue[WrappedOptionLong]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionLong("myText", OptionLong(Some(151L)))
    v1.wrappedLong.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.valueLong) shouldBe 302L
  }

  it should "deserialize WrappedOptionVarLong" ignore {
    val mapper = newMapperWithScalaReflectExtensions
    val v1 = mapper.readValue[WrappedOptionVarLong]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionVarLong("myText", OptionVarLong(Some(151L)))
    v1.wrappedLong.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.valueLong) shouldBe 302L
  }

  it should "deserialize WrappedOptionOptionVarLong" ignore {
    val mapper = newMapperWithScalaReflectExtensions
    val v1 = mapper.readValue[WrappedOptionOptionVarLong]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionOptionVarLong("myText", Some(OptionVarLong(Some(151L))))
    v1.wrappedLong shouldBe defined
    v1.wrappedLong.get.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.get.valueLong) shouldBe 302L
  }

  it should "deserialize WrappedSeqLong" ignore {
    val mapper = newMapperWithScalaReflectExtensions
    val w1 = WrappedSeqLong("myText", SeqLong(Seq(100L, 100000000000000L)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[WrappedSeqLong](t1)
    v1 shouldEqual w1
    useSeqLong(v1.wrappedLongs.longs) shouldEqual w1.wrappedLongs.longs.sum
  }

  it should "deserialize WrappedSeqLong with old style mix-in" in {
    val mapper = new ObjectMapper with ScalaReflectExtensions
    mapper.registerModule(DefaultScalaModule)
    val w1 = WrappedSeqLong("myText", SeqLong(Seq(100L, 100000000000000L)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[WrappedSeqLong](t1)
    v1 shouldEqual w1
    useSeqLong(v1.wrappedLongs.longs) shouldEqual w1.wrappedLongs.longs.sum
  }

  it should "deserialize WrappedSeqOptionLong" ignore {
    val mapper = newMapperWithScalaReflectExtensions
    val w1 = WrappedSeqOptionLong("myText", SeqOptionLong(Seq(Some(100L), Some(100000000000000L), None)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[WrappedSeqOptionLong](t1)
    v1 shouldEqual w1
    v1.wrappedLongs.values.map(useOptionLong).sum shouldEqual w1.wrappedLongs.values.map(useOptionLong).sum
  }

  it should "deserialize WrappedOptionSeqLong" in {
    val mapper = newMapperWithScalaReflectExtensions
    val w1 = WrappedOptionSeqLong("myText", OptionSeqLong(Some(Seq(100L, 100000000000000L))))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[WrappedOptionSeqLong](t1)
    v1 shouldEqual w1
    v1.wrappedLongs.values.getOrElse(Seq.empty).sum shouldEqual w1.wrappedLongs.values.getOrElse(Seq.empty).sum
  }

  it should "deserialize OptionSeqOptionLong" in {
    val mapper = newMapperWithScalaReflectExtensions
    val w1 = OptionSeqOptionLong(Some(Seq(Some(100L), None)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[OptionSeqOptionLong](t1)
    v1 shouldEqual w1
    v1.values.get.flatten.sum shouldEqual w1.values.get.flatten.sum
  }

  private def newMapperWithScalaReflectExtensions: ObjectMapper with ScalaReflectExtensions = {
    JsonMapper.builder().addModule(DefaultScalaModule).build() :: ScalaReflectExtensions
  }

  private def useOptionLong(v: Option[Long]): Long = v.map(_ * 2).getOrElse(0L)
  private def useSeqLong(longs: Seq[Long]): Long = longs.sum
}
