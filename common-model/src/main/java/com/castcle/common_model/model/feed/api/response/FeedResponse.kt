package com.castcle.common_model.model.feed.api.response

import com.castcle.common_model.model.feed.QuoteCast
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
//  Created by sklim on 24/8/2021 AD at 16:08.

data class FeedResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("payload")
    val payload: List<FeedContentResponse>,
    @SerializedName("pagination")
    val pagination: Pagination
)

data class Pagination(
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("next")
    val next: Int,
    @SerializedName("previous")
    val previous: Int,
    @SerializedName("self")
    val self: Int
)

data class FeedContentResponse(
    @SerializedName("id") var id: String,
    @SerializedName("feature") var feature: Feature,
    @SerializedName("circle") var circle: Circle,
    @SerializedName("aggregator") var aggregator: Aggregator,
    @SerializedName("type") var type: String,
    @SerializedName("payload") var payload: PayloadResponse,
    @SerializedName("created") var created: String,
    @SerializedName("updated") var updated: String
)

data class Feature(
    @SerializedName("id") var id: String,
    @SerializedName("slug") var slug: String,
    @SerializedName("name") var name: String,
    @SerializedName("key") var key: String
)

data class Circle(
    @SerializedName("id") var id: String,
    @SerializedName("slug") var slug: String,
    @SerializedName("name") var name: String,
    @SerializedName("key") var key: String
)

data class Aggregator(
    @SerializedName("type") var type: String,
    @SerializedName("id") var id: String,
    @SerializedName("action") var action: String,
    @SerializedName("message") var message: String
)

data class PayloadResponse(
    @SerializedName("id") var id: String,
    @SerializedName("type") var type: String,
    @SerializedName("payload") var payload: PayloadContent,
    @SerializedName("feature") var feature: Feature,
    @SerializedName("liked") var likedResponse: LikedResponse,
    @SerializedName("commented") var commentedResponse: CommentedResponse,
    @SerializedName("recasted") var recastedResponse: RecastedResponse,
    @SerializedName("quoteCast") var quoteCast: QuoteCast,
    @SerializedName("author") var author: Author,
    @SerializedName("created") var created: String,
    @SerializedName("updated") var updated: String
)

data class PayloadContent(
    @SerializedName("header") var header: String? = null,
    @SerializedName("content") var content: String,
    @SerializedName("photo") var photo: PhotoResponse,
    @SerializedName("link") var linkResponse: List<LinkResponse>
)

data class Participant(
    @SerializedName("type") var type: String,
    @SerializedName("id") var id: String,
    @SerializedName("name") var name: String,
    @SerializedName("avatar") var avatar: String
)

data class Author(
    @SerializedName("id") var id: String,
    @SerializedName("type") var type: String,
    @SerializedName("displayName") var displayName: String,
    @SerializedName("avatar") var avatar: String,
    @SerializedName("verified") var verified: Boolean,
    @SerializedName("followed") var followed: Boolean
)

data class Contents(
    @SerializedName("url") var url: String
)

data class LinkResponse(
    @SerializedName("type") var type: String,
    @SerializedName("url") var url: String
)

data class LikedResponse(
    @SerializedName("count") var count: Int,
    @SerializedName("liked") var liked: Boolean,
    @SerializedName("participant") var participant: List<Participant>
)

data class CommentedResponse(
    @SerializedName("count") var count: Int,
    @SerializedName("commented") var commented: Boolean,
    @SerializedName("participant") var participant: List<Participant>
)

data class RecastedResponse(
    @SerializedName("count") var count: Int,
    @SerializedName("recasted") var recasted: Boolean,
    @SerializedName("participant") var participant: List<Participant>
)

data class PhotoResponse(
    @SerializedName("contents") var contents: List<Contents>? = null,
    @SerializedName("cover") var cover: Contents? = null
)