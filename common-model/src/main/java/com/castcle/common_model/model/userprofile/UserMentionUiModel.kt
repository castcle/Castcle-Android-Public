package com.castcle.common_model.model.userprofile

import com.castcle.common_model.model.feed.*

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
//  Created by sklim on 19/10/2021 AD at 12:47.
data class UserMentionUiModel(
    val userMention: List<MentionUiModel>,
    val paginationModel: PaginationModel
)

data class MentionUiModel(
    val id: String,
    val castcleId: String,
    val displayName: String? = null,
    val type: String,
    val images: ImageContentUiModel,
)

fun UserMentionResponse.toUserMentionUiModel(): UserMentionUiModel {
    return UserMentionUiModel(
        userMention = userMentionResponse.map {
            it.toMentionUiModel()
        },
        paginationModel = pagination.toPaginationModel()
    )
}

fun MentionResponse.toMentionUiModel(): MentionUiModel {
    return MentionUiModel(
        id = id,
        castcleId = castcleId,
        displayName = displayName,
        type = type ?: "",
        images = images.avatar.toImageContentUiModel()
    )
}