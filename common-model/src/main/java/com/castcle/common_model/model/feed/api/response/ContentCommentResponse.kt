package com.castcle.common_model.model.feed.api.response

import com.castcle.common_model.model.feed.*
import com.google.gson.annotations.SerializedName
import java.util.*

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

data class ContentCommentResponse(
    @SerializedName("payload")
    val payload: List<PayloadResponse>,

    @SerializedName("pagination")
    val pagination: Pagination
)

fun List<PayloadResponse>.toFeedCommentList(): List<ContentDbModel> {
    return map { it ->
        ContentDbModel(
            id = it.id,
            featureSlug = it.feature?.slug ?: "",
            circleSlug = "",
            contentType = it.type,
            created = it.created,
            updated = it.updated,
            payloadContent = it.payload.toPayloadContentUiModel(),
            commented = it.commentedResponse?.toCommentedUiModel() ?: CommentedUiModel(),
            liked = it.likedResponse?.toLikedUiModel() ?: LikedUiModel(),
            recasted = it.recastedResponse?.toRecastedUiModel() ?: RecastedUiModel(),
            author = it.author.toAuthorUiModel(),
            replyUiModel = it.reply?.let { item ->
                item.map {
                    it.toReplyUiModel()
                }
            } ?: emptyList()
        )
    }
}