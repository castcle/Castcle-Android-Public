package com.castcle.ui.common.viewholder.feedMock

import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.components_android.databinding.LayoutFeedTemplateShortBinding
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.components_android.ui.custom.previewlinkurl.*
import com.castcle.extensions.loadGranularRoundedCornersImage
import com.castcle.extensions.visible
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.common.events.Click

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

class FeedContentShortMockViewHolder(
    val binding: LayoutFeedTemplateShortBinding,
    private val click: (Click) -> Unit
) : CommonMockAdapter.ViewHolder<ContentUiModel>(binding.root) {

    override fun bindUiModel(uiModel: ContentUiModel) {
        super.bindUiModel(uiModel)

        with(binding) {
            with(uiModel.payLoadUiModel) {
                ubUser.bindUiModel(uiModel)
                tvFeedContent.text = contentFeed
                ftFooter.bindUiModel(uiModel)
                if (link.isNotEmpty()) {
                    groupPreview.visible()
                    link.firstOrNull()?.url?.let {
                        PreViewLinkUrl(it, object : PreViewLinkCallBack {
                            override fun onComplete(urlInfo: UrlInfoUiModel) {
                                with(urlInfo) {
                                    ivPerviewUrl.loadGranularRoundedCornersImage(image)
                                    tvPreviewUrl.text = url
                                    tvPreviewHeader.text = title
                                    tvFeedContent.text = description
                                }
                            }

                            override fun onFailed(throwable: Throwable) {

                            }
                        }).fetchUrlPreview()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            clickItem: (Click) -> Unit
        ): FeedContentShortMockViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = LayoutFeedTemplateShortBinding.inflate(inflate, parent, false)
            return FeedContentShortMockViewHolder(binding, clickItem)
        }
    }
}
