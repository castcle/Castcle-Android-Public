package com.castcle.data.repository

import androidx.paging.*
import com.castcle.common_model.model.feed.ContentDbModel
import com.castcle.data.model.dao.feed.CommentDao

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
class CommentedRemoteMediator(
    private val commentDao: CommentDao,
    private val commented: ContentDbModel
) : RemoteMediator<Int, ContentDbModel>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ContentDbModel>
    ): MediatorResult {
        return runCatching {
            when (loadType) {
                LoadType.REFRESH -> Pair(1, DEFAULT_PAGE_SIZE)
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    commentDao.insertComment(commented)
                }
            }

            MediatorResult.Success(endOfPaginationReached = false)
        }.fold(
            onSuccess = { endOfPaginationReached ->
                MediatorResult.Success(
                    endOfPaginationReached.endOfPaginationReached
                )
            },
            onFailure = {
                MediatorResult.Error(it)
            }
        )
    }
}
