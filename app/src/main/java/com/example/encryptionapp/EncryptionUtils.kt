package com.example.encryptionapp

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val KEY = "MySuperSecretKey" // 16 characters for AES-128

    private fun getKey(): Key {
        val keyBytes = KEY.toByteArray(Charsets.UTF_8)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    fun encrypt(text: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getKey())
            val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            throw RuntimeException("Encryption failed", e)
        }
    }

    fun decrypt(encryptedText: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getKey())
            val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw RuntimeException("Decryption failed", e)
        }
    }
}