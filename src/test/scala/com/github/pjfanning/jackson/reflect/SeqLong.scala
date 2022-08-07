package com.github.pjfanning.jackson.reflect

case class SeqLong(longs: Seq[Long])
case class WrappedSeqLong(text: String, wrappedLongs: SeqLong)
