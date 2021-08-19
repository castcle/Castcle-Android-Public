package com.castcle.authen_android.data.encryption

import android.os.Build.VERSION_CODES.M
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import androidx.annotation.RequiresApi
import com.co.the1.the1app.common.rx.BuildConfig
import java.security.KeyStore
import java.security.KeyStore.SecretKeyEntry
import java.security.KeyStore.getInstance
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

interface AuthSecretKey {
    fun get(): SecretKey
}

@RequiresApi(M)
class AuthSecretKeyImpl @Inject constructor() : AuthSecretKey {

    private val keyStore: KeyStore
        get() = getInstance(KEYSTORE_NAME).apply {
            load(null)
            if (!containsAlias(ALIAS)) {
                createKeyEntry()
            }
        }

    private fun createKeyEntry() {
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES, KEYSTORE_NAME)
        val purposes = PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        val spec = KeyGenParameterSpec.Builder(ALIAS, purposes)
            .setBlockModes(BLOCK_MODE_GCM)
            .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(false)
            .build()
        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    override fun get(): SecretKey {
        return try {
            (keyStore.getEntry(ALIAS, null) as SecretKeyEntry).secretKey
        } catch (_: Throwable) {
            // NOTE: accessing the key store above can fail with
            // `android.security.KeyStoreException: Invalid key blob`.
            // In that case, delete the current key entry,
            // so a new one can be created next time.
            keyStore.deleteEntry(ALIAS)
            throw SecretKeyException()
        }
    }
}

class PreMAuthSecretKeyImpl @Inject constructor() : AuthSecretKey {

    override fun get(): SecretKey {
        val privateKey = "1Hbfh667adfDEJ78"
        return SecretKeySpec(privateKey.toByteArray(), "AES")
    }
}

private const val KEYSTORE_NAME = "AndroidKeyStore"
private const val ALIAS = BuildConfig.LIBRARY_PACKAGE_NAME + ".keystoreAlias"

class SecretKeyException : Exception("Secret Key has been corrupted.")
