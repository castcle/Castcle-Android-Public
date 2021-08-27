package com.castcle.common_model.model.feed

import android.os.Parcelable
import com.castcle.common_model.ContentBaseUiModel.CommonContentBaseUiModel.ContentFeedUiModel
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
//  Created by sklim on 24/8/2021 AD at 17:07.

fun List<FeedContentResponse>.toContentFeedUiModel(): ContentFeedUiModel {
    return ContentFeedUiModel(
        feedContentUiModel = map {
            it.toContentUiModel()
        }.toMutableList()
    )
}

@Parcelize
data class ContentUiModel(
    val id: String,
    val featureSlug: String,
    val circleSlug: String,
    val contentType: String,
    val created: String,
    val updated: String,
    val payLoadUiModel: PayLoadUiModel
) : Parcelable

fun FeedContentResponse.toContentUiModel(): ContentUiModel {
    return ContentUiModel(
        id = id,
        featureSlug = feature.slug,
        circleSlug = circle.slug,
        contentType = payload.type,
        created = created,
        updated = updated,
        payLoadUiModel = payload.toPayloadUiModel()
    )
}

@Parcelize
data class PayLoadUiModel(
    val headerFeed: String,
    val contentFeed: String,
    val photo: PhotoUiModel,
    val created: String,
    val updated: String,
    val link: List<LinkUiModel>,
    val likedUiModel: LikedUiModel,
    val commentedUiModel: CommentedUiModel,
    val reCastedUiModel: RecastedUiModel,
    val author: AuthorUiModel
) : Parcelable

fun PayloadResponse.toPayloadUiModel(): PayLoadUiModel {
    return PayLoadUiModel(
        headerFeed = payload.header ?: "",
        contentFeed = payload.content,
        photo = payload.photo.toPhotoUiMode(),
        created = created,
        updated = updated,
        link = payload.linkResponse.map {
            it.toLinkUiModel()
        },
        likedUiModel = likedResponse.toLikedUiModel(),
        commentedUiModel = commentedResponse.toCommentedUiModel(),
        reCastedUiModel = recastedResponse.toRecastedUiModel(),
        author = author.toAuthorUiModel()
    )
}


@Parcelize
data class PhotoUiModel(
    val imageCover: String? = null,
    val imageContent: List<String>
) : Parcelable

fun PhotoResponse.toPhotoUiMode() =
    PhotoUiModel(
        imageCover = cover?.url,
        imageContent = contents?.map {
            it.url
        } ?: emptyList()
    )

@Parcelize
data class LinkUiModel(
    val type: String,
    val url: String
) : Parcelable

fun LinkResponse.toLinkUiModel(): LinkUiModel {
    return LinkUiModel(
        type = type,
        url = url
    )
}

@Parcelize
data class LikedUiModel(
    val count: Int,
    val liked: Boolean,
    val participantUiModel: List<ParticipantUiModel>
) : Parcelable

fun LikedResponse.toLikedUiModel() =
    LikedUiModel(
        count = count,
        liked = liked,
        participantUiModel = participant.map {
            it.toParticipantUiModel()
        }
    )

@Parcelize
data class CommentedUiModel(
    val count: Int,
    val commented: Boolean,
    val participantUiModel: List<ParticipantUiModel>
) : Parcelable

fun CommentedResponse.toCommentedUiModel() =
    CommentedUiModel(
        count = count,
        commented = commented,
        participantUiModel = participant.map {
            it.toParticipantUiModel()
        }

    )

@Parcelize
data class RecastedUiModel(
    val count: Int,
    val recasted: Boolean,
    val participantUiModel: List<ParticipantUiModel>
) : Parcelable

fun RecastedResponse.toRecastedUiModel() =
    RecastedUiModel(
        count = count,
        recasted = recasted,
        participantUiModel = participant.map {
            it.toParticipantUiModel()
        }
    )

@Parcelize
data class ParticipantUiModel(
    val avatar: String,
    val id: String,
    val name: String,
    val type: String
) : Parcelable

fun Participant.toParticipantUiModel() =
    ParticipantUiModel(
        avatar = avatar,
        id = id,
        name = name,
        type = type
    )

@Parcelize
data class QuoteCast(
    val id: String
) : Parcelable

@Parcelize
data class AuthorUiModel(
    val avatar: String,
    val displayName: String,
    val followed: Boolean,
    val id: String,
    val type: String,
    val verified: Boolean
) : Parcelable

fun Author.toAuthorUiModel() =
    AuthorUiModel(
        avatar = avatar,
        displayName = displayName,
        followed = followed,
        id = id,
        type = type,
        verified = verified
    )