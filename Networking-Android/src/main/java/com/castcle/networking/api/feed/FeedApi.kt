package com.castcle.networking.api.feed

import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.api.response.*
import com.castcle.common_model.model.feed.converter.LikeCommentRequest
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.networking.service.common.*
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
//  Created by sklim on 24/8/2021 AD at 15:15.

interface FeedApi {

    @GET("feeds/members/{feature_slug}/{circle_slug}")
    suspend fun getFeed(
        @Path("feature_slug") featureSlug: String,
        @Path("circle_slug") circleSlug: String,
        @Query(MODE) mode: String,
        @Query(HAS_TAG) hasTag: String,
        @Query(USER_FIELDS) userField: String,
        @Query(UNIT_ID) unitId: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<PayLoadList>

    @GET("feeds/members/{feature_slug}/{circle_slug}")
    suspend fun getFeed(
        @Path("feature_slug") featureSlug: String,
        @Path("circle_slug") circleSlug: String,
        @Query(MODE) mode: String,
        @Query(HAS_TAG) hasTag: String,
        @Query(USER_FIELDS) userField: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<PayLoadList>

    @GET("feeds/guests")
    suspend fun getFeedGuests(
        @Query(UNIT_ID) unitId: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<PayLoadList>

    @GET("feeds/guests")
    suspend fun getFeedGuests(
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<PayLoadList>

    @GET("feeds/members/{feature_slug}/{circle_slug}")
    suspend fun getFeedByMode(
        @Path("feature_slug") featureSlug: String,
        @Path("circle_slug") circleSlug: String,
        @Query(HAS_TAG) mode: String,
        @Query(USER_FIELDS) userField: String,
        @Query(UNIT_ID) unitId: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int
    ): Response<PayLoadList>

    @GET("feeds/members/{feature_slug}/{circle_slug}")
    suspend fun getFeedByMode(
        @Path("feature_slug") featureSlug: String,
        @Path("circle_slug") circleSlug: String,
        @Query(HAS_TAG) mode: String,
        @Query(USER_FIELDS) userField: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int
    ): Response<PayLoadList>

    @PUT("contents/{contentId}/{likeStatus}")
    fun likeFeedContent(
        @Path("contentId") contentId: String,
        @Path("likeStatus") likeStatus: String,
        @Body likeContentRequest: LikeContentRequest,
    ): Flowable<Response<Any>>

    @POST("contents/{id}/recasted")
    fun recastContent(
        @Path("id") id: String,
        @Body recastRequest: RecastRequest,
    ): Flowable<Response<Unit>>

    @DELETE("contents/{id}")
    fun unRecastContent(
        @Path("id") id: String,
    ): Flowable<Response<Unit>>

    @POST("contents/{id}/quotecast")
    fun quoteCastContent(
        @Path("id") id: String,
        @Body recastRequest: RecastRequest,
    ): Flowable<Response<Unit>>

    @GET("contents/{id}/comments")
    suspend fun getComments(
        @Path("id") id: String,
        @Query(PAGE_NUMBER) pageNumber: Int,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Response<ContentCommentResponse>

    @GET("contents/{id}/comments")
    fun getCommented(
        @Path("id") id: String,
        @Query(UNIT_ID) unitId: String,
        @Query(PAGE_SIZE) pageSize: Int,
    ): Flowable<Response<ContentCommentResponse>>

    @POST("contents/{contentId}/comments")
    fun sentComments(
        @Path("contentId") contentId: String,
        @Body commentRequest: ReplyCommentRequest,
    ): Flowable<Response<ContentCommentedResponse>>

    @POST("contents/{contentId}/comments/{commentId}/reply")
    fun sentReplyComments(
        @Path("contentId") contentId: String,
        @Path("commentId") commentId: String,
        @Body commentRequest: ReplyCommentRequest,
    ): Flowable<Response<ContentCommentedResponse>>

    @DELETE("contents/{contentId}")
    fun deleteContent(
        @Path("contentId") contentId: String,
    ): Flowable<Response<Unit>>

    @DELETE("contents/{contentId}/comments/{commentId}")
    fun deleteComment(
        @Path("contentId") contentId: String,
        @Path("commentId") contentIdChild: String,
    ): Flowable<Response<Unit>>

    @PUT("contents/{contentId}/comments/{commentId}/{likeStatus}")
    fun likeComment(
        @Path("contentId") contentId: String,
        @Path("commentId") commentId: String,
        @Path("likeStatus") likeStatus: String,
        @Body likedRequest: LikeCommentRequest,
    ): Flowable<Response<Unit>>

    @POST("contents/{contentId}/reporting")
    fun reportContent(
        @Path("contentId") contentId: String,
        @Body message: ReportContentRequest,
    ): Flowable<Response<Unit>>
}
