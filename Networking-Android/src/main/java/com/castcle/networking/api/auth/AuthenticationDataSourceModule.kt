package com.castcle.networking.api.auth

import com.castcle.networking.api.auth.network.AuthenticationApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthenticationDataSourceModule {

    @Provides
    fun provideAuthenticationApi(retrofit: Retrofit): AuthenticationApi {
        return retrofit.create(AuthenticationApi::class.java)
    }

}
