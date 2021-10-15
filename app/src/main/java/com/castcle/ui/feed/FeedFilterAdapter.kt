package com.castcle.ui.feed

import android.annotation.SuppressLint
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.android.components_android.databinding.ItemFeedFilterBinding
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.extensions.getColorResource
import com.castcle.ui.common.events.ItemClickable
import com.castcle.ui.common.events.ItemClickableImpl
import kotlinx.android.extensions.LayoutContainer

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
//  Created by sklim on 30/8/2021 AD at 19:46.

class FeedFilterAdapter : RecyclerView.Adapter<FeedFilterAdapter.FilterViewHolder>(),
    ItemClickable<SearchUiModel> by ItemClickableImpl(), DiffUpdateAdapter {

    private var selectedPosition = 0

    var items: List<SearchUiModel> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val viewBinding = ItemFeedFilterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FilterViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.onBindFeed(items[position])
    }

    fun selectedFilter(itemFeedFilter: SearchUiModel) {
        val adapterPosition = items.indexOf(itemFeedFilter)
        notifyItemChanged(selectedPosition)
        notifyItemChanged(adapterPosition)
        selectedPosition = adapterPosition
    }

    fun selectedFilterDefault() {
        notifyItemChanged(selectedPosition)
        notifyItemChanged(0)
        selectedPosition = 0
    }

    override fun getItemCount(): Int = items.size

    inner class FilterViewHolder(val binding: ItemFeedFilterBinding) :
        RecyclerView.ViewHolder(binding.root), LayoutContainer {

        override val containerView: View
            get() = itemView

        fun onBindFeed(feedItem: SearchUiModel) {
            when (feedItem) {
                is SearchUiModel.SearchHasTagUiModel -> feedItem.run(::bindFeed)
            }
            bindOnClick(feedItem)
        }

        private fun bindOnClick(feedItem: SearchUiModel) {
            itemView.setOnClickListener {
                if (selectedPosition != bindingAdapterPosition) {
                    notifyItemClick(feedItem)
                }
            }
        }

        private fun bindFeed(filter: SearchUiModel.SearchHasTagUiModel) {
            binding.tvFilterName.text = filter.slug
            binding.tvFilterName.isActivated = selectedPosition == bindingAdapterPosition
            if (selectedPosition == bindingAdapterPosition) {
                bindActivate()
            } else {
                bindDeactivate()
            }
        }

        private fun bindActivate() {
            binding.tvFilterName.setTextColor(
                binding.root.context.getColorResource(
                    R.color.white
                )
            )
        }

        private fun bindDeactivate() {
            binding.tvFilterName.setTextColor(
                binding.root.context.getColorResource(
                    R.color.gray_light
                )
            )
        }
    }
}

sealed class FilterUiModel {
    data class FeedFilterUiModel(
        val hashTagId: String,
        val hashTagName: String
    ) : FilterUiModel()
}
