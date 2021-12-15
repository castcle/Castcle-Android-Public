package com.castcle.networking.api.auth.network

import com.castcle.common_model.model.engagement.EngagementRequest
import com.castcle.common_model.model.engagement.domain.OtpRequest
import com.castcle.common_model.model.engagement.domain.VerifyOtpRequest
import com.castcle.common_model.model.login.domain.LoginRequest
import com.castcle.common_model.model.login.domain.LoginResponse
import com.castcle.common_model.model.setting.domain.*
import com.castcle.common_model.model.signin.domain.*
import com.castcle.networking.api.response.SocialTokenResponse
import com.castcle.networking.api.response.TokenResponse
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.*

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
    ): Flowable<Response<LoginResponse>>

    @POST("authentications/checkEmailExists")
    fun checkEmailExists(
        @Body emailRequest: EmailRequest
    ): Flowable<Response<AuthExsitResponse>>

    @POST("authentications/suggestCastcleId")
    fun getSuggestionDisplayName(
        @Body displayNameRequest: DisplayNameRequest
    ): Flowable<Response<AuthExsitResponse>>

    @POST("authentications/checkCastcleIdExists")
    fun checkCastcleIdExists(
        @Body castcleRequest: CastcleIdRequest
    ): Flowable<Response<AuthExsitResponse>>

    @POST("authentications/register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Flowable<Response<TokenResponse>>

    @POST("authentications/loginWithSocial")
    fun registerWithSocial(
        @Body registerRequest: RegisterWithSocialRequest
    ): Flowable<Response<SocialTokenResponse>>

    @POST("authentications/requestLinkVerify")
    fun checkrRquestLinkVerify(): Flowable<Response<Unit>>

    @POST("authentications/verificationPassword")
    fun verificationPassword(
        @Body verificationRequest: VerificationRequest
    ): Flowable<Response<VerificationResponse>>

    @POST("authentications/changePasswordSubmit")
    fun changePasswordSubmit(
        @Body changePassRequest: ChangePassRequest
    ): Flowable<Response<Unit>>

    @POST("/pages")
    fun createPage(
        @Body createPageRequest: CreatePageRequest
    ): Flowable<Response<CreatePageResponse>>

    @PUT("/pages/{castcleId}")
    fun updatePage(
        @Path("castcleId") castcleId: String,
        @Body createPageRequest: CreatePageRequest
    ): Flowable<Response<CreatePageResponse>>

    @POST("/engagements")
    fun onEngagements(
        @Body engagementRequest: EngagementRequest
    ): Flowable<Response<Unit>>

    @POST("/authentications/requestOTP")
    fun requestOtp(
        @Body otpRequest: OtpRequest
    ): Flowable<Response<VerificationResponse>>

    @POST("authentications/verificationOTP")
    fun requestVerifyOtp(
        @Body verifyOtpRequest: VerifyOtpRequest
    ): Flowable<Response<VerificationResponse>>

    @POST("notifications/registerToken")
    fun registerFireBaseToken(
        @Body registerFireBaseTokenRequest: RegisterFireBaseTokenRequest
    ): Flowable<Response<Unit>>
}
