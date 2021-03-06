package com.castcle.networking.api.feed.datasource

import androidx.paging.PagingData
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.*
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
//  Created by sklim on 24/8/2021 AD at 16:43.

interface FeedRepository {

    suspend fun getFeed(
        feedRequestHeader: MutableStateFlow<FeedRequestHeader>
    ): Flow<PagingData<ContentFeedUiModel>>

    suspend fun getFeedRemoteMediator(
        feedRequestHeader: MutableStateFlow<FeedRequestHeader>
    ): Flow<PagingData<ContentFeedUiModel>>

    suspend fun getFeedGuests(
        feedRequestHeader: MutableStateFlow<FeedRequestHeader>
    ): Flow<PagingData<ContentFeedUiModel>>

    fun getCommentedPaging(
        commentRequest: CommentRequest
    ): Single<CommentedModel>

    suspend fun getTrend(
        feedRequestHeader: FeedRequestHeader
    ): Flow<PagingData<ContentFeedUiModel>>

    fun createComment(
        commentRequest: ReplyCommentRequest
    ): Single<ContentUiModel>

    fun createReplyComment(
        commentRequest: ReplyCommentRequest
    ): Single<ContentUiModel>

    fun likeContent(
        likeContentRequest: LikeContentRequest
    ): Completable

    fun recastContentPost(
        recastRequest: RecastRequest
    ): Completable

    fun quoteCastContentPost(
        recastRequest: RecastRequest
    ): Completable

    fun likeComment(
        likeCommentRequest: LikeCommentRequest,
        likeStatus: Boolean
    ): Completable

    fun deleteComment(
        deleteCommentRequest: DeleteCommentRequest,
    ): Completable

    fun deleteContent(
        deleteCommentRequest: DeleteContentRequest,
    ): Completable

    fun reportContent(
        contentId: String,
    ): Completable
}
