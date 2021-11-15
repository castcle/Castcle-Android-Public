package com.castcle.networking.api.search

import com.castcle.common_model.model.search.*
import com.castcle.common_model.model.search.domain.SearchResponse
import com.castcle.networking.api.search.api.SearchApi
import com.castcle.networking.service.operators.ApiOperators
import io.reactivex.Single
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
//  Created by sklim on 7/10/2021 AD at 14:43.

interface SearchRepository {
    fun getTopTrends(): Single<List<SearchUiModel>>

    fun getSearch(keyword: String): Single<List<SearchUiModel>>
}

class SearchRepositoryImpl @Inject constructor(
    private val searchApi: SearchApi
) : SearchRepository {

    override fun getTopTrends(): Single<List<SearchUiModel>> {
        return searchApi.getTopTrends()
            .lift(ApiOperators.mobileApiError())
            .map {
                it.hashtags.toSearchHasTagUiModels()
            }.firstOrError()
    }

    override fun getSearch(keyword: String): Single<List<SearchUiModel>> {
        return searchApi.getSearch(keyword)
            .lift(ApiOperators.mobileApiError())
            .map {
                handleMapSearchUiModel(it)
            }.firstOrError()
    }

    private fun handleMapSearchUiModel(searchResponse: SearchResponse): List<SearchUiModel> {
        val result = mutableListOf<SearchUiModel>()
        if (!searchResponse.keyword.isNullOrEmpty()) {
            searchResponse.keyword?.toSearchKeywordiModels()?.let {
                result.addAll(it)
            }
        }
        if (!searchResponse.hashtags.isNullOrEmpty()) {
            searchResponse.hashtags.toSearchHasTagUiModels().let {
                result.addAll(it)
            }
        }

        if (!searchResponse.follows.isNullOrEmpty()) {
            searchResponse.follows.toSearchFollowModels().let {
                result.addAll(it)
            }
        }

        return result
    }
}
