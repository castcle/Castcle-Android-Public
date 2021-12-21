import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.components_android.databinding.LayoutFeedTemplateShortWebBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.feed.LinkUiModel
import com.castcle.components_android.ui.custom.event.TemplateEventClick
import com.castcle.components_android.ui.custom.socialtextview.SocialTextView
import com.castcle.components_android.ui.custom.socialtextview.model.LinkedType
import com.castcle.data.staticmodel.ContentType
import com.castcle.extensions.*
import com.castcle.ui.common.CommonAdapter
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.chayangkoon.champ.linkpreview.LinkPreview
import com.chayangkoon.champ.linkpreview.common.LinkContent
import com.workfort.linkpreview.LinkData
import kotlinx.coroutines.*

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

class FeedContentShortWebViewHolder(
    val binding: LayoutFeedTemplateShortWebBinding,
    private val click: (Click) -> Unit
) : CommonAdapter.ViewHolder<ContentFeedUiModel>(binding.root) {

    private lateinit var contentFeedUiModel: ContentFeedUiModel

    init {
        binding.ubUser.itemClick.subscribe {
            handleItemClick(it)
        }.addToDisposables()
        binding.ftFooter.itemClick.subscribe {
            handleItemClick(it)
        }.addToDisposables()
        binding.clPreviewContent.clInPreviewContent.subscribeOnClick {
            handleItemClick(
                TemplateEventClick.WebContentClick(
                    contentFeedUiModel
                )
            )
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
            is TemplateEventClick.OptionalClick -> {
                click.invoke(
                    FeedItemClick.EditContentClick(
                        bindingAdapterPosition,
                        it.contentUiModel
                    )
                )
            }
            is TemplateEventClick.FollowingClick -> {
                click.invoke(
                    FeedItemClick.FeedFollowingClick(
                        it.contentUiModel
                    )
                )
            }
            is TemplateEventClick.WebContentClick -> {
                click.invoke(
                    FeedItemClick.WebContentClick(
                        it.contentUiModel
                    )
                )
            }
            is TemplateEventClick.WebContentMessageClick -> {
                click.invoke(
                    FeedItemClick.WebContentMessageClick(
                        it.url
                    )
                )
            }
            else -> {
            }
        }
    }

    val scope = CoroutineScope(Job() + Dispatchers.Main)

    @SuppressLint("SetTextI18n")
    override fun bindUiModel(uiModel: ContentFeedUiModel) {
        super.bindUiModel(uiModel)
        contentFeedUiModel = uiModel
        with(binding) {
            skeletonLoading.shimmerLayoutLoading.run {
                stopShimmer()
                setShimmer(null)
                gone()
            }
            with(uiModel) {
                binding.linkUiItem = link ?: LinkUiModel()

                ubUser.bindUiModel(uiModel)
                with(tvFeedContent) {
                    if (uiModel.type == ContentType.SHORT.type) {
                        appendLinkText(message)
                    } else {
                        setTextReadMore(message)
                        subscribeOnClick {
                            tvFeedContent.toggle()
                        }.addToDisposables()
                    }
                    setLinkClickListener(object : SocialTextView.LinkClickListener {
                        override fun onLinkClicked(linkType: LinkedType, matchedText: String) {
                            handleItemClick(
                                TemplateEventClick.WebContentMessageClick(
                                    matchedText
                                )
                            )
                        }
                    })
                }
                ftFooter.bindUiModel(uiModel)

                link?.let {
                    scope.launch {
                        val urlPreview = getContentWebPreview(it.url)
                        onBindContentImageWeb(it, urlPreview)
                    }
                }
            }
        }
    }

    private val linkPreview = LinkPreview.Builder().build()

    private suspend fun getContentWebPreview(urlPreview: String): LinkContent {
        if (urlPreview.isBlank()) {
            return LinkContent()
        }
        return withContext(Dispatchers.IO) {
            linkPreview.loadPreview(urlPreview)
        }
    }

    private fun onBindContentIconWeb(linkUiModel: LinkData) {
        stopLoadingPreViewShimmer()
        with(binding.clPreviewIconContent) {
            clInPreviewIconContent.visible()
            with(linkUiModel) {
                ivPerviewIconUrl.loadIconImage(favicon ?: "")
                tvIconPreview.text = url
                tvPreviewIconHeader.text = title
                tvPreviewIconContent.text = description
            }
        }
    }

    private fun onBindContentImageWeb(linkUiModel: LinkUiModel, urlPreview: LinkContent) {
        stopLoadingPreViewShimmer()
        linkUiModel.apply {
            linkTitle = urlPreview.title
            linkDescription = urlPreview.description
        }.run {
            with(binding.clPreviewContent) {
                clInPreviewContent.visible()
                binding.linkUiItem = linkUiModel
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
        ): FeedContentShortWebViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = LayoutFeedTemplateShortWebBinding.inflate(inflate, parent, false)
            return FeedContentShortWebViewHolder(binding, clickItem)
        }
    }
}
