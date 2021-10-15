package com.castcle.networking.api.feed.datasource

import androidx.paging.*
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikeCommentRequest
import com.castcle.networking.api.feed.FeedApi
import com.castcle.networking.service.operators.ApiOperators
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
//  Created by sklim on 24/8/2021 AD at 16:46.

class FeedRepositoryImpl @Inject constructor(
    private val feedApi: FeedApi,
) : FeedRepository {

    @ExperimentalCoroutinesApi
    override suspend fun getFeed(
        feedRequestHeader: MutableStateFlow<FeedRequestHeader>
    ): Flow<PagingData<ContentUiModel>> {
        return feedRequestHeader.flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = DEFAULT_PAGE_SIZE,
                    prefetchDistance = DEFAULT_PREFETCH
                ),
                pagingSourceFactory = {
                    FeedPagingDataSource(feedApi, it)
                }
            ).flow
        }
    }

    override suspend fun getTrend(
        feedRequestHeader: FeedRequestHeader
    ): Flow<PagingData<ContentUiModel>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE, prefetchDistance = DEFAULT_PREFETCH),
        pagingSourceFactory = { TrendPagingDataSource(feedApi, feedRequestHeader) }
    ).flow

    override fun createComment(commentRequest: CommentRequest): Single<ContentUiModel> {
        return feedApi.sentComments(
            contentId = commentRequest.feedItemId,
            commentRequest = commentRequest
        )
            .lift(ApiOperators.mobileApiError())
            .map {
                ContentUiModel(
                    payLoadUiModel = PayLoadUiModel(
                        replyedUiModel = it.payload.toReplyUiModel()
                    )
                )
            }.firstOrError()
    }

    override suspend fun getCommented(
        commentRequest: CommentRequest
    ): Flow<PagingData<ContentUiModel>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE, prefetchDistance = DEFAULT_PREFETCH),
        pagingSourceFactory = { CommentPagingDataSource(feedApi, commentRequest) }
    ).flow

    override fun getCommentedPaging(commentRequest: CommentRequest): Single<CommentedModel> {
        return feedApi.getCommented(
            id = commentRequest.feedItemId,
            pageSize = commentRequest.paginationModel.limit,
            pageNumber = commentRequest.paginationModel.next ?: 0,
        ).lift(ApiOperators.mobileApiError())
            .map {
                it.toCommentModel()
            }.firstOrError()
    }

    suspend fun insertCommented(
        commentRequest: CommentRequest
    ): Flow<PagingData<ContentUiModel>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE, prefetchDistance = DEFAULT_PREFETCH),
        pagingSourceFactory = {
            val dataSource = CommentPagingDataSource(feedApi, commentRequest)
            dataSource.invalidate()
            dataSource
        }
    ).flow

    override fun likeContent(contentId: String, likeStatus: Boolean): Completable {
        val status = if (!likeStatus) {
            LIKE_STATUS_LIKE
        } else {
            LIKE_STATUS_UNLIKE
        }
        return feedApi.likeFeedContent(contentId, status)
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }

    override fun recastContentPost(
        recastRequest: RecastRequest
    ): Single<ContentUiModel> {
        return if (recastRequest.reCasted) {
            feedApi.unRecastContent(id = recastRequest.contentId, recastRequest)
                .lift(ApiOperators.mobileApiError())
                .map {
                    it.toContentUiModel()
                }.firstOrError()
        } else {
            feedApi.recastContent(id = recastRequest.contentId, recastRequest)
                .lift(ApiOperators.mobileApiError())
                .map {
                    it.toContentUiModel()
                }.firstOrError()
        }
    }

    override fun quoteCastContentPost(
        recastRequest: RecastRequest
    ): Single<ContentUiModel> {
        return feedApi.quoteCastContent(id = recastRequest.contentId, recastRequest)
            .lift(ApiOperators.mobileApiError())
            .map {
                it.toContentUiModel()
            }
            .firstOrError()
    }

    override fun likeComment(
        likeCommentRequest: LikeCommentRequest,
        likeStatus: Boolean
    ): Completable {
        val status = if (!likeStatus) {
            LIKE_STATUS_LIKE
        } else {
            LIKE_STATUS_UNLIKE
        }
        return feedApi.likeComment(
            likeCommentRequest.feedItemId,
            likeCommentRequest.commentId,
            status,
            likeCommentRequest
        ).lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }

    override fun deleteComment(
        likeCommentRequest: LikeCommentRequest,
    ): Completable {
        return feedApi.deleteComment(
            likeCommentRequest.feedItemId,
            likeCommentRequest.commentId,
        ).lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }
}

const val DEFAULT_PAGE_SIZE = 25
const val DEFAULT_PREFETCH = 2
private const val LIKE_STATUS_LIKE = "liked"
private const val LIKE_STATUS_UNLIKE = "unliked"
