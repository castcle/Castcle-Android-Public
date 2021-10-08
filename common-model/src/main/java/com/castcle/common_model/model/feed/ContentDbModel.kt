package com.castcle.common_model.model.feed

import androidx.room.*
import com.castcle.common_model.model.feed.converter.*

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
@Entity(tableName = "comment")
@TypeConverters(
    ReplyConverter::class,
    AuthorConverter::class,
    CommentedConverter::class,
    LikedConverter::class,
    RecastedConverter::class,
    PayloadContentConverter::class
)
data class  ContentDbModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "",
    @ColumnInfo(name = "featureSlug")
    val featureSlug: String = "",
    @ColumnInfo(name = "circleSlug")
    val circleSlug: String = "",
    @ColumnInfo(name = "contentType")
    val contentType: String = "",
    @ColumnInfo(name = "created")
    val created: String = "",
    @ColumnInfo(name = "updated")
    val updated: String = "",
    @ColumnInfo(name = "payloadContent")
    var payloadContent: PayloadContentUiModel?,
    @ColumnInfo(name = "liked")
    var liked: LikedUiModel?,
    @ColumnInfo(name = "commented")
    val commented: CommentedUiModel?,
    @ColumnInfo(name = "recasted")
    val recasted: RecastedUiModel?,
    @ColumnInfo(name = "author")
    var author: AuthorUiModel?,
    @ColumnInfo(name = "replyUiModel")
    val replyUiModel: List<ReplyUiModel> = emptyList(),
)
