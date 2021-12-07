package com.castcle.components_android.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.ItemMenuBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.setting.MenuItem
import com.castcle.components_android.ui.base.*
import com.castcle.extensions.*

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

class MenuChildTemplateAdapter(
    listener: OnItemClickListener
) : BaseTemplateAdapter<OnItemClickListener, MenuItem, MenuChildTemplateAdapter.MenuChildViewHolder>(
    listener
) {

    public override var uiModels = listOf<MenuItem>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = mutableListOf<MenuItem>().apply {
                addAll(value)
            }
            notifyDataSetChanged()
        }

    override fun getLayoutId(position: Int): Int {
        return R.layout.item_menu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuChildViewHolder {
        val viewBinding = ItemMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuChildViewHolder(viewBinding, listener)
    }

    inner class MenuChildViewHolder(
        val binding: ItemMenuBinding,
        listener: OnItemClickListener
    ) : BaseViewHolder<OnItemClickListener, MenuItem>(binding.root, listener) {

        override fun onBind(item: MenuItem) {
            with(binding) {
                if (item.icon != 0) {
                    ivMenu.visible()
                    ivMenu.loadImageResource(item.icon)
                } else {
                    ivMenu.gone()
                }
                if(item.disableActionIcon){
                    ivAction.gone()
                }

                tvHeader.setText(item.menuName)
                if (item.menuDetail.isNotBlank()) {
                    tvDetail.visible()
                    tvDetail.text = item.menuDetail
                }
                itemView.subscribeOnClick {
                    listener?.onItemClick(
                        TemplateClicks.MenuClick(
                            item.menuType
                        )
                    )
                }
            }
        }
    }
}
