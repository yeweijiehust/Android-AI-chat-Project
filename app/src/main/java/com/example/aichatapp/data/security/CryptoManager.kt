package com.example.aichatapp.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor() {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun getSecretKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createSecretKey()
    }

    private fun createSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return keyGenerator.generateKey()
    }

    fun encrypt(plainText: String): String {
        if (plainText.isBlank()) return ""

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray())

        // Combine the IV (first 12 bytes) and the encrypted data so we can decrypt it later
        val combined = ByteArray(iv.size + encrypted.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encrypted, 0, combined, iv.size, encrypted.size)

        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String {
        if (encryptedText.isBlank()) return ""

        return try {
            val combined = Base64.decode(encryptedText, Base64.DEFAULT)
            val cipher = Cipher.getInstance(TRANSFORMATION)

            // Extract the 12-byte IV from the combined array
            val spec = GCMParameterSpec(128, combined, 0, 12)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            // Decrypt the remaining bytes
            val decrypted = cipher.doFinal(combined, 12, combined.size - 12)
            String(decrypted)
        } catch (e: Exception) {
            e.printStackTrace()
            "" // If encryption fails (e.g., key gets wiped by OS), return empty string gracefully
        }
    }

    companion object {
        private const val KEY_ALIAS = "ai_chat_secure_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}