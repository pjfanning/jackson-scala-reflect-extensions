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
}
