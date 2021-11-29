package com.castcle.common_model.model.feed

import com.castcle.common_model.model.feed.api.response.*

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
//  Created by sklim on 22/11/2021 AD at 13:17.

data class ContentFeedUiModel(
    val id: String = "",
    val contentId: String = "",
    val authorId: String = "",
    val referencedCastsId: String = "",
    val referencedCastsType: String = "",
    val message: String = "",
    val messageHeader: String = "",
    val messageContent: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    var userContent: UserContent? = null,
    val photo: List<ImageContentUiModel>? = null,
    val type: String = "",
    val featureUiModel: FeatureUiModel,
    val circleUiModelUiModel: CircleUiModel,
    var link: LinkUiModel? = null,
    var likeCount: Int,
    var liked: Boolean = false,
    var commentCount: Int,
    var commented: Boolean = false,
    var quoteCount: Int,
    var quoted: Boolean = false,
    var recastCount: Int,
    var recasted: Boolean = false,
)

data class UserContent(
    val id: String = "",
    val type: String = "",
    val castcleId: String = "",
    val displayName: String = "",
    val followed: Boolean = false,
    val avatar: String = "",
    val verifiedEmail: Boolean = false,
    val verifiedMobile: Boolean = false,
    val verifiedOfficial: Boolean = false
)

fun mapToContentFeedUiMode(payLoadList: PayLoadList): List<ContentFeedUiModel> {
    val contentList = mutableListOf<ContentFeedUiModel>()
    payLoadList.payLoadLists.forEach { it ->
        mapContentFeedUiModel(it, payLoadList.includes).run {
            contentList.add(this)
            if (this.referencedCastsId.isNotBlank()) {
                payLoadList.includes?.let { contentIncludes ->
                    mapContentFeedReference(this.referencedCastsId, contentIncludes)?.let { it ->
                        contentList.add(it)
                        if (it.referencedCastsId.isNotBlank()) {
                            mapContentFeedReference(it.referencedCastsId, contentIncludes)?.let {
                                contentList.add(it)
                            }
                        }
                    }
                }
            }
        }
    }
    return contentList
}

fun mapContentFeedReference(
    referencedCastsId: String,
    contentReference: IncludesResponse
): ContentFeedUiModel? {
    return contentReference.casts.objectContent.find {
        it.id == referencedCastsId
    }?.toContentFeedUiModel()
}

fun mapContentFeedUiModel(payload: Payload, includes: IncludesResponse?): ContentFeedUiModel {
    return payload.toContentFeedUiModel().apply {
        userContent =
            includes?.toAuthorContent(this.authorId) ?: UserContent()
    }
}

fun mapContentFeedUiModelReference(
    payload: Payload,
    includes: IncludesResponse?
): ContentFeedUiModel {
    return payload.toContentFeedUiModel().apply {
        userContent =
            includes?.toAuthorContent(this.authorId) ?: UserContent()
    }
}

fun Payload.toContentFeedUiModel(): ContentFeedUiModel {
    return ContentFeedUiModel(
        id = id,
        contentId = payload.id,
        authorId = payload.authorId,
        message = payload.message,
        createdAt = payload.created,
        updatedAt = payload.updated,
        photo = payload.photo.contents?.map {
            it.toImageContentUiModel()
        } ?: emptyList(),
        referencedCastsId = payload.referencedCasts?.id ?: "",
        referencedCastsType = payload.referencedCasts?.type ?: "",
        featureUiModel = feature.toFeatureUiModel(),
        circleUiModelUiModel = circle?.toCircleUiModel() ?: CircleUiModel(),
        link = payload.links?.firstOrNull()?.toLinkUiModel() ?: LinkUiModel(),
        liked = payload.participate?.liked ?: false,
        likeCount = payload.metrics?.likeCount ?: 0,
        commented = payload.participate?.liked ?: false,
        commentCount = payload.metrics?.commentCount ?: 0,
        recasted = payload.participate?.liked ?: false,
        recastCount = payload.metrics?.recastCount ?: 0,
        quoted = payload.participate?.liked ?: false,
        quoteCount = payload.metrics?.quoteCount ?: 0,
    )
}

fun Circle.toCircleUiModel(): CircleUiModel {
    return CircleUiModel(
        id = id ?: "",
        slug = slug ?: "",
        name = name ?: "",
        key = key ?: ""
    )
}

fun IncludesResponse.toAuthorContent(authorId: String): UserContent? {
    return this.users.find {
        it.id == authorId
    }?.let {
        UserContent(
            id = it.id,
            type = it.type,
            castcleId = it.castcleId,
            avatar = it.avatar.thumbnail ?: "",
            displayName = it.displayName,
            followed = it.followed,
            verifiedOfficial = it.verified.official ?: false,
            verifiedMobile = it.verified.mobile ?: false,
            verifiedEmail = it.verified.email ?: false
        )
    }
}