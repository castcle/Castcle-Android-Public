package com.castcle.networking.api.auth

import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.DecodedJWT
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.common_model.model.engagement.EngagementRequest
import com.castcle.common_model.model.login.domain.LoginRequest
import com.castcle.common_model.model.login.domain.LoginResponse
import com.castcle.common_model.model.setting.*
import com.castcle.common_model.model.setting.domain.*
import com.castcle.common_model.model.signin.*
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel.*
import com.castcle.common_model.model.signin.domain.*
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.toUserPage
import com.castcle.networking.api.auth.network.AuthenticationApi
import com.castcle.networking.api.response.toOAuthResponse
import com.castcle.networking.service.operators.ApiOperators
import com.castcle.networking.service.response.OAuthResponse
import com.castcle.session_memory.SessionManagerRepository
import com.castcle.session_memory.model.SessionEnvironment
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

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
//  Created by sklim on 31/8/2021 AD at 14:37.
interface AuthenticationsRepository {
    fun authLoginWithEmail(loginRequest: LoginRequest): Single<LoginResponse>

    fun authRegister(registerRequest: RegisterRequest): Completable

    fun authCheckEmailExsit(emailRequest: EmailRequest): Single<EmailVerifyUiModel>

    fun suggestionsDisplayName(displayNameRequest: DisplayNameRequest):
        Single<DisplayNameVerifyUiModel>

    fun authCheckCastcleExsit(castcleIdRequest: CastcleIdRequest):
        Single<CastcleIdVerifyUiModel>

    fun authRequestLinkVerifyEmail(): Completable

    fun authVerificationPassword(password: String): Single<VerificationUiModel>

    fun authChangePasswordSubmit(changePassRequest: ChangePassRequest): Completable

    fun createPage(createPageRequest: CreatePageRequest): Single<CreatePageResponse>

    fun updatePage(createPageRequest: CreatePageRequest): Single<CreatePageResponse>

    fun updatePageWorker(createPageRequest: CreatePageRequest): Single<User>

    fun engagementsTacking(engagementRequest: EngagementRequest): Completable
}

class AuthenticationsRepositoryImpl @Inject constructor(
    private val authenticationApi: AuthenticationApi,
    private val secureStorage: SecureStorage,
    private val sessionManagerRepository: SessionManagerRepository
) : AuthenticationsRepository {

    override fun authLoginWithEmail(loginRequest: LoginRequest): Single<LoginResponse> {
        return authenticationApi
            .authLogin(loginRequest)
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .doOnSuccess {
                updateProfile(it)
            }
    }

    override fun authRegister(registerRequest: RegisterRequest): Completable {
        return authenticationApi
            .register(registerRequest)
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .doOnSuccess {
                updateAccessToken(it.toOAuthResponse())
            }.ignoreElement()
    }

    private fun updateProfile(loginResponse: LoginResponse) {
        updateAccessToken(loginResponse.toOAuthResponse())
    }

    override fun createPage(createPageRequest: CreatePageRequest): Single<CreatePageResponse> {
        return authenticationApi
            .createPage(createPageRequest)
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
    }

    override fun updatePage(createPageRequest: CreatePageRequest): Single<CreatePageResponse> {
        return authenticationApi
            .updatePage(
                createPageRequest.castcleId,
                createPageRequest
            ).lift(ApiOperators.mobileApiError())
            .firstOrError()
    }

    override fun updatePageWorker(createPageRequest: CreatePageRequest): Single<User> {
        return authenticationApi
            .updatePage(
                createPageRequest.castcleId,
                createPageRequest
            ).lift(ApiOperators.mobileApiError())
            .map {
                it.toUserPage()
            }
            .firstOrError()
    }

    override fun authRequestLinkVerifyEmail(): Completable {
        return authenticationApi
            .checkrRquestLinkVerify()
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }

    override fun authCheckEmailExsit(emailRequest: EmailRequest): Single<EmailVerifyUiModel> {
        return authenticationApi
            .checkEmailExists(emailRequest)
            .lift(ApiOperators.mobileApiError())
            .map { it.toEmailVerifyUiModel() }
            .firstOrError()
    }

    override fun suggestionsDisplayName(displayNameRequest: DisplayNameRequest):
        Single<DisplayNameVerifyUiModel> {
        return authenticationApi
            .getSuggestionDisplayName(displayNameRequest)
            .lift(ApiOperators.mobileApiError())
            .map { it.toDisplayNameUiModel() }
            .firstOrError()
    }

    override fun authCheckCastcleExsit(castcleIdRequest: CastcleIdRequest):
        Single<CastcleIdVerifyUiModel> {
        return authenticationApi
            .checkCastcleIdExists(castcleIdRequest)
            .lift(ApiOperators.mobileApiError())
            .map { it.toCastcleIdVerifyUiModel() }
            .firstOrError()
    }

    private fun updateAccessToken(oAuthResponse: OAuthResponse) {
        secureStorage.apply {
            userAccessToken = oAuthResponse.accessToken
            userRefreshToken = oAuthResponse.refreshToken
        }
        getDataFromAccessToken(oAuthResponse.accessToken)
        updateSessionToken(oAuthResponse)
    }

    private fun getDataFromAccessToken(accessToken: String) {
        val tokenId = decodeHS256(accessToken)?.getClaim("id")?.asString() ?: ""
        secureStorage.apply {
            this.tokenId = tokenId
        }
    }

    private fun decodeHS256(encodeToken: String): DecodedJWT? {
        try {
            return JWT.decode(encodeToken)
        } catch (e: JWTDecodeException) {
            Log.e("Decode HS256", e.message ?: "")
        }
        return null
    }

    private fun updateSessionToken(response: OAuthResponse) {
        sessionManagerRepository.setSessionToken(
            SessionEnvironment.SessionToken(
                response.accessToken
            )
        )
        sessionManagerRepository.setSessionRefreshToken(
            SessionEnvironment.SessionRefreshToken(
                response.refreshToken
            )
        )
    }

    override fun authVerificationPassword(password: String): Single<VerificationUiModel> {
        return authenticationApi
            .verificationPassword(
                VerificationRequest(
                    password = password
                )
            ).lift(ApiOperators.mobileApiError())
            .map {
                it.toVerificationUiModel()
            }.firstOrError()
    }

    override fun authChangePasswordSubmit(changePassRequest: ChangePassRequest): Completable {
        return authenticationApi
            .changePasswordSubmit(
                changePassRequest
            ).lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }

    override fun engagementsTacking(engagementRequest: EngagementRequest): Completable {
        return authenticationApi
            .onEngagements(engagementRequest)
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }
}
