package com.castcle.ui.search.viewholder

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.R
import com.castcle.android.databinding.ItemRankBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.extensions.getDrawableRes
import com.castcle.extensions.toCount
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.SearchItemClick
import com.castcle.ui.search.SearchAdapter

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
//  Created by sklim on 7/10/2021 AD at 16:14.

class SearchTrendViewHolder(
    private val binding: ItemRankBinding,
    private val click: (Click) -> Unit
) : SearchAdapter.ViewHolder<SearchUiModel>(binding.root) {

    override fun bindUiModel(uiModel: SearchUiModel) {
        super.bindUiModel(uiModel)
        if (uiModel is SearchUiModel.SearchHasTagUiModel) {
            with(binding) {
                tvTrend.text = binding.root.context
                    .getString(R.string.search_is_trend).format(
                        uiModel.rank.plus(1)
                    )
                tvTrendCount.text = binding.root.context
                    .getString(R.string.search_cast_count).format(
                        uiModel.count.toCount()
                    )
                tvHastag.text = uiModel.slug
            }
            onBindClick(uiModel)
        }
    }

    private fun onBindClick(uiModel: SearchUiModel.SearchHasTagUiModel) {
        itemView.subscribeOnClick {
            click.invoke(
                SearchItemClick.HasTagItemClick(
                    bindingAdapterPosition,
                    uiModel
                )
            )
        }.addToDisposables()
    }

    private fun getIconRank(statusRank: String): Drawable? {
        return if (statusRank == TREND_UP) {
            binding.root.context.getDrawableRes(R.drawable.ic_rank_up)
        } else {
            binding.root.context.getDrawableRes(R.drawable.ic_rank_down)
        }
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            clickItem: (Click) -> Unit
        ): SearchTrendViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = ItemRankBinding.inflate(inflate, parent, false)
            return SearchTrendViewHolder(binding, clickItem)
        }
    }
}

private const val TREND_UP = "up"
