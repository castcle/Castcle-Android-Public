package com.castcle.ui.feed.feeddetail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.R
import com.castcle.android.components_android.databinding.ItemCommentChildTemplateBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ReplyUiModel
import com.castcle.extensions.*
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.CommentItemClick
import com.castcle.ui.feed.feeddetail.CommentedChildAdapter
import com.perfomer.blitz.setTimeAgo

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
//  Created by sklim on 26/8/2021 AD at 09:53.

class CommentedChildItemViewHolder(
    val binding: ItemCommentChildTemplateBinding,
    private val click: (Click) -> Unit
) : CommentedChildAdapter.ViewHolder<ReplyUiModel>(binding.root) {

    private lateinit var replyUiModel: ReplyUiModel

    init {
        binding.tvLiked.subscribeOnClick {
            click.invoke(
                CommentItemClick.CommentedLikeChildClick(
                    bindingAdapterPosition,
                    replyUiModel.id
                )
            )
        }
    }

    override fun bindUiModel(uiModel: ReplyUiModel) {
        super.bindUiModel(uiModel)
        replyUiModel = uiModel
        with(binding) {
            with(uiModel) {
                ivAvatar.loadCircleImage(author.avatar)
                tvUserName.text = author.displayName
                created.toTime()?.let {
                    tvDataTime.setTimeAgo(it)
                }
                tvCommentMessage.text = message
                with(tvLiked) {
//                    if (likedUiModel.liked) {
//                        setTextColor(binding.root.context.getColorResource(R.color.blue))
//                        isActivated = likedUiModel.liked
//                    }
                    text = binding.root.context.getString(
                        R.string.comment_item_like
                    ).format(0)
                }
            }
        }
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            clickItem: (Click) -> Unit
        ): CommentedChildItemViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = ItemCommentChildTemplateBinding.inflate(inflate, parent, false)
            return CommentedChildItemViewHolder(binding, clickItem)
        }
    }
}
