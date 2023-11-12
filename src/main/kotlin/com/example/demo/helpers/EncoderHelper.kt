package com.example.demo.helpers

import java.nio.ByteBuffer
import java.util.*

object EncoderHelper {
	fun decode(encodedText: String): String {
		val decoder = Base64.getDecoder()
		return String(decoder.decode(encodedText))
	}

	fun encode(text: String): String {
		val encoder = Base64.getEncoder()
		return encoder.encodeToString(text.toByteArray())
	}
}