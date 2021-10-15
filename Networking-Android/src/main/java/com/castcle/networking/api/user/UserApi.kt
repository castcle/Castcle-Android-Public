package com.castcle.networking.api.user

import com.castcle.common_model.model.feed.api.response.ContentResponse
import com.castcle.common_model.model.feed.api.response.ContentViewResponse
import com.castcle.common_model.model.userprofile.*
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
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<ContentResponse>

    @GET("users/{castcleId}/contents")
    suspend fun getUserViewProfileContent(
        @Path("castcleId") castcleId: String,
        @Query(FILTER_TYPE) filterType: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<ContentViewResponse>

    @PUT("users/me")
    fun updateUserProfile(
        @Body userUpdateRequest: UserUpdateRequest
    ): Flowable<Response<UserProfileResponse>>

    @POST("contents/{feature_slug}")
    fun createContent(
        @Path("feature_slug") featureSlug: String,
        @Body createContentRequest: CreateContentRequest
    ): Flowable<Response<CreateCastResponse>>

    @PUT("users/{castcleId}/follow")
    fun createFollowUser(
        @Path("castcleId") castcleId: String,
        @Body followRequest: FollowRequest,
    ): Flowable<Response<Unit>>
}
