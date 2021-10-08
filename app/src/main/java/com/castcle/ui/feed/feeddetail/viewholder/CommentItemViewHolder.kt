package com.castcle.ui.feed.feeddetail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.components_android.databinding.ItemCommentHeaderTemplateBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentDbModel
import com.castcle.components_android.ui.custom.timeago.TimeAgo
import com.castcle.extensions.*
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.CommentItemClick
import com.castcle.ui.feed.feeddetail.CommentAdapter
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
//  Created by sklim on 26/8/2021 AD at 09:53.

class CommentItemViewHolder(
    val binding: ItemCommentHeaderTemplateBinding,
    private val click: (Click) -> Unit
) : CommentAdapter.ViewHolder<ContentDbModel>(binding.root) {

    private lateinit var contentDbModel: ContentDbModel

    private var commentedAdapter: CommentedAdapter

    init {
        binding.rvChildComment.run {
            adapter = CommentedAdapter().also {
                commentedAdapter = it
            }
        }
        commentedAdapter.itemClick.subscribe {
            handleCommentedClick(it)
        }.addToDisposables()

        binding.tvReply.subscribeOnClick {
            CommentItemClick.CommentReplyClick(
                bindingAdapterPosition,
                contentDbModel
            )
        }.addToDisposables()
    }

    private fun handleCommentedClick(it: Click?) {
        when (it) {
            is CommentItemClick.CommentedLikeClick -> {
                click.invoke(
                    CommentItemClick.CommentedLikeClick(
                        it.position,
                        it.replyId,
                        contentDbModel.id
                    )
                )
            }
        }
    }

    override fun bindUiModel(uiModel: ContentDbModel) {
        super.bindUiModel(uiModel)

        with(binding) {
            with(uiModel) {
                ivAvatar.loadCircleImage(author?.avatar ?: "")
                tvUserName.text = author?.displayName
                val dateTime = if (TimeAgo.using(created.toTime()).isBlank()) {
                    created.toFormatDate()
                } else {
                    TimeAgo.using(created.toTime())
                }
                tvDataTime.text = dateTime
                tvCommentMessage.text = payloadContent?.message
                if (replyUiModel.isNotEmpty()) {
                    rvChildComment.visible()
                    commentedAdapter.uiModels = replyUiModel
                }
            }
        }
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            clickItem: (Click) -> Unit
        ): CommentItemViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = ItemCommentHeaderTemplateBinding.inflate(inflate, parent, false)
            return CommentItemViewHolder(binding, clickItem)
        }
    }
}
