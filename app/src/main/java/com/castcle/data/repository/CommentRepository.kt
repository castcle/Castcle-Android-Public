package com.castcle.data.repository

import androidx.paging.*
import com.castcle.common_model.model.feed.CommentRequest
import com.castcle.common_model.model.feed.ContentDbModel
import com.castcle.data.model.dao.feed.CommentDao
import com.castcle.data.model.dao.feed.PageKeyDao
import com.castcle.networking.api.feed.CommentApi
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
//  Created by sklim on 6/10/2021 AD at 10:20.

interface CommentRepository {

    suspend fun getCommentMediator(
        commentRequest: CommentRequest
    ): Flow<PagingData<ContentDbModel>>

    suspend fun addCommentMediator(
        commentRequest: CommentRequest,
        commented: ContentDbModel
    ): Flow<PagingData<ContentDbModel>>

    suspend fun addCommentMediator(
        commented: ContentDbModel
    )
}

class CommentRepositoryImpl @Inject constructor(
    private val commentApi: CommentApi,
    private val commentDao: CommentDao,
    private val pageKeyDao: PageKeyDao
) : CommentRepository {

    private val pagingSourceFactory = { commentDao.pagingSource() }

    @ExperimentalPagingApi
    override suspend fun getCommentMediator(
        commentRequest: CommentRequest
    ): Flow<PagingData<ContentDbModel>> = Pager(
        config = PagingConfig(
            pageSize = DEFAULT_PAGE_SIZE,
            prefetchDistance = DEFAULT_PREFETCH
        ),
        remoteMediator = CommentRemoteMediator(
            commentApi,
            commentDao,
            pageKeyDao,
            commentRequest
        ),
        pagingSourceFactory = pagingSourceFactory
    ).flow

    override suspend fun addCommentMediator(commented: ContentDbModel) {
        commentDao.insertComment(commented)
    }

    @ExperimentalPagingApi
    override suspend fun addCommentMediator(
        commentRequest: CommentRequest,
        commented: ContentDbModel
    ): Flow<PagingData<ContentDbModel>> {

        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = DEFAULT_PREFETCH
            ),
            remoteMediator = CommentedRemoteMediator(
                commentDao,
                commented
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}
