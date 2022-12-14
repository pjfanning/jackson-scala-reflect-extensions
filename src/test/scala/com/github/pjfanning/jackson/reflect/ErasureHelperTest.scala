package com.github.pjfanning.jackson.reflect

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object ErasureHelperTest {
  private trait SuperType {
    def getFoo: String
  }
}

class ErasureHelperTest extends AnyFlatSpec with Matchers {
  "ErasureHelper" should "handle MyTrait" in {
    ErasureHelper.erasedOptionalPrimitives(classOf[ErasureHelperTest.SuperType]) shouldBe empty
  }
  it should "handle OptionSeqLong" in {
    ErasureHelper.erasedOptionalPrimitives(classOf[OptionSeqLong]) shouldBe Map("values" -> classOf[Long])
  }
  it should "handle SeqOptionLong" in {
    ErasureHelper.erasedOptionalPrimitives(classOf[SeqOptionLong]) shouldBe Map("values" -> classOf[Long])
  }
  it should "handle OptionSeqOptionLong" in {
    ErasureHelper.erasedOptionalPrimitives(classOf[OptionSeqOptionLong]) shouldBe Map("values" -> classOf[Long])
  }
  it should "handle Nested.OptionSeqLong" in {
    ErasureHelper.erasedOptionalPrimitives(classOf[Nested.OptionSeqLong]) shouldBe Map("values" -> classOf[Long])
  }
}
