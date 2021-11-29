package com.castcle.networking

import android.content.Context
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.networking.api.auth.freshtoken.AuthRefreshTokenApi
import com.castcle.networking.api.auth.freshtoken.AuthenticationTokenRepositoryImpl
import com.castcle.networking.service.authenticator.TokenRefresher
import com.castcle.networking.service.common.secrets.ApiEndPointUrl
import com.castcle.networking.service.common.secrets.ApiEndPointUrlImpl
import com.castcle.networking.service.providers.CoreApi
import com.castcle.session_memory.SessionManagerRepository
import com.castcle.session_memory.model.SessionEnvironment
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Qualifier

//  Copyright (c) 2021, Castcle and/or its affiliates. All rights reserved.
//  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//
//  This code is free software; you can redistribute it and/or modify it
//  under the terms of the GNU General Public License version 3 only, as
//  published by the Free Software Foundation.
//
//  This code is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
//  version 3 for more details (a copy is included in the LICENSE file that
//  accompanied this code).
//
//  You should have received a copy of the GNU General Public License version
//  3 along with this work; if not, write to the Free Software Foundation,
//  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
//
//  Please contact Castcle, 22 Phet Kasem 47/2 Alley, Bang Khae, Bangkok,
//  Thailand 10160, or visit www.castcle.com if you need additional information
//  or have any questions.
//
//
//  Created by sklim on 20/8/2021 AD at 15:08.

@Module
class NetworkModule {

    @Provides
    fun provideApiEndPointUrl(): ApiEndPointUrl = ApiEndPointUrlImpl()

    @Provides
    fun provideTokenRefreshRepository(
        context: Context,
        secureStorage: SecureStorage,
        authRefreshTokenApi: AuthRefreshTokenApi,
        sessionManagerRepository: SessionManagerRepository
    ): TokenRefresher = AuthenticationTokenRepositoryImpl(
        context,
        secureStorage,
        authRefreshTokenApi,
        sessionManagerRepository
    )

    @Provides
    @NonAuthentication
    fun provideNonAuthenticationRetrofit(
        context: Context,
        apiEndPointUrl: ApiEndPointUrl,
        sessionEnvironment: SessionEnvironment,
        sessionManagerRepository: SessionManagerRepository
    ): Retrofit {
        return CoreApi.retrofitService(
            context = context,
            apiEndPointUrl = apiEndPointUrl,
            sessionVariable = sessionEnvironment,
            sessionManagerRepository = sessionManagerRepository
        )
    }

    @Provides
    fun provideRetrofit(
        context: Context,
        apiEndPointUrl: ApiEndPointUrl,
        tokenRefresher: TokenRefresher,
        sessionEnvironment: SessionEnvironment,
        secureStorage: SecureStorage,
        sessionManagerRepository: SessionManagerRepository
    ): Retrofit {
        return CoreApi.retrofitService(
            context = context,
            apiEndPointUrl = apiEndPointUrl,
            sessionVariable = sessionEnvironment,
            tokenRefresher = tokenRefresher,
            secureStorage = secureStorage,
            sessionManagerRepository = sessionManagerRepository
        )
    }

    @TwitterAuthentication
    @Provides
    fun provideTwitterRetrofit(
        context: Context,
        apiEndPointUrl: ApiEndPointUrl,
        tokenRefresher: TokenRefresher,
        sessionEnvironment: SessionEnvironment,
        secureStorage: SecureStorage,
        sessionManagerRepository: SessionManagerRepository
    ): Retrofit {
        return CoreApi.retrofitService(
            context = context,
            apiEndPointUrl = apiEndPointUrl,
            sessionVariable = sessionEnvironment,
            tokenRefresher = tokenRefresher,
            secureStorage = secureStorage,
            sessionManagerRepository = sessionManagerRepository
        )
    }
}

@Qualifier
annotation class NonAuthentication

@Qualifier
annotation class TwitterAuthentication
