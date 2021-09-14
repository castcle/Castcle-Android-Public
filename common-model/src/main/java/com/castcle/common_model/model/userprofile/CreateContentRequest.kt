package com.castcle.common_model.model.userprofile

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
//  Created by sklim on 13/9/2021 AD at 11:33.

data class CreateContentRequest(
    @SerializedName("authorId")
    val authorId: String = "",

    @SerializedName("payload")
    val payload: Payload = Payload(),

    @SerializedName("type")
    val type: String = "",

    val createType: String = "",

    @SerializedName("content")
    val contentImage: String? = null
)

data class Payload(
    @SerializedName("link")
    val link: List<Link>? = null,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("photo")
    val photo: Photo ? = null
)

data class Photo(
    @SerializedName("contents")
    val contents: List<Content>? = null,

    @SerializedName("cover")
    val cover: Content? = null,
)

data class Content(
    @SerializedName("url")
    val url: String? = ""
)

data class Link(
    @SerializedName("type")
    val type: String = "",

    @SerializedName("url")
    val url: String = ""
)

data class File(
    @SerializedName("file")
    val file: String? = ""
)
