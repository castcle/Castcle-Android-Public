package com.castcle.common_model.model.search

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
//  Created by sklim on 7/10/2021 AD at 14:28.

sealed class SearchUiModel {

    data class SearchKeywordUiModel(
        val text: String,
        val isTrending: Boolean
    ) : SearchUiModel()

    data class SearchFollowUiModel(
        val aggregator: AggregatorUiModel,
        val avatar: String,
        val castcleId: String,
        val count: Int,
        val displayName: String,
        val id: String,
        val overview: String,
        val type: String,
        val verified: Boolean
    ) : SearchUiModel()

    data class SearchHasTagUiModel(
        val count: Int,
        val id: String,
        val key: String,
        val name: String,
        val rank: Int,
        val slug: String,
        val trends: String? = "",
        val isTrending: Boolean = false
    ) : SearchUiModel()

    data class SearchResentUiModel(
        val keyword: String
    ) : SearchUiModel()

    object SearchResentHeaderUiModel : SearchUiModel()
}

fun List<Hashtag>.toSearchHasTagUiModels(): List<SearchUiModel> {
    return map {
        it.toSearchHasTagUiModel()
    }
}

fun List<Follow>.toSearchFollowModels(): List<SearchUiModel> {
    return map {
        it.toSearchFollowUiModel()
    }
}

fun List<Keyword>.toSearchKeywordiModels(): List<SearchUiModel> {
    return map {
        it.toSearchKeywordUiModel()
    }
}

fun Keyword.toSearchKeywordUiModel(): SearchUiModel {
    return SearchUiModel.SearchKeywordUiModel(
        text = text,
        isTrending = isTrending
    )
}

fun Hashtag.toSearchHasTagUiModel(): SearchUiModel {
    return SearchUiModel.SearchHasTagUiModel(
        count = count,
        id = id,
        key = key,
        name = name,
        rank = rank,
        slug = slug,
        trends = trends ?: "",
        isTrending = isTrending ?: false
    )
}

fun Follow.toSearchFollowUiModel(): SearchUiModel {
    return SearchUiModel.SearchFollowUiModel(
        count = count,
        id = id,
        avatar = avatar,
        castcleId = castcleId,
        overview = overview,
        type = type,
        verified = verified,
        displayName = displayName,
        aggregator = aggregator.toAggregatorUiModel()
    )
}

data class AggregatorUiModel(
    val action: String,
    val id: String,
    val message: String,
    val type: String
)

fun Aggregator.toAggregatorUiModel() =
    AggregatorUiModel(
        action = action,
        id = id,
        message = message,
        type = type
    )

const val FOLLOW_PERSON = "person"
