package com.castcle.common_model.model.feed.api.response

import com.castcle.common_model.model.userprofile.domain.ImageResponse
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
//  Created by sklim on 22/11/2021 AD at 12:25.

data class PayloadObjectContent(
    @SerializedName("id") var id: String? = null,
    @SerializedName("type") var type: String,
    @SerializedName("message") var message: String? = null,
    @SerializedName("photo") var photo: PhotoContents? = null,
    @SerializedName("authorId") var authorId: String,
    @SerializedName("link") val links: List<LinkResponse>? = null,
    @SerializedName("referencedCasts") val referencedCasts: ReferencedCasts? = null,
    @SerializedName("metrics") val metrics: Metrics? = null,
    @SerializedName("participate") val participate: Participate? = null,
    @SerializedName("createdAt") var created: String? = null,
    @SerializedName("updatedAt") var updated: String? = null,
)

data class PhotoContents(
    @SerializedName("contents") var contents: List<ImageResponse>? = null,
)

data class LinksContents(
    @SerializedName("type") var type: String,
    @SerializedName("url") var url: String,
    @SerializedName("imagePreview") var imagePreview: String,
)

data class ReferencedCasts(
    // quoted, recasted
    @SerializedName("type") var type: String,
    //Content id
    @SerializedName("id") var id: String,
)

data class Metrics(
    @SerializedName("likeCount") var likeCount: Int? = 0,
    @SerializedName("commentCount") var commentCount: Int? = 0,
    @SerializedName("quoteCount") var quoteCount: Int? = 0,
    @SerializedName("recastCount") var recastCount: Int? = 0,
)

data class Participate(
    @SerializedName("liked") var liked: Boolean = false,
    @SerializedName("commented") var commented: Boolean = false,
    @SerializedName("quoted") var quoted: Boolean = false,
    @SerializedName("recasted") var recasted: Boolean = false,
)

data class AggregatorContent(
    //createTime
    @SerializedName("type") var type: String,
)
