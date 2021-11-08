package com.castcle.ui.common.dialog.recast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.databinding.ItemRecastUserBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.setting.PageUiModel
import com.castcle.common_model.model.userprofile.UserPage
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.extensions.loadRoundedCornersImage
import com.castcle.extensions.visibleOrGone
import com.castcle.ui.common.events.ItemClickable
import com.castcle.ui.common.events.ItemClickableImpl
import kotlin.properties.Delegates

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
//  Created by sklim on 21/9/2021 AD at 13:10.

class RecastAdapter : RecyclerView.Adapter<RecastAdapter.RecastViewHolder>(),
    DiffUpdateAdapter,
    ItemClickable<PageUiModel> by ItemClickableImpl() {

    private val click: (PageUiModel) -> Unit = {
        notifyItemClick(it)
    }

    var uiModels: List<PageUiModel> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(
            old,
            new,
            { oldItem, newItem -> oldItem == newItem }
        )
    }

    private var indexCurrent: Int = 0

    fun onPageSelected(userPage: PageUiModel) {
        val indexSelected = uiModels.indexOf(userPage)
        notifyItemChanged(indexCurrent)
        notifyItemChanged(indexSelected)
        indexCurrent = indexSelected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecastViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemRecastUserBinding.inflate(inflate, parent, false)
        return RecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecastViewHolder, position: Int) {
        holder.bindUiModel(uiModels[position])
    }

    override fun getItemCount(): Int {
        return uiModels.size
    }

    inner class RecastViewHolder(
        val binding: ItemRecastUserBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindUiModel(uiModel: PageUiModel) {
            with(binding) {
                ivAvatar.loadRoundedCornersImage(uiModel.avatarUrl)
                tvDisplayName.text = uiModel.displayName
                ivSelected.visibleOrGone(indexCurrent == bindingAdapterPosition)
                clBgItem.isActivated = indexCurrent == bindingAdapterPosition
            }
            itemView.subscribeOnClick {
                if (indexCurrent != bindingAdapterPosition) {
                    click.invoke(uiModel)
                }
            }
        }
    }
}
