package com.castcle.common_model.model.feed.api.response

import com.castcle.common_model.model.userprofile.domain.ImageResponse
import com.castcle.common_model.model.userprofile.domain.Verified
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
//  Created by sklim on 22/11/2021 AD at 12:52.

data class IncludesResponse(
    @SerializedName("users")
    val users: List<IncludesUserdata>,
    @SerializedName("casts")
    val casts: List<IncludesContentItemResponse>
)

data class IncludesContentItemResponse(
    @SerializedName("id") var id: String? = null,
    @SerializedName("authorId") var authorId: String,
    @SerializedName("type") var type: String,
    @SerializedName("message") var message: String? = null,
    @SerializedName("photo") var photo: PhotoContents? = null,
    @SerializedName("link") val links: List<LinkResponse>? = null,
    @SerializedName("referencedCasts") val referencedCasts: ReferencedCasts? = null,
    @SerializedName("participate") val participate: Participate? = null,
    @SerializedName("metrics") val metrics: Metrics? = null,
    @SerializedName("aggregator") val aggregator: AggregatorContent? = null,
    @SerializedName("createdAt") var created: String? = null,
    @SerializedName("updatedAt") var updated: String? = null,
)

data class IncludesUserdata(
    @SerializedName("castcleId")
    val castcleId: String? = null,
    @SerializedName("displayName")
    val displayName: String? = null,
    @SerializedName("followed")
    val followed: Boolean,
    @SerializedName("blocking")
    val blocking: Boolean,
    @SerializedName("blocked")
    val blocked: Boolean,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("avatar")
    val avatar: ImageResponse,
    @SerializedName("verified")
    val verified: Verified? = null
)
