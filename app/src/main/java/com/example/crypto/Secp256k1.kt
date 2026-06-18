package com.example.crypto

import java.math.BigInteger

/**
 * Mathematically authentic Secp256k1 elliptic curve point arithmetic.
 * Performs Point Addition, Point Doubling, and Scalar Multiplication over Finite Field F_p.
 * Curve: y^2 = x^3 + 7 (mod p)
 */
object Secp256k1 {
    val p = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16)
    val Gx = BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16)
    val Gy = BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199c47d08ffb10d4b8", 16)
    val n = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16) // order n

    data class Point(val x: BigInteger, val y: BigInteger) {
        val isInfinity: Boolean get() = this == INFINITY
    }

    val INFINITY = Point(BigInteger.ZERO, BigInteger.ZERO)
    val G = Point(Gx, Gy)

    /**
     * Elliptic curve point addition: P1 + P2
     */
    fun add(p1: Point, p2: Point): Point {
        if (p1.isInfinity) return p2
        if (p2.isInfinity) return p1
        if (p1.x == p2.x) {
            if (p1.y != p2.y) return INFINITY
            // Point doubling case: p1 == p2
            return doublePoint(p1)
        }

        val num = p2.y.subtract(p1.y).mod(p)
        val den = p2.x.subtract(p1.x).modInverse(p)
        val slope = num.multiply(den).mod(p)

        val x3 = slope.multiply(slope).subtract(p1.x).subtract(p2.x).mod(p)
        val y3 = slope.multiply(p1.x.subtract(x3)).subtract(p1.y).mod(p)
        return Point(x3, y3)
    }

    /**
     * Elliptic curve point doubling: 2 * p1
     */
    fun doublePoint(p1: Point): Point {
        if (p1.isInfinity || p1.y == BigInteger.ZERO) return INFINITY

        val num = p1.x.multiply(p1.x).multiply(BigInteger.valueOf(3)).mod(p)
        val den = p1.y.multiply(BigInteger.valueOf(2)).modInverse(p)
        val slope = num.multiply(den).mod(p)

        val x3 = slope.multiply(slope).subtract(p1.x.multiply(BigInteger.valueOf(2))).mod(p)
        val y3 = slope.multiply(p1.x.subtract(x3)).subtract(p1.y).mod(p)
        return Point(x3, y3)
    }

    /**
     * Elliptic curve point scalar multiplication: k * point (using Double-and-Add)
     */
    fun multiply(k: BigInteger, point: Point = G): Point {
        val scalar = k.mod(n)
        if (scalar == BigInteger.ZERO) return INFINITY
        var result = INFINITY
        var base = point
        var tempK = scalar

        while (tempK > BigInteger.ZERO) {
            if (tempK.testBit(0)) {
                result = add(result, base)
            }
            base = add(base, base)
            tempK = tempK.shiftRight(1)
        }
        return result
    }

    /**
     * Serializes public key to compressed format (33 bytes)
     */
    fun getCompressedPublicKey(pubKey: Point): ByteArray {
        val xBytes = pubKey.x.toByteArray()
        val x32 = ByteArray(32)
        // Right-align and copy
        val srcPos = if (xBytes.size > 32) xBytes.size - 32 else 0
        val destPos = if (xBytes.size < 32) 32 - xBytes.size else 0
        val length = minOf(32, xBytes.size)
        System.arraycopy(xBytes, srcPos, x32, destPos, length)

        val prefix = if (pubKey.y.testBit(0)) 0x03.toByte() else 0x02.toByte()
        val out = ByteArray(33)
        out[0] = prefix
        System.arraycopy(x32, 0, out, 1, 32)
        return out
    }
}
