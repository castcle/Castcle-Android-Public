package com.castcle.networking.api.feed.datasource

import android.util.Log
import androidx.paging.*
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.domain.dao.*
import com.castcle.networking.api.feed.FeedApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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
    private val userDao: UserDao,
    private val pageKeyDao: PageKeyDao,
    private val feedCacheDao: FeedCacheDao,
    private val feedRequestHeader: FeedRequestHeader
) : RemoteMediator<Int, FeedCacheModel>() {

    private var nextPage: Int = 0

    private var oldestId: String = ""

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private var remoteKey: PageKey? = PageKey()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedCacheModel>
    ): MediatorResult {

        return try {

            Log.d("REMOTE-KEY", "${loadType.name}")
            val (loadKey, pageSize) = when (loadType) {
                LoadType.REFRESH -> Pair(1, DEFAULT_PAGE_SIZE)
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    remoteKey = withContext(Dispatchers.IO) {
                        pageKeyDao.getAllNextPageKey()?.lastOrNull()
                    } ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    Log.d("REMOTE-KEY-NEXT", "${remoteKey?.nextPage},${remoteKey?.pageSize}")
                    Pair(remoteKey?.nextPage, remoteKey?.pageSize)
                }
            }
            val response = feedApi.getFeed(
                featureSlug = feedRequestHeader.featureSlug,
                circleSlug = feedRequestHeader.circleSlug,
                hasTag = feedRequestHeader.hashtag,
                unitId = remoteKey?.unitId ?: "",
                pageNumber = loadKey ?: 0,
                pageSize = DEFAULT_PAGE_SIZE,
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
                        feedCacheDao.deleteFeedCache()
                    }
                    pageKeyDao.insertOrReplace(
                        PageKey(
                            contentId = data.lastOrNull()?.contentId,
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
            Log.d("REMOTE-ERROR", "$e")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.d("REMOTE-ERROR", "${e.message}")
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, FeedCacheModel>): PageKey? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                pageKeyDao.getNextPageKey(repo.contentId)
            }
    }
}
