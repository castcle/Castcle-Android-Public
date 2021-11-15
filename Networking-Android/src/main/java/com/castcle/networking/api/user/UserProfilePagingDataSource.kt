package com.castcle.networking.api.user

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.common_model.model.feed.api.response.toProfileContentUiModel

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

class UserProfilePagingDataSource(
    private val userApi: UserApi,
    private val contentRequestHeader: FeedRequestHeader
) : PagingSource<Int, ContentUiModel>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentUiModel> {
        val pageNumber = params.key ?: 1
        val pageSize = params.loadSize
        return try {
            val response = userApi.getUserProfileContent(
                pageNumber = pageNumber,
                pageSize = pageSize,
                filterType = contentRequestHeader.type
            )
            val pagedResponse = response.body()
            val data = pagedResponse?.payload?.toProfileContentUiModel()
            var nextPageNumber: Int? = null
            if (pagedResponse?.pagination?.next != null) {
                val uri = Uri.parse(pagedResponse.pagination.next.toString())
                val nextPageQuery = uri.getQueryParameter("page")
                nextPageNumber = nextPageQuery?.toInt()
            }

            LoadResult.Page(
                data = data?.feedContentUiModel.orEmpty(),
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ContentUiModel>): Int = 1
}

const val PROFILE_TYPE_ME = "me"
const val PROFILE_TYPE_PAGE = "page"
const val PROFILE_TYPE_PEOPLE = "people"
