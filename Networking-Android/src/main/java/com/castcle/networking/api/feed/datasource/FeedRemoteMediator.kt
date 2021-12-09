package com.castcle.networking.api.feed.datasource

import androidx.paging.*
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.domain.dao.*
import com.castcle.networking.api.feed.FeedApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.concurrent.*

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
//  Created by sklim on 25/8/2021 AD at 08:20.

@ExperimentalPagingApi
class FeedRemoteMediator(
    private val feedApi: FeedApi,
    private val pageKeyDao: PageKeyDao,
    private val feedCacheDao: FeedCacheDao,
    private val feedRequestHeader: FeedRequestHeader
) : RemoteMediator<Int, FeedCacheModel>() {

    private var nextPage: Int = 0

    private var oldestId: String = ""

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedCacheModel>
    ): MediatorResult {

        return try {
            var remoteKey = PageKey("", 0, 0, "")
            val (loadKey, pageSize) = when (loadType) {
                LoadType.REFRESH -> Pair(1, DEFAULT_PAGE_SIZE)
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    remoteKey = withContext(Dispatchers.IO) {
                        pageKeyDao.getNextPageKey(lastItem?.id ?: "")!!
                    }

                    if (remoteKey.id.isBlank()) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }
                    Pair(remoteKey.nextPage, remoteKey.pageSize)
                }
            }

            val response = feedApi.getFeed(
                featureSlug = feedRequestHeader.featureSlug,
                circleSlug = feedRequestHeader.circleSlug,
                hasTag = feedRequestHeader.hashtag,
                unitId = remoteKey.unitId ?: "",
                pageNumber = loadKey ?: 0,
                pageSize = pageSize ?: 25,
                mode = feedRequestHeader.mode
            )
            nextPage = loadKey?.plus(1) ?: 0
            oldestId = feedRequestHeader.oldestId

            val pagedResponse = response.body()
            val data = pagedResponse?.let { mapToContentFeedUiMode(feedRequestHeader.isMeId, it) }

            if (data != null) {
                withContext(Dispatchers.IO) {
                    if (loadType == LoadType.REFRESH) {
                        pageKeyDao.clearAll()
                        feedCacheDao.deleteComment()
                    }
                    pageKeyDao.insertOrReplace(
                        PageKey(
                            id = pagedResponse.payLoadLists.lastOrNull()?.id ?: "",
                            nextPage = nextPage,
                            pageSize = DEFAULT_PAGE_SIZE,
                            unitId = pagedResponse.meta.oldestId ?: ""
                        )
                    )
                    feedCacheDao.insertAllComment(data.toMapFeedCache())
                }
            }
            MediatorResult.Success(endOfPaginationReached = response.body()?.meta?.oldestId == null)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
