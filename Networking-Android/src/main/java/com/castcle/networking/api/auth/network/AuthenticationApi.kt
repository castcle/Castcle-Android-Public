package com.castcle.networking.api.auth.network

import com.castcle.common_model.model.login.LoginRequest
import com.castcle.common_model.model.signin.response.AuthExsitResponse
import com.castcle.common_model.model.signin.reuquest.*
import com.castcle.networking.api.response.TokenResponse
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
//  Created by sklim on 23/8/2021 AD at 08:46.

interface AuthenticationApi {

    @POST("authentications/login")
    fun authLogin(
        @Body loginRequest: LoginRequest
    ): Flowable<Response<TokenResponse>>

    @POST("authentications/checkEmailExists")
    fun checkEmailExists(
        @Body emailRequest: EmailRequest
    ): Flowable<Response<AuthExsitResponse>>

    @POST("authentications/checkDisplayNameExists")
    fun checkDisplayName(
        @Body displayNameRequest: DisplayNameRequest
    ): Flowable<Response<AuthExsitResponse>>

    @POST("authentications/checkCastcleIdExists")
    fun checkCastcleIdExists(
        @Body castcleRequest: CastcleIdRequest
    ): Flowable<Response<AuthExsitResponse>>

}