package com.example.casosemestral.util

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA512"
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val SALT_SIZE = 16

    fun hashPassword(password: String): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_SIZE)
        random.nextBytes(salt)

        val hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        return "${bytesToHex(salt)}:${bytesToHex(hash)}"
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) {
            return password == storedHash
        }
        val salt = hexToBytes(parts[0])
        val expectedHash = hexToBytes(parts[1])

        val actualHash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        return slowEquals(expectedHash, actualHash)
    }

    private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {
        val spec: KeySpec = PBEKeySpec(password, salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }

    private fun slowEquals(a: ByteArray, b: ByteArray): Boolean {
        var diff = a.size xor b.size
        for (i in 0 until a.size.coerceAtMost(b.size)) {
            diff = diff or (a[i].toInt() xor b[i].toInt())
        }
        return diff == 0
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        val hexArray = "0123456789abcdef".toCharArray()
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v ushr 4]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun hexToBytes(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}
