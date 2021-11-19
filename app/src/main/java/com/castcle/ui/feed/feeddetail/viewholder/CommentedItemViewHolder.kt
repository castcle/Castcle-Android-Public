package com.castcle.ui.feed.feeddetail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.R
import com.castcle.android.components_android.databinding.ItemCommentHeaderTemplateBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.LikedUiModel
import com.castcle.extensions.*
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.CommentItemClick
import com.castcle.ui.feed.feeddetail.CommentedAdapter
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

class CommentedItemViewHolder(
    val binding: ItemCommentHeaderTemplateBinding,
    private val click: (Click) -> Unit
) : CommentedAdapter.ViewHolder<ContentUiModel>(binding.root) {

    private lateinit var contentUiModel: ContentUiModel

    private var commentedAdapter: CommentedChildAdapter

    init {
        binding.rvChildComment.run {
            adapter = CommentedChildAdapter().also {
                commentedAdapter = it
            }
        }
        commentedAdapter.itemClick.subscribe {
            handleCommentedClick(it)
        }.addToDisposables()

        binding.tvLiked.subscribeOnClick {
            click.invoke(
                CommentItemClick.CommentedLikedItemClick(
                    bindingAdapterPosition,
                    contentUiModel
                )
            )
        }.addToDisposables()

        binding.tvReply.subscribeOnClick {
            click.invoke(
                CommentItemClick.CommentReplyClick(
                    bindingAdapterPosition,
                    contentUiModel
                )
            )
        }.addToDisposables()
    }

    private fun handleCommentedClick(it: Click?) {
        when (it) {
            is CommentItemClick.CommentedLikeChildClick -> {
                click.invoke(
                    CommentItemClick.CommentedLikeChildClick(
                        it.position,
                        it.replyId,
                        contentUiModel.id,
                        contentUiModel.payLoadUiModel.likedUiModel.liked
                    )
                )
            }
        }
    }

    override fun bindUiModel(uiModel: ContentUiModel) {
        super.bindUiModel(uiModel)
        contentUiModel = uiModel
        with(binding) {

            with(uiModel.payLoadUiModel) {
                ivAvatar.loadCircleImage(author.avatar)
                tvUserName.text = author.displayName
                onBindLikeComment(likedUiModel)
                created.toTime()?.let {
                    tvDataTime.setTimeAgo(it)
                }
                tvCommentMessage.text = contentMessage
                if (replyUiModel?.isNotEmpty() == true) {
                    lrLineRelation.visible()
                    rvChildComment.visible()
                    commentedAdapter.uiModels = replyUiModel ?: emptyList()
                }
            }
        }
    }

    private fun onBindLikeComment(likedUiModel: LikedUiModel) {
        with(binding) {
            with(tvLiked) {
                if (likedUiModel.liked) {
                    isActivated = likedUiModel.liked
                    setTextColor(binding.root.context.getColorResource(R.color.blue))
                } else {
                    isActivated = likedUiModel.liked
                    setTextColor(binding.root.context.getColorResource(R.color.white))
                }
                text = binding.root.context.getString(
                    R.string.comment_item_like
                ).format(likedUiModel.count)
            }
        }
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            clickItem: (Click) -> Unit
        ): CommentedItemViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = ItemCommentHeaderTemplateBinding.inflate(inflate, parent, false)
            return CommentedItemViewHolder(binding, clickItem)
        }
    }
}
