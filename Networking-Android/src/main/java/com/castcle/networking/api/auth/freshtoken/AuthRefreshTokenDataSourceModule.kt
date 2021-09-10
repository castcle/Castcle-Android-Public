package com.castcle.networking.api.auth.freshtoken

import android.content.Context
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.networking.NonAuthentication
import com.castcle.session_memory.SessionManagerRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthRefreshTokenDataSourceModule {

    @Provides
    fun provideRefreshTokenApi(
        @NonAuthentication retrofit: Retrofit
    ): AuthRefreshTokenApi {
        return retrofit.create(AuthRefreshTokenApi::class.java)
    }

    @Provides
    fun provideAuthRefreshToken(
        context: Context,
        secureStorage: SecureStorage,
        authRefreshTokenApi: AuthRefreshTokenApi,
        sessionManagerRepository: SessionManagerRepository
    ): AuthenticationTokenRepository = AuthenticationTokenRepositoryImpl(
        context,
        secureStorage,
        authRefreshTokenApi,
        sessionManagerRepository
    )
}
