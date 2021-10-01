package com.castcle.ui.setting.language

import android.annotation.SuppressLint
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.databinding.ItemAppLanguageBinding
import com.castcle.android.databinding.ItemLanguageBinding
import com.castcle.common_model.model.setting.LanguageUiModel
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.extensions.visibleOrGone
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
//  Created by sklim on 29/9/2021 AD at 09:58.

class LanguageAdapter : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>(),
    ItemClickable<LanguageUiModel> by ItemClickableImpl(), DiffUpdateAdapter {

    var items: List<LanguageUiModel> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val viewBinding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LanguageViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.onBindFeed(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LanguageViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root), LayoutContainer {

        override val containerView: View
            get() = itemView

        fun onBindFeed(language: LanguageUiModel) {
            bindLanguage(language)
            bindOnClick(language)
        }

        private fun bindLanguage(language: LanguageUiModel) {
            with(binding) {
                tvDisplayName.text = language.display
                ivArrowRight.visibleOrGone(language.isSelected)
            }
        }

        private fun bindOnClick(language: LanguageUiModel) {
            binding.ivDelete.setOnClickListener {
                notifyItemClick(language)
            }
        }

    }
}

