package com.castcle.common_model.model.feed.api.response

import com.castcle.common_model.model.feed.QuoteCast
import com.castcle.common_model.model.userprofile.*
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
    val payload: List<FeedViewContentResponse>,
    @SerializedName("pagination")
    val pagination: Pagination
)

data class Pagination(
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("next")
    val next: Int? = null,
    @SerializedName("previous")
    val previous: Int,
    @SerializedName("self")
    val self: Int
)

data class FeedContentResponse(
    @SerializedName("id") var id: String,
    @SerializedName("feature") var feature: Feature,
    @SerializedName("circle") var circle: Circle? = null,
    @SerializedName("aggregator") var aggregator: Aggregator,
    @SerializedName("type") var type: String,
    @SerializedName("payload") var payload: PayloadResponse,
    @SerializedName("createdAt") var created: String,
    @SerializedName("updatedAt") var updated: String,
)

data class FeedViewContentResponse(
    @SerializedName("id") var id: String,
    @SerializedName("feature") var feature: Feature,
    @SerializedName("circle") var circle: Circle? = null,
    @SerializedName("aggregator") var aggregator: Aggregator,
    @SerializedName("type") var type: String,
    @SerializedName("payload") var payload: ViewPayloadResponse,
    @SerializedName("created") var created: String,
    @SerializedName("updated") var updated: String,
)

data class Feature(
    @SerializedName("id") var id: String? = null,
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
    @SerializedName("feature") var feature: Feature? = null,
    @SerializedName("liked") var likedResponse: LikedResponse? = null,
    @SerializedName("commented") var commentedResponse: CommentedResponse? = null,
    @SerializedName("recasted") var recastedResponse: RecastedResponse? = null,
    @SerializedName("quoteCast") var quoteCast: QuoteCast,
    @SerializedName("author") var author: Author,
    @SerializedName("createAt") var created: String,
    @SerializedName("updateAt") var updated: String,
    @SerializedName("reply") var reply: List<ReplyResponse>? = null
)

data class ViewPayloadResponse(
    @SerializedName("id") var id: String,
    @SerializedName("type") var type: String,
    @SerializedName("payload") var payload: PayloadContent,
    @SerializedName("feature") var feature: Feature? = null,
    @SerializedName("liked") var likedResponse: LikedResponse? = null,
    @SerializedName("commented") var commentedResponse: CommentedResponse? = null,
    @SerializedName("recasted") var recastedResponse: RecastedResponse? = null,
    @SerializedName("quoteCast") var quoteCast: QuoteCast,
    @SerializedName("author") var author: ViewAuthor,
    @SerializedName("createdAt") var created: String,
    @SerializedName("updatedAt") var updated: String,
    @SerializedName("reply") var reply: List<ReplyResponse>? = null
)

data class ReplyResponse(
    @SerializedName("id") var id: String,
    @SerializedName("message") var message: String,
    @SerializedName("createdAt") var created: String,
    @SerializedName("author") var author: Author,
)

data class PayloadContent(
    @SerializedName("header") var header: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("photo") var photo: PhotoResponse? = null,
    @SerializedName("link") var linkResponse: List<LinkResponse>? = null
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
    @SerializedName("displayName") var displayName: String? = null,
    @SerializedName("castcleId") var castcleId: String? = null,
    @SerializedName("avatar") var avatar: ImageResponse? = null,
    @SerializedName("verified") var verified: Verified? = null,
    @SerializedName("followed") var followed: Boolean? = null
)

data class ViewAuthor(
    @SerializedName("id") var id: String,
    @SerializedName("type") var type: String,
    @SerializedName("displayName") var displayName: String? = null,
    @SerializedName("castcleId") var castcleId: String? = null,
    @SerializedName("avatar") var avatar: String? = null,
    @SerializedName("verified") var verified: Verified? = null,
    @SerializedName("followed") var followed: Boolean? = null
)

data class Contents(
    @SerializedName("url") var url: String
)

data class LinkResponse(
    @SerializedName("type") var type: String,
    @SerializedName("url") var url: String,
    @SerializedName("imagePreview") var imagePreview: String? = null
)

data class LikedResponse(
    @SerializedName("count") var count: Int,
    @SerializedName("liked") var liked: Boolean,
    @SerializedName("participant") var participant: List<Participant>? = null
)

data class CommentedResponse(
    @SerializedName("count") var count: Int,
    @SerializedName("commented") var commented: Boolean,
    @SerializedName("participant") var participant: List<Participant>? = null
)

data class RecastedResponse(
    @SerializedName("count") var count: Int,
    @SerializedName("recasted") var recasted: Boolean,
    @SerializedName("participant") var participant: List<Participant>? = null
)

data class PhotoResponse(
    @SerializedName("contents") var contents: List<ImageResponse>? = null,
    @SerializedName("cover") var cover: Contents? = null
)