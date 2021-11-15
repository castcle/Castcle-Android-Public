package com.castcle.common_model.model.feed.api.response

import com.castcle.common_model.ContentBaseUiModel
import com.castcle.common_model.model.feed.*
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
//  Created by sklim on 4/10/2021 AD at 11:50.

data class ContentResponse(
    @SerializedName("payload")
    val payload: List<PayloadProfileContentResponse>,

    @SerializedName("pagination")
    val pagination: Pagination
)

data class PayloadProfileContentResponse(
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

fun List<PayloadProfileContentResponse>.toProfileContentUiModel():
    ContentBaseUiModel.CommonContentBaseUiModel.ContentFeedUiModel {
    return ContentBaseUiModel.CommonContentBaseUiModel.ContentFeedUiModel(
        feedContentUiModel = map {
            ContentUiModel(
                contentType = it.type,
                featureSlug = it.feature?.slug ?: "",
                created = it.created,
                updated = it.updated,
                payLoadUiModel = it.toPayloadUiModel()
            )
        }.toMutableList()
    )
}

fun PayloadProfileContentResponse.toPayloadUiModel(): PayLoadUiModel {
    return PayLoadUiModel(
        contentId = id,
        contentType = type,
        headerFeed = "",
        contentFeed = "",
        photo = dynamicPhotoType(payload.photo),
        contentMessage = payload.message ?: "",
        created = created,
        updated = updated,
        link = payload.linkResponse?.map {
            it.toLinkUiModel()
        } ?: emptyList(),
        likedUiModel = likedResponse?.toLikedUiModel() ?: LikedUiModel(),
        commentedUiModel = commentedResponse?.toCommentedUiModel() ?: CommentedUiModel(),
        reCastedUiModel = recastedResponse?.toRecastedUiModel() ?: RecastedUiModel(),
        author = author.toAuthorUiModel(),
        featureContent = feature?.toFeatureUiModel(),
        replyUiModel = reply?.let { it ->
            it.map {
                it.toReplyUiModel()
            }
        }
    )
}