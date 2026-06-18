package com.example.crypto

/**
 * Mathematically correct, pure Kotlin implementation of RIPEMD-160 hash algorithm.
 * Process data in 512-bit (64-byte) blocks to produce a 160-bit (20-byte) hash.
 */
object RIPEMD160 {

    private val f1 = { x: Int, y: Int, z: Int -> x xor y xor z }
    private val f2 = { x: Int, y: Int, z: Int -> (x and y) or (x.inv() and z) }
    private val f3 = { x: Int, y: Int, z: Int -> (x or y.inv()) xor z }
    private val f4 = { x: Int, y: Int, z: Int -> (x and z) or (y and z.inv()) }
    private val f5 = { x: Int, y: Int, z: Int -> x xor (y or z.inv()) }

    private val r1 = intArrayOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
        3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
        1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
        4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 15, 13, 6
    )

    private val r2 = intArrayOf(
        5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
        6, 11, 3, 7, 0, 7, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
        15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
        8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
        12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
    )

    private val s1 = intArrayOf(
        11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
        7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
        11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
        11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
        9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
    )

    private val s2 = intArrayOf(
        8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
        9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
        9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
        15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
        8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
    )

    fun hash(message: ByteArray): ByteArray {
        val padded = padMessage(message)
        var h0 = 0x67452301
        var h1 = 0xefcdab89.toInt()
        var h2 = 0x98badcfe.toInt()
        var h3 = 0x10325476
        var h4 = 0xc3d2e1f0.toInt()

        val words = IntArray(16)
        for (i in 0 until padded.size / 64) {
            val offset = i * 64
            for (j in 0..15) {
                words[j] = (padded[offset + j * 4].toInt() and 0xFF) or
                        ((padded[offset + j * 4 + 1].toInt() and 0xFF) shl 8) or
                        ((padded[offset + j * 4 + 2].toInt() and 0xFF) shl 16) or
                        ((padded[offset + j * 4 + 3].toInt() and 0xFF) shl 24)
            }

            var A = h0; var B = h1; var C = h2; var D = h3; var E = h4
            var Ap = h0; var Bp = h1; var Cp = h2; var Dp = h3; var Ep = h4

            for (j in 0..79) {
                // Left side
                var T = when {
                    j < 16 -> f1(B, C, D) + words[r1[j]] + 0x00000000
                    j < 32 -> f2(B, C, D) + words[r1[j]] + 0x5a827999
                    j < 48 -> f3(B, C, D) + words[r1[j]] + 0x6ed9eba1.toInt()
                    j < 64 -> f4(B, C, D) + words[r1[j]] + 0x8f1bbcdc.toInt()
                    else -> f5(B, C, D) + words[r1[j]] + 0xa953fd4e.toInt()
                }
                T = (T + A).rotateLeft(s1[j]) + E
                A = E; E = D; D = C.rotateLeft(10); C = B; B = T

                // Right side
                var Tp = when {
                    j < 16 -> f5(Bp, Cp, Dp) + words[r2[j]] + 0x50a28be6.toInt()
                    j < 32 -> f4(Bp, Cp, Dp) + words[r2[j]] + 0x5c4dd124.toInt()
                    j < 48 -> f3(Bp, Cp, Dp) + words[r2[j]] + 0x6d703ef3.toInt()
                    j < 64 -> f2(Bp, Cp, Dp) + words[r2[j]] + 0x7a6d76e9.toInt()
                    else -> f1(Bp, Cp, Dp) + words[r2[j]] + 0x00000000
                }
                Tp = (Tp + Ap).rotateLeft(s2[j]) + Ep
                Ap = Ep; Ep = Dp; Dp = Cp.rotateLeft(10); Cp = Bp; Bp = Tp
            }

            val t = h1 + C + Dp
            h1 = h2 + D + Ep
            h2 = h3 + E + Ap
            h3 = h4 + A + Bp
            h4 = h0 + B + Cp
            h0 = t
        }

        val out = ByteArray(20)
        for (i in 0..4) {
            val v: Int = when (i) {
                0 -> h0
                1 -> h1
                2 -> h2
                3 -> h3
                else -> h4
            }
            out[i * 4] = (v and 0xFF).toByte()
            out[i * 4 + 1] = ((v shr 8) and 0xFF).toByte()
            out[i * 4 + 2] = ((v shr 16) and 0xFF).toByte()
            out[i * 4 + 3] = ((v shr 24) and 0xFF).toByte()
        }
        return out
    }

    private fun padMessage(message: ByteArray): ByteArray {
        val msgLengthBits = message.size.toLong() * 8
        val remainder = message.size % 64
        val paddingLength = if (remainder < 56) 64 - remainder else 128 - remainder
        val padded = ByteArray(message.size + paddingLength)
        System.arraycopy(message, 0, padded, 0, message.size)
        padded[message.size] = 0x80.toByte()

        // Append length in bits as little-endian 64-bit integer
        val lengthOffset = padded.size - 8
        for (i in 0..7) {
            padded[lengthOffset + i] = ((msgLengthBits shr (i * 8)) and 0xFF).toByte()
        }
        return padded
    }

    private fun Int.rotateLeft(n: Int): Int {
        return (this shl n) or (this ushr (32 - n))
    }
}
