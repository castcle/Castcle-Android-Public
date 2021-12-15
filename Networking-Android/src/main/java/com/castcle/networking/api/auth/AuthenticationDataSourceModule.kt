package com.castcle.networking.api.auth

import android.content.Context
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.networking.api.auth.network.AuthenticationApi
import com.castcle.session_memory.SessionManagerRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthenticationDataSourceModule {

    @Provides
    fun provideAuthenticationApi(retrofit: Retrofit): AuthenticationApi {
        return retrofit.create(AuthenticationApi::class.java)
    }

    @Provides
    fun provideAuthenticationsRepository(
        context: Context,
        secureStorage: SecureStorage,
        authenticationApi: AuthenticationApi,
        sessionManagerRepository: SessionManagerRepository
    ): AuthenticationsRepository = AuthenticationsRepositoryImpl(
        context,
        authenticationApi,
        secureStorage,
        sessionManagerRepository
    )
}
