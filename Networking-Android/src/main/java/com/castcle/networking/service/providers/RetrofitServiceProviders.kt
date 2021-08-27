package com.castcle.networking.service.providers

import android.content.Context
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.networking.service.authenticator.ApplicationRequestAuthenticator
import com.castcle.networking.service.authenticator.TokenRefresher
import com.castcle.networking.service.common.secrets.ApiEndPointUrl
import com.castcle.networking.service.interceptor.ApplicationRequestInterceptor
import com.castcle.networking.service.providers.ConverterFactoryProvider.getGsonConverterFactory
import com.castcle.networking.service.providers.OkHttpClientProvider.getOkHttpClientBuilder
import com.castcle.session_memory.SessionManagerRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

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
//  Created by sklim on 20/8/2021 AD at 12:57.

internal object RetrofitServiceProviders {

    private var authenticatedRetrofit: Retrofit? = null
    private var nonAuthenticatedRetrofit: Retrofit? = null

    internal fun getRetrofitApi(
        context: Context,
        apiEndPointUrl: ApiEndPointUrl,
        sessionManagerRepository: SessionManagerRepository?,
        requiredAuthenticated: Boolean,
        tokenRefresher: TokenRefresher?,
        secureStorage: SecureStorage?
    ): Retrofit {
        return when (requiredAuthenticated) {
            true -> {
                if (authenticatedRetrofit == null) {
                    authenticatedRetrofit =
                        assembleRetrofit(
                            context,
                            apiEndPointUrl,
                            sessionManagerRepository,
                            true,
                            tokenRefresher,
                            secureStorage
                        )
                }
                requireNotNull(authenticatedRetrofit)
            }
            false -> {
                if (nonAuthenticatedRetrofit == null) {
                    nonAuthenticatedRetrofit =
                        assembleRetrofit(
                            context,
                            apiEndPointUrl,
                            sessionManagerRepository,
                        )
                }
                requireNotNull(nonAuthenticatedRetrofit)
            }
        }
    }

    private fun assembleRetrofit(
        context: Context,
        apiEndPointUrl: ApiEndPointUrl,
        sessionManager: SessionManagerRepository?,
        requiredAuthenticated: Boolean = false,
        tokenRefresher: TokenRefresher? = null,
        secureStorage: SecureStorage? = null
    ): Retrofit {
        val okHttpClient =
            provideOkHttpClient(
                context,
                sessionManager,
                requiredAuthenticated,
                tokenRefresher,
                secureStorage
            )
        val gson = provideGson()
        val converterFactories =
            provideConverterFactories(gson)
        return provideRetrofitBuilder(apiEndPointUrl, okHttpClient, *converterFactories).build()
    }

    private fun provideConverterFactories(
        gson: Gson,
    ): Array<Converter.Factory> {
        return arrayOf(getGsonConverterFactory(gson))
    }

    private fun provideOkHttpClient(
        context: Context,
        sessionManagerRepository: SessionManagerRepository?,
        requiredAuthenticated: Boolean,
        tokenRefresher: TokenRefresher?,
        secureStorage: SecureStorage?
    ): OkHttpClient {
        val requestInterceptor = ApplicationRequestInterceptor(
            context,
            sessionManagerRepository,
            requiredAuthenticated,
            secureStorage
        )
        val authenticator =
            tokenRefresher?.let {
                ApplicationRequestAuthenticator(
                    tokenRefresher = it,
                    sessionManagerRepository = sessionManagerRepository
                )
            }

        val okHttpBuilder = getOkHttpClientBuilder(
            context = context,
            interceptors = listOf(requestInterceptor)
        ).apply {
            authenticator?.let { authenticator(it) }
        }

//        FlipperInitializer.initFlipperPlugins(context, okHttpBuilder)

        return okHttpBuilder
            .build()
            .apply {
                authenticator?.okHttpClient = this
            }
    }

    private fun provideGson() = GsonBuilder()
        .setLenient()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        .create()

    private fun provideRetrofitBuilder(
        apiEndpointUrl: ApiEndPointUrl,
        okHttpClient: OkHttpClient,
        vararg converterFactories: Converter.Factory
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(apiEndpointUrl.value)
            .client(okHttpClient)
            .also {
                for (converterFactory in converterFactories) {
                    it.addConverterFactory(converterFactory)
                }
            }
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }
}
