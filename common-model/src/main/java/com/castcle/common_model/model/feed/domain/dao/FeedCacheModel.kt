package com.castcle.common_model.model.feed.domain.dao

import androidx.room.*
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikedConverter
import com.castcle.common_model.model.feed.domain.converter.*

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
//  Created by sklim on 5/10/2021 AD at 18:14.
@Entity(tableName = "feedcache")
@TypeConverters(
    ContentFeedUiModelConverter::class,
    AuthorReferentConverter::class,
    UserContentConverter::class,
    FeatureUiModelConverter::class,
    ImageContentConverter::class,
    CircleUiModelConverter::class,
    LikedConverter::class,
    LinkUiModelConverter::class
)
data class FeedCacheModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "",
    @ColumnInfo(name = "contentId")
    val contentId: String = "",
    @ColumnInfo(name = "authorId")
    val authorId: String = "",
    @ColumnInfo(name = "authorReference")
    var authorReference: List<String>,
    @ColumnInfo(name = "contentQuoteCast")
    var contentQuoteCast: ContentFeedUiModel,
    @ColumnInfo(name = "referencedCastsId")
    val referencedCastsId: String = "",
    @ColumnInfo(name = "referencedCastsType")
    val referencedCastsType: String = "",
    @ColumnInfo(name = "message")
    val message: String = "",
    @ColumnInfo(name = "messageHeader")
    val messageHeader: String = "",
    @ColumnInfo(name = "messageContent")
    val messageContent: String = "",
    @ColumnInfo(name = "createdAt")
    val createdAt: String = "",
    @ColumnInfo(name = "updatedAt")
    val updatedAt: String = "",
    @ColumnInfo(name = "userContent")
    var userContent: UserContent,
    @ColumnInfo(name = "photo")
    val photo: List<ImageContentUiModel>,
    @ColumnInfo(name = "type")
    var type: String = "",
    @ColumnInfo(name = "featureUiModel")
    val featureUiModel: FeatureUiModel,
    @ColumnInfo(name = "circleUiModelUiModel")
    val circleUiModelUiModel: CircleUiModel,
    @ColumnInfo(name = "link")
    var link: LinkUiModel,
    @ColumnInfo(name = "likeCount")
    var likeCount: Int = 0,
    @ColumnInfo(name = "liked")
    var liked: Boolean = false,
    @ColumnInfo(name = "commentCount")
    var commentCount: Int = 0,
    @ColumnInfo(name = "commented")
    var commented: Boolean = false,
    @ColumnInfo(name = "quoteCount")
    var quoteCount: Int = 0,
    @ColumnInfo(name = "quoted")
    var quoted: Boolean = false,
    @ColumnInfo(name = "recastCount")
    var recastCount: Int = 0,
    @ColumnInfo(name = "recasted")
    var recasted: Boolean = false,
    @ColumnInfo(name = "reported")
    var reported: Boolean = false,
    @ColumnInfo(name = "isMindId")
    var isMindId: Boolean = false,
    @ColumnInfo(name = "followed")
    var followed: Boolean = false
)

fun List<ContentFeedUiModel>.toMapFeedCache(): List<FeedCacheModel> {
    return map {
        it.toFeedCacheModel()
    }
}

fun FeedCacheModel.toContentFeedUiModel(): ContentFeedUiModel {
    return ContentFeedUiModel(
        id = id,
        contentId = contentId,
        authorId = authorId,
        authorReference = authorReference,
        contentQuoteCast = contentQuoteCast,
        referencedCastsId = referencedCastsId,
        referencedCastsType = referencedCastsType,
        message = message,
        messageHeader = messageHeader,
        messageContent = messageContent,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userContent = userContent,
        photo = photo,
        type = type,
        featureUiModel = featureUiModel,
        circleUiModelUiModel = circleUiModelUiModel,
        link = link,
        likeCount = likeCount,
        liked = liked,
        commentCount = commentCount,
        commented = commented,
        quoteCount = quoteCount,
        quoted = quoted,
        recastCount = recastCount,
        recasted = recasted,
        isMindId = isMindId,
        followed = followed
    )
}

fun ContentFeedUiModel.toFeedCacheModel(): FeedCacheModel {
    return FeedCacheModel(
        id = id,
        contentId = contentId,
        authorId = authorId,
        authorReference = authorReference,
        contentQuoteCast = contentQuoteCast ?: ContentFeedUiModel(),
        referencedCastsId = referencedCastsId,
        referencedCastsType = referencedCastsType,
        message = message,
        messageHeader = messageHeader,
        messageContent = messageContent,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userContent = userContent,
        photo = photo ?: emptyList(),
        type = type,
        featureUiModel = featureUiModel,
        circleUiModelUiModel = circleUiModelUiModel,
        link = link ?: LinkUiModel(),
        likeCount = likeCount,
        liked = liked,
        commentCount = commentCount,
        commented = commented,
        quoteCount = quoteCount,
        quoted = quoted,
        recastCount = recastCount,
        recasted = recasted,
        isMindId = isMindId,
        followed = userContent.followed
    )
}
