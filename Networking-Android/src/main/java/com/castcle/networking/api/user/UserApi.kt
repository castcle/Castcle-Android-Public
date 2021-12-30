package com.castcle.networking.api.user

import com.castcle.common_model.model.feed.ReportContentRequest
import com.castcle.common_model.model.userprofile.DeletePagePayload
import com.castcle.common_model.model.userprofile.DeleteUserPayload
import com.castcle.common_model.model.userprofile.ReportUserRequest
import com.castcle.common_model.model.userprofile.domain.*
import com.castcle.networking.service.common.*
import io.reactivex.Flowable
import io.reactivex.Single
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
//  Created by sklim on 31/8/2021 AD at 17:50.

interface UserApi {

    @GET("users/me")
    fun getUserProfile(): Single<Response<UserProfileResponse>>

    @GET("users/{castcleId}")
    fun getUserViewProfileId(
        @Path("castcleId") castcleId: String
    ): Single<Response<UserProfileResponse>>

    @GET("users/me/contents")
    suspend fun getUserProfileContent(
        @Query(FILTER_TYPE) filterType: String,
        @Query(UNIT_ID) unitId: String,
        @Query(USER_FIELDS) userField: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<UserContentResponse>

    @GET("users/me/contents")
    suspend fun getUserProfileContent(
        @Query(FILTER_TYPE) filterType: String,
        @Query(USER_FIELDS) userField: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<UserContentResponse>

    @GET("users/{castcleId}/contents")
    suspend fun getUserViewProfileContent(
        @Path("castcleId") castcleId: String,
        @Query(FILTER_TYPE) filterType: String,
        @Query(USER_FIELDS) userField: String,
        @Query(UNIT_ID) unitId: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<UserContentResponse>

    @GET("users/{castcleId}/contents")
    suspend fun getUserViewProfileContent(
        @Path("castcleId") castcleId: String,
        @Query(FILTER_TYPE) filterType: String,
        @Query(USER_FIELDS) userField: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<UserContentResponse>

    @PUT("users/me")
    fun updateUserProfile(
        @Body userUpdateRequest: UserUpdateRequest
    ): Flowable<Response<UserProfileResponse>>

    @HTTP(method = "DELETE", path = "/users/me", hasBody = true)
    fun onDeleteAccount(
        @Body deleteUserPayload: DeleteUserPayload,
    ): Flowable<Response<Unit>>

    @HTTP(method = "DELETE", path = "/pages/{castcleId}", hasBody = true)
    fun onDeletePage(
        @Body deleteUserPayload: DeletePagePayload,
        @Path("castcleId") castcleId: String,
    ): Flowable<Response<Unit>>

    @POST("contents/{feature_slug}")
    fun createContent(
        @Path("feature_slug") featureSlug: String,
        @Body createContentRequest: CreateContentRequest
    ): Flowable<Response<CreateCastResponse>>

    @PUT("users/{castcleId}/following")
    fun createFollowUser(
        @Path("castcleId") castcleId: String,
        @Body followRequest: FollowRequest,
    ): Flowable<Response<Unit>>

    @PUT("users/{castcleId}/unfollow")
    fun createUnFollowUser(
        @Path("castcleId") castcleId: String,
        @Body followRequest: FollowRequest,
    ): Flowable<Response<Unit>>

    @POST("users/{castcleId}/blocking")
    fun blockUser(
        @Path("castcleId") castcleId: String
    ): Flowable<Response<Unit>>

    @POST("users/{castcleId}/unblocking")
    fun unblockUser(
        @Path("castcleId") castcleId: String
    ): Flowable<Response<Unit>>

    @POST("users/{castcleId}/reporting")
    fun reportUser(
        @Path("castcleId") castcleId: String,
        @Body message: ReportUserRequest
    ): Flowable<Response<Unit>>

    @GET("pages/{castcleId}")
    fun getViewPage(
        @Path("castcleId") castcleId: String,
    ): Flowable<Response<UserProfileResponse>>

    @GET("pages/{castcleId}/contents")
    suspend fun getViewPageContent(
        @Path("castcleId") castcleId: String,
        @Query(FILTER_TYPE) filterType: String,
        @Query(USER_FIELDS) userField: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<UserContentResponse>

    @GET("pages/{castcleId}/contents")
    suspend fun getViewPageContent(
        @Path("castcleId") castcleId: String,
        @Query(FILTER_TYPE) filterType: String,
        @Query(UNIT_ID) unitId: String,
        @Query(USER_FIELDS) userField: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<UserContentResponse>

    @GET("/users/me/pages")
    fun getUserPage(
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Flowable<Response<UserPageResponse>>

    @GET("/users/mentions")
    fun getUserMention(
        @Query(KEYWORD) keyword: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Flowable<Response<UserMentionResponse>>
}
