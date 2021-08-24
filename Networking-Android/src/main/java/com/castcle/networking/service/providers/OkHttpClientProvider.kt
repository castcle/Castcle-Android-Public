package com.castcle.networking.service.providers

import android.content.Context
import com.castcle.networking.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.*

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
//  Created by sklim on 20/8/2021 AD at 13:20.

object OkHttpClientProvider {
    @JvmStatic
    fun getOkHttpClientBuilder(
        context: Context,
        interceptors: List<Interceptor>,
        authenticator: Authenticator? = null
    ): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val cacheDir = context.cacheDir
        val applicationCache = Cache(cacheDir, CACHE_SIZE)

        return OkHttpClient.Builder()
            .cache(applicationCache)
            .connectTimeout(DEFAULT_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .also {
                interceptors.forEach { interceptor ->
                    it.addInterceptor(interceptor)
                }
                if (authenticator != null) it.authenticator(authenticator)
            }
    }
}

private const val CACHE_SIZE = 10L * 1024 * 1024 // 10Mb
private const val DEFAULT_TIMEOUT_IN_SEC = 60L
