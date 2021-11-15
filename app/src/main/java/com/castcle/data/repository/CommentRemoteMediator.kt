package com.castcle.data.repository

import androidx.paging.*
import com.castcle.common_model.model.feed.*
import com.castcle.data.model.dao.feed.CommentDao
import com.castcle.data.model.dao.feed.PageKeyDao
import com.castcle.networking.api.feed.CommentApi
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
class CommentRemoteMediator(
    private val commentApi: CommentApi,
    private val commentDao: CommentDao,
    private val pageKeyDao: PageKeyDao,
    private val commentRequest: CommentRequest
) : RemoteMediator<Int, ContentDbModel>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ContentDbModel>
    ): MediatorResult {

        return try {
            val (loadKey, pageSize) = when (loadType) {
                LoadType.REFRESH -> Pair(1, DEFAULT_PAGE_SIZE)
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    val remoteKey: PageKey? = if (lastItem?.id != null) {
                        pageKeyDao.getNextPageKey(lastItem.id.toInt())
                    } else {
                        null
                    }
                    Pair(remoteKey?.nextPage, remoteKey?.pageSize)
                }
            }

            val response = commentApi.getComments(
                id = commentRequest.feedItemId,
                pageNumber = loadKey ?: 0,
                pageSize = pageSize ?: 25
            )
            val pagedResponse = response.body()
            val data = listOf<ContentDbModel>()
            if (data != null) {
                if (loadType == LoadType.REFRESH) {
                    pageKeyDao.clearAll()
                    commentDao.deleteComment()
                }
                pageKeyDao.insertOrReplace(
                    PageKey(
                        data.firstOrNull()?.id.toString(),
                        pagedResponse?.pagination?.next,
                        pagedResponse?.pagination?.limit
                    )
                )
                commentDao.insertAllComment(data)
            }

            MediatorResult.Success(endOfPaginationReached = response.body()?.pagination?.next == null)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
