package com.castcle.ui.search.onsearch.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.databinding.ItemSearchSuggestionBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.SearchItemClick
import com.castcle.ui.search.onsearch.SearchTrendAdapter

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
//  Created by sklim on 8/10/2021 AD at 10:59.

class SearchSuggestionViewHolder(
    private val binding: ItemSearchSuggestionBinding,
    private val click: (Click) -> Unit
) : SearchTrendAdapter.ViewHolder<SearchUiModel>(binding.root) {

    private lateinit var searchUiModel: SearchUiModel

    init {
        itemView.subscribeOnClick {
            click.invoke(
                SearchItemClick.SuggestionItemClick(
                    bindingAdapterPosition,
                    searchUiModel
                )
            )
        }.addToDisposables()
    }

    override fun bindUiModel(uiModel: SearchUiModel) {
        super.bindUiModel(uiModel)
        searchUiModel = uiModel
        if (uiModel is SearchUiModel.SearchKeywordUiModel)
            with(binding) {
                tvHastag.text = uiModel.text
            }
        if (uiModel is SearchUiModel.SearchResentUiModel)
            with(binding) {
                tvHastag.text = uiModel.keyword
            }
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            clickItem: (Click) -> Unit
        ): SearchSuggestionViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = ItemSearchSuggestionBinding.inflate(inflate, parent, false)
            return SearchSuggestionViewHolder(binding, clickItem)
        }
    }
}
