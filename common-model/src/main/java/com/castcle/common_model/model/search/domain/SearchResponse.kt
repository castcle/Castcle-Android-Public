package com.castcle.common_model.model.search.domain

import com.castcle.common_model.model.userprofile.domain.ImageResponse
import com.castcle.common_model.model.userprofile.domain.ImagesRequest
import com.google.gson.annotations.SerializedName

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
//  Created by sklim on 7/10/2021 AD at 14:22.

data class SearchResponse(
    @SerializedName("keyword")
    val keyword: List<Keyword>? = null,
    @SerializedName("follows")
    val follows: List<Follow>,
    @SerializedName("hashtags")
    val hashtags: List<Hashtag>
)

data class Keyword(
    @SerializedName("isTrending")
    val isTrending: Boolean,
    @SerializedName("text")
    val text: String
)

data class Follow(
    @SerializedName("aggregator")
    val aggregator: Aggregator,
    @SerializedName("avatar")
    val avatar: ImageResponse,
    @SerializedName("castcleId")
    val castcleId: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("verified")
    val verified: Boolean
)

data class Hashtag(
    @SerializedName("count")
    val count: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("trends")
    val trends: String?="",
    @SerializedName("isTrending")
    val isTrending: Boolean? = null
)

data class Aggregator(
    @SerializedName("action")
    val action: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("count")
    val count: String
)