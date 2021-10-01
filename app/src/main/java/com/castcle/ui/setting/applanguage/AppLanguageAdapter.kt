package com.castcle.ui.setting.applanguage

import android.annotation.SuppressLint
import android.view.*
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.android.databinding.ItemAppLanguageBinding
import com.castcle.android.databinding.ItemEmptyBinding
import com.castcle.common_model.model.setting.LanguageUiModel
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.extensions.gone
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

class AppLanguageAdapter : RecyclerView.Adapter<AppLanguageAdapter.ViewHolder<LanguageUiModel>>(),
    ItemClickable<LanguageUiModel> by ItemClickableImpl(), DiffUpdateAdapter {

    var items: List<LanguageUiModel> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<LanguageUiModel> {
        return when (viewType) {
            R.layout.item_language -> {
                LanguageViewHolder(
                    ItemAppLanguageBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            R.layout.item_empty -> {
                EmptyViewHolder(
                    ItemEmptyBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> throw UnsupportedOperationException()

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isShow) {
            R.layout.item_language
        } else {
            R.layout.item_empty
        }
//        return R.layout.item_language
    }

    override fun onBindViewHolder(holder: ViewHolder<LanguageUiModel>, position: Int) {
        holder.bindUiModel(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class EmptyViewHolder(val binding: ItemEmptyBinding) :
        ViewHolder<LanguageUiModel>(binding.root) {

        override fun bindUiModel(uiModel: LanguageUiModel) {
            super.bindUiModel(uiModel)
            itemView.gone()
        }
    }

    inner class LanguageViewHolder(val binding: ItemAppLanguageBinding) :
        ViewHolder<LanguageUiModel>(binding.root) {

        override fun bindUiModel(uiModel: LanguageUiModel) {
            super.bindUiModel(uiModel)
            bindLanguage(uiModel)
            bindOnClick(uiModel)
        }

        private fun bindLanguage(language: LanguageUiModel) {
            with(binding) {
                tvDisplayName.text = language.title
                ivArrowRight.visibleOrGone(language.isSelected)
            }
        }

        private fun bindOnClick(language: LanguageUiModel) {
            itemView.setOnClickListener {
                notifyItemClick(language)
            }
        }

    }

    abstract class ViewHolder<UiModel : LanguageUiModel>(itemView: View) :
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        lateinit var uiModel: UiModel

        override val containerView: View?
            get() = itemView

        @CallSuper
        open fun bindUiModel(uiModel: UiModel) {
            this.uiModel = uiModel
        }
    }
}

