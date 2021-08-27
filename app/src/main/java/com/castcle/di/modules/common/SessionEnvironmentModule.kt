package com.castcle.di.modules.common

import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.data.storage.AppPreferences
import com.castcle.session_memory.model.SessionEnvironment
import dagger.Module
import dagger.Provides

@Module
class SessionEnvironmentModule {
    @Provides
    fun provideSessionEnvironment(
        secureStorage: SecureStorage,
        appPreferences: AppPreferences
    ): SessionEnvironment {

        val sessionToken = SessionEnvironment.SessionToken(
            token = secureStorage.userAccessToken ?: ""
        )

        val sessionRefreshToken = SessionEnvironment.SessionRefreshToken(
            refreshToken = secureStorage.userRefreshToken ?: ""
        )

        val languageCode =
            appPreferences.language ?: "en"

        return SessionEnvironment(
            sessionToken = sessionToken,
            sessionRefreshToken = sessionRefreshToken,
            languageCode = languageCode
        )
    }
}
