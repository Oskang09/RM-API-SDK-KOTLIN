package com.github.revenuemonster.util

import java.time.Instant
import java.util.Random
import kotlin.math.floor

object Random {
    private val random: Random = Random(Instant.now().epochSecond)

    fun generateNonce(size: Int): String {
        val builder = StringBuilder()
        var ch: Char
        for (i in 0 until size) {
            ch = floor(26 * random.nextDouble() + 65).toInt().toChar()
            builder.append(ch)
        }
        return builder.toString()
    }
}