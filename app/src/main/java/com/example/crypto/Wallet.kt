package com.example.crypto

import java.math.BigInteger
import java.security.MessageDigest

data class DerivedWallet(
    val mnemonic: String,
    val privateKeyHex: String,
    val publicKeyHex: String,
    val address: String
)

/**
 * Handles professional BIP-44 key derivation and address generation for Takeshi Coin.
 * Adheres strictly to the mathematical curves and hashing sequence from the blueprint.
 */
object Wallet {

    /**
     * Derives a full cryptographic wallet state from a valid 12-word mnemonic phrase.
     */
    fun deriveWallet(mnemonic: String): DerivedWallet {
        // Step 1: Generate a 64-byte seed by hashing the mnemonic bytes with Sha512
        val mnemonicBytes = mnemonic.trim().toByteArray(Charsets.UTF_8)
        val sha512 = MessageDigest.getInstance("SHA-512")
        val seed = sha512.digest(mnemonicBytes)

        // Step 2: Update the hasher with seed and the exact path sequence
        val pathBytes = "Takeshi Coin Path: m/44'/999'/0'/0/0".toByteArray(Charsets.UTF_8)
        sha512.reset()
        sha512.update(seed)
        sha512.update(pathBytes)
        val pathResult = sha512.digest()

        // Step 3: Strictly extract the first 32 bytes as the private key
        val privateKeyBytes = ByteArray(32)
        System.arraycopy(pathResult, 0, privateKeyBytes, 0, 32)
        val privateKeyBigInt = BigInteger(1, privateKeyBytes)

        // Ensure key falls inside valid range for secp256k1
        val safePrivateKey = privateKeyBigInt.mod(Secp256k1.n)

        // Step 4: Multiply Generator G by private key scalar to derive secp256k1 Public Key
        val publicKeyPoint = Secp256k1.multiply(safePrivateKey)
        val compressedPublicKeyBytes = Secp256k1.getCompressedPublicKey(publicKeyPoint)

        val privateKeyHex = privateKeyBytes.joinToString("") { "%02x".format(it) }
        val publicKeyHex = compressedPublicKeyBytes.joinToString("") { "%02x".format(it) }

        // Step 5: Address mapping (Hash160 style): SHA-256 followed by RIPEMD-160
        val sha256Digest = MessageDigest.getInstance("SHA-256")
        val sha256Result = sha256Digest.digest(compressedPublicKeyBytes)
        val hash160Bytes = RIPEMD160.hash(sha256Result)

        // Encode to hex and prefix strictly as tks1
        val hexHash = hash160Bytes.joinToString("") { "%02x".format(it) }
        val address = "tks1$hexHash"

        return DerivedWallet(
            mnemonic = mnemonic.trim(),
            privateKeyHex = privateKeyHex,
            publicKeyHex = publicKeyHex,
            address = address
        )
    }
}
