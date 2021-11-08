package com.castcle.ui.feed.feeddetail.viewholder

import android.view.ViewGroup
import com.castcle.android.databinding.LayoutItemProgressBarFooterBinding
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.extensions.getLayoutInflater
import com.castcle.ui.feed.feeddetail.CommentedAdapter

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
//  Created by sklim on 4/11/2021 AD at 11:28.

class ProgressBarViewHolder private constructor(
    viewBinding: LayoutItemProgressBarFooterBinding
) : CommentedAdapter.ViewHolder<ContentUiModel>(viewBinding.root) {
    companion object {
        operator fun invoke(parent: ViewGroup, attachToRoot: Boolean) =
            LayoutItemProgressBarFooterBinding.inflate(
                parent.getLayoutInflater(),
                parent,
                attachToRoot
            ).run(::ProgressBarViewHolder)
    }
}
