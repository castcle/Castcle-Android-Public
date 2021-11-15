package com.castcle.networking.api.search.api

import com.castcle.common_model.model.search.domain.SearchResponse
import com.castcle.networking.service.common.*
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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
//  Created by sklim on 7/10/2021 AD at 14:14.

interface SearchApi {

    @GET("/searches/topTrends")
    fun getTopTrends(
        @Query(EXCLUDE) exclude: String = MODE_DIS_FOLLOW,
    ): Flowable<Response<SearchResponse>>

    @GET("/searches/topTrends")
    fun getTrends(
        @Query(SEARCH_LIMIT) searchLimit: String,
        @Query(EXCLUDE) exclude: String,
    ): Flowable<Response<SearchResponse>>

    @GET("/searches")
    fun getSearch(
        @Query(KEYWORD) exclude: String,
    ): Flowable<Response<SearchResponse>>
}
