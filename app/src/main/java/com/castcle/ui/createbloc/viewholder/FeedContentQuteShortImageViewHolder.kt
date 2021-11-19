import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.components_android.databinding.LayoutFeedTemplateQuoteShortImageBinding
import com.castcle.android.components_android.databinding.LayoutFeedTemplateShortImageBinding
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.components_android.ui.custom.event.TemplateEventClick
import com.castcle.extensions.gone
import com.castcle.extensions.visible
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.castcle.ui.createbloc.adapter.CommonQuoteCastAdapter

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

class FeedContentQuteShortImageViewHolder(
    val binding: LayoutFeedTemplateQuoteShortImageBinding,
    private val click: (Click) -> Unit
) : CommonQuoteCastAdapter.ViewHolder<ContentUiModel>(binding.root) {

    init {
        binding.ubUser.itemClick.subscribe {
            handleItemClick(it)
        }.addToDisposables()
        binding.clPreviewContentImage.icImageContent.imageItemClick.subscribe {
            handleItemClick(it)
        }.addToDisposables()
    }

    private fun handleItemClick(it: TemplateEventClick?) {
        when (it) {
            is TemplateEventClick.AvatarClick -> {
                click.invoke(
                    FeedItemClick.FeedAvatarClick(
                        bindingAdapterPosition,
                        it.contentUiModel
                    )
                )
            }
            is TemplateEventClick.LikeClick -> {
                click.invoke(
                    FeedItemClick.FeedLikeClick(
                        bindingAdapterPosition,
                        it.contentUiModel
                    )
                )
            }
            is TemplateEventClick.RecasteClick -> {
                click.invoke(
                    FeedItemClick.FeedRecasteClick(
                        bindingAdapterPosition,
                        it.contentUiModel
                    )
                )
            }
            is TemplateEventClick.CommentClick -> {
                click.invoke(
                    FeedItemClick.FeedCommentClick(
                        bindingAdapterPosition,
                        it.contentUiModel
                    )
                )
            }
            is TemplateEventClick.ImageClick -> {
                click.invoke(
                    FeedItemClick.FeedImageClick(
                        it.imageIndex,
                        it.contentUiModel
                    )
                )
            }
            else -> {
            }
        }
    }

    override fun bindUiModel(uiModel: ContentUiModel) {
        super.bindUiModel(uiModel)
        with(binding) {
            startLoadingPreViewShimmer()
            skeletonLoading.shimmerLayoutLoading.run {
                stopShimmer()
                setShimmer(null)
                gone()
            }
            with(uiModel.payLoadUiModel) {
                ubUser.bindUiModel(uiModel, true)
                tvFeedContent.text = contentMessage
                with(clPreviewContentImage) {
                    clInPreviewContentImage.visible()
                    icImageContent.bindImageContent(uiModel, true)
                }
                stopLoadingPreViewShimmer()
            }
        }
    }

    private fun stopLoadingPreViewShimmer() {
        with(binding) {
            inShimmerContentLoading.shimmerLayoutLoading.run {
                stopShimmer()
                setShimmer(null)
                gone()
            }
        }
    }

    private fun startLoadingPreViewShimmer() {
        with(binding) {
            inShimmerContentLoading.shimmerLayoutLoading.run {
                startShimmer()
                visible()
            }
        }
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            clickItem: (Click) -> Unit
        ): FeedContentQuteShortImageViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = LayoutFeedTemplateQuoteShortImageBinding.inflate(inflate, parent, false)
            return FeedContentQuteShortImageViewHolder(binding, clickItem)
        }
    }
}
