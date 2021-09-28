package com.castcle.networking.api.feed.datasource

import androidx.paging.*
import com.castcle.common_model.model.feed.*
import com.castcle.networking.api.feed.FeedApi
import com.castcle.networking.service.operators.ApiOperators
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
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
    private val feedApi: FeedApi
) : FeedRepository {

    override suspend fun getFeed(
        feedRequestHeader: FeedRequestHeader
    ): Flow<PagingData<ContentUiModel>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE, prefetchDistance = DEFAULT_PREFETCH),
        pagingSourceFactory = { FeedPagingDataSource(feedApi, feedRequestHeader) }
    ).flow

    override fun likeContent(contentId: String, likeStatus: Boolean): Completable {
        val status = if (likeStatus) {
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
}

private const val DEFAULT_PAGE_SIZE = 25
private const val DEFAULT_PREFETCH = 2
private const val LIKE_STATUS_LIKE = "liked"
private const val LIKE_STATUS_UNLIKE = "unliked"
