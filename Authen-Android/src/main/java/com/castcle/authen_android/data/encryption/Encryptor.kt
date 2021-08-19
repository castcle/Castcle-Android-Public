package com.castcle.authen_android.data.encryption

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

interface Encryptor {

    fun encryptAndEncode(unencrypted: String): String

    fun decodeAndDecrypt(encoded: String): String
}

class EncryptorImpl @Inject constructor(
    private val secretKey: AuthSecretKey
) : Encryptor {

    override fun encryptAndEncode(unencrypted: String): String {
        val cipher = getCipher(Cipher.ENCRYPT_MODE)
        val encryptedData = cipher.doFinal(unencrypted.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    override fun decodeAndDecrypt(encoded: String): String {
        val cipher = getCipher(Cipher.DECRYPT_MODE)
        val encryptedBytes = Base64.decode(encoded, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun getCipher(mode: Int): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            val spec = GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, ENCRYPTION_IV)
            init(mode, secretKey.get(), spec)
        }
    }
}

private const val TRANSFORMATION = "AES/GCM/NoPadding"
private const val AUTHENTICATION_TAG_LENGTH = 128
private val ENCRYPTION_IV = "TheOneCrypto".toByteArray()
