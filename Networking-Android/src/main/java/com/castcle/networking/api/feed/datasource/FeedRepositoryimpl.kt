package com.castcle.networking.api.feed.datasource

import android.content.Context
import androidx.paging.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.*
import com.castcle.common_model.model.feed.domain.dao.*
import com.castcle.networking.api.feed.FeedApi
import com.castcle.networking.service.operators.ApiOperators
import com.chayangkoon.champ.linkpreview.LinkPreview
import com.chayangkoon.champ.linkpreview.common.LinkContent
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.abs

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

@ExperimentalCoroutinesApi
class FeedRepositoryImpl @Inject constructor(
    private val context: Context,
    private val feedApi: FeedApi,
    private val userDao: UserDao,
    private val feedCacheDao: FeedCacheDao,
    private val pageKeyDao: PageKeyDao,
) : FeedRepository {

    override suspend fun getFeed(
        feedRequestHeader: MutableStateFlow<FeedRequestHeader>
    ): Flow<PagingData<ContentFeedUiModel>> {
        return feedRequestHeader.flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = DEFAULT_PAGE_SIZE_V1,
                    prefetchDistance = DEFAULT_PREFETCH_V1
                ),
                pagingSourceFactory = {
                    FeedPagingDataSource(feedApi, it)
                }
            ).flow.map { pagingData ->
                pagingData.map { it ->
                    it.photo?.map {
                        it.imageMedium
                    }?.let { it1 -> proLoadAllImage(it1) }
                    it
                }
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getFeedRemoteMediator(
        feedRequestHeader: MutableStateFlow<FeedRequestHeader>
    ): Flow<PagingData<ContentFeedUiModel>> {
        return withContext(Dispatchers.IO) {
            feedRequestHeader.flatMapLatest { it ->
                Pager(
                    config = PagingConfig(
                        pageSize = DEFAULT_PAGE_SIZE,
                        prefetchDistance = DEFAULT_PREFETCH,
                        enablePlaceholders = true
                    ),
                    remoteMediator = FeedRemoteMediator(
                        feedApi,
                        userDao,
                        pageKeyDao,
                        feedCacheDao,
                        it
                    )
                ) {
                    feedCacheDao.pagingSource()
                }.flow.map { pagingData ->
                    pagingData.map { it ->
                        proLoadAllImage(it.photo.map {
                            it.imageMedium
                        })
                        it.toContentFeedUiModel()
                    }
                }
            }
        }
    }

    private suspend fun proLoadAllImage(imageList: List<String>) {
        withContext(Dispatchers.IO) {
            imageList.forEach { url ->
                Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload()
            }
        }
    }

    val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val linkPreview = LinkPreview.Builder().build()

    private suspend fun getContentWebPreview(urlPreview: String): LinkContent {
        if (urlPreview.isBlank()) {
            return LinkContent()
        }
        return withContext(Dispatchers.IO) {
            linkPreview.loadPreview(urlPreview)
        }
    }

    private suspend fun getFeedCacheContent(): PagingSource<Int, FeedCacheModel> {
        return withContext(Dispatchers.IO) {
            feedCacheDao.pagingSource()
        }
    }

    override suspend fun getFeedGuests(
        feedRequestHeader: MutableStateFlow<FeedRequestHeader>
    ): Flow<PagingData<ContentFeedUiModel>> {
        return feedRequestHeader.flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = DEFAULT_PAGE_SIZE_V1,
                    prefetchDistance = DEFAULT_PREFETCH
                ),
                pagingSourceFactory = {
                    FeedGuestsPagingDataSource(feedApi, it)
                }
            ).flow
        }
    }

    override suspend fun getTrend(
        feedRequestHeader: FeedRequestHeader
    ): Flow<PagingData<ContentFeedUiModel>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE_V1, prefetchDistance = DEFAULT_PREFETCH),
        pagingSourceFactory = { TrendPagingDataSource(feedApi, feedRequestHeader) }
    ).flow

    override fun createComment(commentRequest: ReplyCommentRequest): Single<ContentUiModel> {
        scope.launch {
            updateCommentedContent(commentRequest.contentId)
        }
        return feedApi.sentComments(
            contentId = commentRequest.contentId,
            commentRequest = commentRequest
        ).lift(ApiOperators.mobileApiError())
            .map {
                it.payload.toContentUiModel()
            }.firstOrError()
    }

    override fun createReplyComment(commentRequest: ReplyCommentRequest): Single<ContentUiModel> {
        return feedApi.sentReplyComments(
            contentId = commentRequest.contentId,
            commentId = commentRequest.commentId,
            commentRequest = commentRequest
        ).lift(ApiOperators.mobileApiError())
            .map {
                it.payload.toContentUiModel()
            }.firstOrError()
    }

    override fun getCommentedPaging(commentRequest: CommentRequest): Single<CommentedModel> {
        return if (commentRequest.metaData.oldestId.isNullOrBlank()) {
            feedApi.getCommented(
                id = commentRequest.feedItemId,
                pageSize = commentRequest.metaData.resultCount,
                unitId = commentRequest.metaData.oldestId ?: "",
            )
        } else {
            feedApi.getCommented(
                id = commentRequest.feedItemId,
                pageSize = commentRequest.metaData.resultCount,
                unitId = commentRequest.metaData.oldestId ?: "",
            )
        }.lift(ApiOperators.mobileApiError())
            .map {
                it.toCommentModel()
            }.firstOrError()
    }

    private suspend fun updateCommentedContent(contentId: String) {
        withContext(Dispatchers.IO) {
            feedCacheDao.getFeedCache(contentId)
        }?.let {
            it.commentCount = it.commentCount.plus(1)
            it.commented = true
            feedCacheDao.updateLiked(it)
        }
    }

    override fun likeContent(likeContentRequest: LikeContentRequest): Completable {
        val status = if (!likeContentRequest.likeStatus) {
            LIKE_STATUS_LIKE
        } else {
            LIKE_STATUS_UNLIKE
        }
        scope.launch {
            withContext(Dispatchers.IO) {
                updateLikedContent(likeContentRequest.contentId, likeContentRequest.likeStatus)
            }
        }
        return feedApi.likeFeedContent(
            likeContentRequest.contentId,
            status,
            likeContentRequest
        ).lift(ApiOperators.mobileApiError())
            .firstOrError()
            .doOnError {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        updateLikedContent(likeContentRequest.contentId, true)
                    }
                }
            }.ignoreElement()
    }

    private suspend fun updateLikedContent(contentId: String, likeStatus: Boolean) {
        withContext(Dispatchers.IO) {
            feedCacheDao.getFeedCache(contentId)
        }?.let {
            if (!likeStatus) {
                it.likeCount = it.likeCount.plus(1)
                it.liked = true

            } else {
                it.likeCount = abs(it.likeCount - 1)
                it.liked = false
            }
            feedCacheDao.updateLiked(it)
        }
    }

    override fun recastContentPost(
        recastRequest: RecastRequest
    ): Completable {
        scope.launch {
            updateReCastedContent(recastRequest.contentId, recastRequest.reCasted)
        }
        return if (!recastRequest.reCasted) {
            feedApi.recastContent(id = recastRequest.contentId, recastRequest)
                .lift(ApiOperators.mobileApiError())
                .firstOrError()
                .ignoreElement()
        } else {
            feedApi.unRecastContent(id = recastRequest.contentId)
                .lift(ApiOperators.mobileApiError())
                .firstOrError()
                .ignoreElement()
        }
    }

    private suspend fun updateReCastedContent(contentId: String, recastStatus: Boolean) {
        withContext(Dispatchers.IO) {
            feedCacheDao.getFeedCache(contentId)
        }?.let {
            if (!recastStatus) {
                it.recastCount = it.recastCount.plus(1)
                it.recasted = true

            } else {
                it.recastCount = abs(it.recastCount - 1)
                it.recasted = false
            }
            feedCacheDao.updateLiked(it)
        }
    }

    override fun quoteCastContentPost(
        recastRequest: RecastRequest
    ): Completable {
        return feedApi.quoteCastContent(id = recastRequest.contentId, recastRequest)
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
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
            likeCommentRequest.contentId,
            likeCommentRequest.commentId,
            status,
            likeCommentRequest
        ).lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }

    suspend fun updateLiked(contentId: String) {
        withContext(Dispatchers.IO) {
            feedCacheDao.deleteFeedCache()
        }
    }

    override fun deleteComment(
        deleteCommentRequest: DeleteCommentRequest,
    ): Completable {
        return feedApi.deleteComment(
            deleteCommentRequest.conntentId,
            deleteCommentRequest.commentId,
        ).lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }

    override fun deleteContent(
        deleteCommentRequest: DeleteContentRequest,
    ): Completable {
        return feedApi.deleteContent(
            deleteCommentRequest.conntentId,
        ).lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }

    override fun reportContent(contentId: String): Completable {
        return feedApi
            .reportContent(
                contentId, ReportContentRequest("report content")
            )
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .ignoreElement()
    }
}

const val DEFAULT_PAGE_SIZE = 10
const val DEFAULT_PAGE_SIZE_V1 = 10
const val DEFAULT_PREFETCH = 2
const val DEFAULT_PREFETCH_V1 = 2
private const val LIKE_STATUS_LIKE = "liked"
private const val LIKE_STATUS_UNLIKE = "unliked"
