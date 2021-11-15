package com.castcle.ui.common.events

import com.castcle.common_model.model.feed.ContentUiModel

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
//  Created by sklim on 26/8/2021 AD at 10:02.

sealed class CommentItemClick(
    position: Int
) : Click(position) {

    class CommentedLikedItemClick(
        position: Int,
        val contentUiModel: ContentUiModel
    ) : CommentItemClick(position)

    class CommentedLikeChildClick(
        position: Int,
        val replyId: String,
        val commentedId: String = "",
        val likeStatus: Boolean = false
    ) : CommentItemClick(position)

    class CommentReplyClick(
        position: Int,
        val contentUiModel: ContentUiModel
    ) : CommentItemClick(position)

    class CommentReplyItemClick(
        position: Int,
        val contentUiModel: ContentUiModel
    ) : CommentItemClick(position)
}
