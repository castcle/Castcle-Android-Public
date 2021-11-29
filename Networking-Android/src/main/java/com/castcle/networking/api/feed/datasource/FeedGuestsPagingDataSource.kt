package com.castcle.networking.api.feed.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.castcle.common_model.model.feed.*
import com.castcle.networking.api.feed.FeedApi

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

class FeedGuestsPagingDataSource(
    private val feedApi: FeedApi,
    private val feedRequestHeader: FeedRequestHeader
) : PagingSource<Int, ContentFeedUiModel>() {

    private var nextPage: Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentFeedUiModel> {
        val pageNumber = params.key ?: 1
        val pageSize = params.loadSize
        return try {
            val response = feedApi.getFeedGuests(
                circleSlug = feedRequestHeader.circleSlug,
                hasTag = feedRequestHeader.hashtag,
                pageNumber = pageNumber,
                pageSize = pageSize,
                mode = feedRequestHeader.mode
            )
            nextPage = pageNumber

            val pagedResponse = response.body()
            val data = pagedResponse?.let { mapToContentFeedUiMode(it) }
            var nextPageNumber: Int? = null


            if (pagedResponse?.meta?.newestId != null &&
                pagedResponse.meta.newestId.toInt() != 0 &&
                pagedResponse.meta.newestId.toInt() != nextPage
            ) {
                nextPageNumber = pagedResponse.meta.newestId.toInt()
            }
            Log.d("NEXT-PAGE", "$nextPageNumber")

            LoadResult.Page(
                data = data.orEmpty(),
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ContentFeedUiModel>): Int = 1
}
