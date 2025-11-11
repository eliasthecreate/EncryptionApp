package com.example.encryptionapp

import android.util.Base64
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object EnhancedEncryptionUtils {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1"
    private const val ITERATION_COUNT = 10000
    private const val KEY_LENGTH = 256

    private fun generateKey(password: String, salt: ByteArray): SecretKey {
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, ALGORITHM)
    }

    fun encrypt(text: String, password: String): String {
        try {
            val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
            val key = generateKey(password, salt)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

            // Combine salt + iv + encrypted data
            val combined = salt + iv + encryptedBytes
            return Base64.encodeToString(combined, Base64.NO_WRAP)

        } catch (e: Exception) {
            throw RuntimeException("Encryption failed", e)
        }
    }

    fun decrypt(encryptedText: String, password: String): String {
        try {
            val combined = Base64.decode(encryptedText, Base64.NO_WRAP)

            // Extract salt (first 16 bytes), iv (next 16 bytes), and encrypted data
            val salt = combined.copyOfRange(0, 16)
            val iv = combined.copyOfRange(16, 32)
            val encryptedBytes = combined.copyOfRange(32, combined.size)

            val key = generateKey(password, salt)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)

        } catch (e: Exception) {
            throw RuntimeException("Decryption failed: Invalid password or corrupted data", e)
        }
    }
}