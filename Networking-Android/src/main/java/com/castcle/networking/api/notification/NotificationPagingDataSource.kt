package com.castcle.networking.api.notification

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.castcle.common_model.model.notification.*
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

class NotificationPagingDataSource(
    private val notificationApi: NotificationApi,
    private val source: String
) : PagingSource<Int, NotificationPayloadModel>() {

    private var nextPage: Int = 0

    private var oldestId: String = ""

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationPayloadModel> {
        val pageNumber = params.key ?: 1
        val pageSize = params.loadSize
        return try {
            val response = if (pageNumber == 1) {
                notificationApi.getNotification(
                    source = source,
                    pageNumber = pageNumber,
                    pageSize = pageSize
                )
            } else {
                notificationApi.getNotification(
                    source = source,
                    pageNumber = pageNumber,
                    pageSize = pageSize,
                    unitId = oldestId
                )
            }
            nextPage = pageNumber

            val pagedResponse = response.body()
            val data = pagedResponse?.toNotificationUiModel()
            var nextPageNumber: Int? = null
            oldestId = pagedResponse?.metadata?.oldestId ?: ""

            if (pagedResponse?.metadata?.oldestId != null &&
                pagedResponse.metadata.oldestId?.isNotBlank() == true &&
                pagedResponse.metadata.oldestId != oldestId
            ) {
                oldestId = pagedResponse.metadata.oldestId ?: ""
                nextPageNumber = nextPage.plus(1)
            }

            LoadResult.Page(
                data = (data as NotificationUiModel.NotificationItemModel).notificationPayloadModel,
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationPayloadModel>): Int = 1
}
