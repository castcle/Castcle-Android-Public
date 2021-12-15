package com.castcle.common_model.model.userprofile

import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.userprofile.domain.CreateCastResponse
import com.google.gson.Gson

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
//  Created by sklim on 13/9/2021 AD at 11:42.

data class CreateContentUiModel(
    var id: String? = "",
    var type: String? = "",
    var message: String? = "",
    var content: String? = "",
    var created: String? = "",
    var updated: String? = "",
    val commentedUiModel: CommentedUiModel? = null,
    val photo: PhotoUiModel = PhotoUiModel(),
    val link: LikedUiModel? = null,
    val author: AuthorUiModel = AuthorUiModel(),
    val featureUiModel: FeatureUiModel? = null
)

fun CreateContentUiModel.toStringModel(): String {
    return Gson().toJson(this)
}

fun String.toCreateContentUiModel(): CreateContentUiModel {
    return Gson().fromJson(this, CreateContentUiModel::class.java)
}

fun CreateContentUiModel.toContentUiModel(): ContentUiModel {
    return ContentUiModel(
        id = id ?: "",
        featureSlug = featureUiModel?.slug ?: "",

    )
}