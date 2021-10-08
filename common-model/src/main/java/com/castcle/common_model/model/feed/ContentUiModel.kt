package com.castcle.common_model.model.feed

import android.os.Parcelable
import com.castcle.common_model.ContentBaseUiModel.CommonContentBaseUiModel.ContentFeedUiModel
import com.castcle.common_model.model.feed.api.response.*
import com.castcle.common_model.model.userprofile.User
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

fun List<PayloadResponse>.toContentUiModel(): ContentFeedUiModel {
    return ContentFeedUiModel(
        feedContentUiModel = map {
            ContentUiModel(
                payLoadUiModel = it.toPayloadUiModel()
            )
        }.toMutableList()
    )
}

@Parcelize
data class ContentUiModel(
    val id: String = "",
    val featureSlug: String = "",
    val circleSlug: String = "",
    val contentType: String = "",
    val created: String = "",
    val updated: String = "",
    val payLoadUiModel: PayLoadUiModel = PayLoadUiModel(),
    var deepLink: String = ""
) : Parcelable

fun FeedContentResponse.toContentUiModel(): ContentUiModel {
    return ContentUiModel(
        id = id,
        featureSlug = feature.slug,
        circleSlug = circle?.slug ?: "",
        contentType = payload.type,
        created = created,
        updated = updated,
        payLoadUiModel = payload.toPayloadUiModel()
    )
}

@Parcelize
data class PayLoadUiModel(
    val contentId: String = "",
    val headerFeed: String = "",
    val contentFeed: String = "",
    val contentMessage: String = "",
    val photo: PhotoUiModel = PhotoUiModel(),
    val created: String = "",
    val updated: String = "",
    val link: List<LinkUiModel> = emptyList(),
    val likedUiModel: LikedUiModel = LikedUiModel(),
    val commentedUiModel: CommentedUiModel = CommentedUiModel(),
    val reCastedUiModel: RecastedUiModel = RecastedUiModel(),
    val author: AuthorUiModel = AuthorUiModel(),
    val featureContent: FeatureUiModel? = null,
    val replyUiModel: List<ReplyUiModel>? = null,
    val replyedUiModel: ReplyUiModel? = null
) : Parcelable

fun PayloadResponse.toPayloadUiModel(): PayLoadUiModel {
    return PayLoadUiModel(
        contentId = id,
        headerFeed = payload.header ?: "",
        contentFeed = payload.content ?: "",
        photo = payload.photo?.toPhotoUiMode() ?: PhotoUiModel(),
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

@Parcelize
data class PayloadContentUiModel(
    var header: String? = null,
    var message: String? = null,
    var content: String? = null,
    var photo: PhotoUiModel? = null,
    var link: List<LinkUiModel>? = null
) : Parcelable

fun PayloadContent.toPayloadContentUiModel(): PayloadContentUiModel {
    return PayloadContentUiModel(
        header = header,
        message = message,
        content = content,
        photo = photo?.toPhotoUiMode(),
        link = linkResponse?.map {
            it.toLinkUiModel()
        } ?: emptyList(),
    )
}

fun Feature.toFeatureUiModel() = FeatureUiModel(
    slug = slug,
    name = name,
    key = key
)

@Parcelize
data class FeatureUiModel(
    var slug: String? = null,
    var name: String? = null,
    var key: String? = null
) : Parcelable

@Parcelize
data class ReplyUiModel(
    var id: String,
    var created: String,
    var message: String,
    var author: AuthorUiModel
) : Parcelable

fun ReplyResponse.toReplyUiModel() =
    ReplyUiModel(
        id = id,
        created = created,
        message = message,
        author = author.toAuthorUiModel()
    )

@Parcelize
data class PhotoUiModel(
    val imageCover: String? = null,
    val imageContent: List<String> = emptyList()
) : Parcelable

fun PhotoResponse.toPhotoUiMode() =
    PhotoUiModel(
        imageCover = cover?.url ?: "",
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
    val count: Int = 0,
    var liked: Boolean = false,
    val participantUiModel: List<ParticipantUiModel> = emptyList()
) : Parcelable

fun LikedResponse.toLikedUiModel() =
    LikedUiModel(
        count = count,
        liked = liked,
        participantUiModel = participant?.map {
            it.toParticipantUiModel()
        } ?: emptyList()
    )

@Parcelize
data class CommentedUiModel(
    val count: Int = 0,
    var commented: Boolean = false,
    val participantUiModel: List<ParticipantUiModel> = emptyList()
) : Parcelable

fun CommentedResponse.toCommentedUiModel() =
    CommentedUiModel(
        count = count,
        commented = commented,
        participantUiModel = participant?.map {
            it.toParticipantUiModel()
        } ?: emptyList()

    )

@Parcelize
data class RecastedUiModel(
    val count: Int = 0,
    var recasted: Boolean = false,
    val participantUiModel: List<ParticipantUiModel> = emptyList()
) : Parcelable

fun RecastedResponse.toRecastedUiModel() =
    RecastedUiModel(
        count = count,
        recasted = recasted,
        participantUiModel = participant?.map {
            it.toParticipantUiModel()
        } ?: emptyList()
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
    val avatar: String = "",
    val castcleId: String = "",
    val displayName: String = "",
    val followed: Boolean = false,
    val id: String = "",
    val type: String = "",
    val verified: Boolean = false
) : Parcelable

fun Author.toAuthorUiModel() =
    AuthorUiModel(
        avatar = avatar ?: "",
        displayName = displayName ?: "",
        castcleId = castcleId ?: "",
        followed = followed ?: false,
        id = id,
        type = type,
        verified = verified ?: false
    )

fun User.toContentUiModel(): ContentUiModel {
    return ContentUiModel(
        payLoadUiModel = PayLoadUiModel(
            author = AuthorUiModel(
                avatar = avatar,
                displayName = castcleId,
                followed = followed,
            )
        )
    )
}