package com.github.pjfanning.jackson.reflect

case class SeqOptionLong(values: Seq[Option[Long]])
case class WrappedSeqOptionLong(text: String, wrappedLongs: SeqOptionLong)
