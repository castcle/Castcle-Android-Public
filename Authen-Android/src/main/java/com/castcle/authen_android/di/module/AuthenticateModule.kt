package com.castcle.authen_android.di.module

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import com.castcle.authen_android.data.encryption.*
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.authen_android.data.storage.SecureStorageImpl
import dagger.*

@Module
@SuppressWarnings("UnnecessaryAbstractClass")
abstract class AuthenticateModule {

    @Binds
    internal abstract fun encryptor(encryptorImpl: EncryptorImpl): Encryptor

    @Binds
    internal abstract fun secureStorage(secureStorage: SecureStorageImpl): SecureStorage

    companion object {

        @Provides
        fun authSecretKey(
            authSecretKeyImpl: AuthSecretKeyImpl,
            preMAuthSecretKeyImpl: PreMAuthSecretKeyImpl
        ): AuthSecretKey {
            return if (SDK_INT >= M) {
                authSecretKeyImpl
            } else {
                preMAuthSecretKeyImpl
            }
        }
    }
}
