package com.example.crypto

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Mathematically authentic BIP-39 mnemonic generator.
 * Maps cryptographic entropy securely into structured 12-word mnemonic wallet phrases with a 4-bit checksum.
 */
object Mnemonic {

    private val SECURE_RANDOM = SecureRandom()

    /**
     * Generates a 12-word seed phrase cleanly adhering to standard BIP-39 specifications.
     * Incorporates 128-bit secure entropy mapped perfectly to 11-bit indices + 4-bit checksum.
     */
    fun generate(): String {
        val entropy = ByteArray(16)
        SECURE_RANDOM.nextBytes(entropy)

        // Calculate SHA-256 checksum of entropy
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hash = sha256.digest(entropy)
        
        // Checksum is the first 4 bits of hash (hash[0] upper 4 bits)
        val bits = BooleanArray(132)
        
        // Copy 128 entropy bits
        for (i in 0 until 16) {
            val byteVal = entropy[i].toInt() and 0xFF
            for (j in 0 until 8) {
                bits[i * 8 + j] = ((byteVal shr (7 - j)) and 1) == 1
            }
        }
        
        // Append 4 bits of checksum
        val checksumByte = hash[0].toInt() and 0xFF
        for (j in 0 until 4) {
            bits[128 + j] = ((checksumByte shr (7 - j)) and 1) == 1
        }
        
        // Group into 12 groups of 11 bits each
        val wordsList = ArrayList<String>()
        val words = Bip39Wordlist.WORDS
        for (i in 0 until 12) {
            var index = 0
            for (j in 0 until 11) {
                index = index shl 1
                if (bits[i * 11 + j]) {
                    index = index or 1
                }
            }
            wordsList.add(words[index % words.size])
        }
        
        return wordsList.joinToString(" ")
    }

    /**
     * Verifies if a given mnemonic string is structurally valid.
     */
    fun isValid(mnemonic: String): Boolean {
        val parts = mnemonic.trim().split("\\s+".toRegex())
        if (parts.size != 12) return false
        val words = Bip39Wordlist.WORDS
        return parts.all { it in words }
    }
}
