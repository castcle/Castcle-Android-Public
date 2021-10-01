package com.castcle.components_android.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.ItemPageHeaderBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.setting.PageUiModel
import com.castcle.components_android.ui.base.*
import com.castcle.extensions.getColorResource
import com.castcle.extensions.loadCircleImage

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
//  Created by sklim on 7/9/2021 AD at 16:38.

class PageHeaderTemplateAdapter(listener: OnItemClickListener) :
    BaseTemplateAdapter<
        OnItemClickListener,
        PageUiModel,
        PageHeaderTemplateAdapter.PageViewHolder>(
        listener
    ), OnItemClickListener {

    public override var uiModels = listOf<PageUiModel>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = mutableListOf<PageUiModel>().apply {
                addAll(value)
            }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val viewBinding = ItemPageHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PageViewHolder(viewBinding, listener)
    }

    inner class PageViewHolder(
        val binding: ItemPageHeaderBinding,
        listener: OnItemClickListener
    ) : BaseViewHolder<OnItemClickListener, PageUiModel>(binding.root, listener) {


        override fun onBind(item: PageUiModel) {
            with(binding.ivAvatar) {
                borderColor = binding.root.context.getColorResource(R.color.red_primary_warning)
                loadCircleImage(item.avatarUrl)
                itemView.subscribeOnClick {
                    listener?.onItemClick(
                        TemplateClicks.AvatarClick("")
                    )
                }.addToDisposables()
            }
        }
    }

    override fun onItemClick(templateClicks: TemplateClicks) {
        listener.onItemClick(templateClicks)
    }

    override fun getLayoutId(position: Int): Int {
        return R.layout.item_page_header
    }
}
