package com.castcle.common_model.model.feed

import android.os.Parcelable
import com.castcle.common_model.model.feed.api.response.*
import kotlinx.parcelize.Parcelize

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
@Parcelize
data class ContentFeedUiModel(
    val id: String = "",
    val contentId: String = "",
    val authorId: String = "",
    var authorReference: List<String> = emptyList(),
    var contentQuoteCast: ContentFeedUiModel? = null,
    val referencedCastsId: String = "",
    val referencedCastsType: String = "",
    val message: String = "",
    val messageHeader: String = "",
    val messageContent: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    var userContent: UserContent = UserContent(),
    val photo: List<ImageContentUiModel>? = null,
    var type: String = "",
    val featureUiModel: FeatureUiModel = FeatureUiModel(),
    val circleUiModelUiModel: CircleUiModel = CircleUiModel(),
    var link: LinkUiModel? = null,
    var likeCount: Int = 0,
    var liked: Boolean = false,
    var commentCount: Int = 0,
    var commented: Boolean = false,
    var quoteCount: Int = 0,
    var quoted: Boolean = false,
    var recastCount: Int = 0,
    var recasted: Boolean = false,
    var isMindId: String = ""
) : Parcelable

@Parcelize
data class UserContent(
    val id: String = "",
    val type: String = "",
    val castcleId: String = "",
    val displayName: String = "",
    var followed: Boolean = false,
    val avatar: String = "",
    val verifiedEmail: Boolean = false,
    val verifiedMobile: Boolean = false,
    val verifiedOfficial: Boolean = false
) : Parcelable

fun mapToContentFeedUiMode(
    isMindId: String? = "",
    payLoadList: PayLoadList
): List<ContentFeedUiModel> {
    val contentList = mutableListOf<ContentFeedUiModel>()
    val contentMain = payLoadList.payLoadLists.map {
        mapContentFeedUiModelReference(isMindId, it, payLoadList.includes)
    }
    val contentMainRef = contentMain.map {
        Pair(it.id, it.userContent.castcleId)
    }

    val contentReference = payLoadList.includes?.casts?.map {
        mapContentFeedUiModelReference(isMindId, it, payLoadList.includes)
    }?.onEach { contentRef ->
        contentMainRef.filter {
            it.first == contentRef.id
        }.let { it ->
            contentRef.authorReference = it.map {
                it.second
            }
        }
    } ?: emptyList()

    contentList.apply {
        addAll(contentMain)
        addAll(contentReference)
    }

    return contentList
}

private val contentList = mutableListOf<ContentFeedUiModel>()

fun mapContentFeedReference(
    referencedCastsId: String,
    authorReferenceId: String,
    contentReference: IncludesResponse
): ContentFeedUiModel? {
    val userRef = mutableListOf<String>()
    contentReference.users.find {
        it.id == authorReferenceId
    }?.let {
        userRef.add(it.castcleId)
    }
    return contentReference.casts.find {
        it.id == referencedCastsId
    }?.toContentFeedUiModel()?.apply {
        authorReference = userRef
        userContent =
            contentReference.toAuthorContent(authorId) ?: UserContent()
    }
}

fun mapContentFeedUiModelReference(
    isMindId: String?,
    payload: Payload,
    includes: IncludesResponse?
): ContentFeedUiModel {
    return payload.toContentFeedUiModel().apply {
        this.isMindId = isMindId ?: ""
        userContent =
            includes?.toAuthorContent(this.authorId) ?: UserContent()
        if (referencedCastsType.isNotBlank()) {
            type = referencedCastsType
            if (referencedCastsType == QUOTECAST_TYPE) {
                contentQuoteCast = mapContentRefQuote(id, includes)
            }
        }
    }
}

fun mapContentRefQuote(contentRefId: String, includes: IncludesResponse?): ContentFeedUiModel? {
    return includes?.casts?.find {
        it.id == contentRefId
    }?.toContentFeedUiModel()
}

fun Payload.toContentFeedUiModel(): ContentFeedUiModel {
    return ContentFeedUiModel(
        id = id ?: "",
        contentId = payload.id,
        authorId = payload.authorId,
        message = payload.message,
        type = payload.type,
        createdAt = payload.created,
        updatedAt = payload.updated,
        photo = payload.photo?.contents?.map {
            it.toImageContentUiModel()
        },
        referencedCastsId = payload.referencedCasts?.id ?: "",
        referencedCastsType = payload.referencedCasts?.type ?: "",
        featureUiModel = feature.toFeatureUiModel(),
        circleUiModelUiModel = circle?.toCircleUiModel() ?: CircleUiModel(),
        link = payload.links?.firstOrNull()?.toLinkUiModel(),
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

const val RECASTED_TYPE = "recasted"
const val QUOTECAST_TYPE = "quoted"
