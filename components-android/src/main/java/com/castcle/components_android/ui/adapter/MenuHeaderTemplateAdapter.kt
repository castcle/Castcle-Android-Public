package com.castcle.components_android.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.ItemMenuHeaderBinding
import com.castcle.common_model.model.setting.SettingMenuUiModel
import com.castcle.components_android.ui.base.*
import com.castcle.extensions.gone
import io.reactivex.subjects.BehaviorSubject

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

class MenuHeaderTemplateAdapter(listener: OnItemClickListener) :
    BaseTemplateAdapter<
        OnItemClickListener,
        SettingMenuUiModel,
        MenuHeaderTemplateAdapter.MenuHeaderViewHolder>(
        listener
    ), OnItemClickListener {

    public override var uiModels = listOf<SettingMenuUiModel>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = mutableListOf<SettingMenuUiModel>().apply {
                addAll(value)
            }
            notifyDataSetChanged()
        }

    private val _itemClick = BehaviorSubject.create<TemplateClicks>()
    val itemClick: BehaviorSubject<TemplateClicks>
        get() = _itemClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHeaderViewHolder {
        val viewBinding = ItemMenuHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuHeaderViewHolder(viewBinding, listener)
    }

    inner class MenuHeaderViewHolder(
        val binding: ItemMenuHeaderBinding,
        listener: OnItemClickListener
    ) : BaseViewHolder<OnItemClickListener, SettingMenuUiModel>(binding.root, listener),
        OnItemClickListener {

        private val adapter by lazy { MenuChildTemplateAdapter(this) }

        private val _itemMenuClick = BehaviorSubject.create<TemplateClicks>()

        init {
            _itemMenuClick.subscribe {
                listener.onItemClick(it)
            }.addToDisposables()
        }

        override fun onItemClick(templateClicks: TemplateClicks) {
            _itemMenuClick.onNext(templateClicks)
        }

        override fun onBind(item: SettingMenuUiModel) {
            if (item.header != 0) {
                binding.tvHeader.setText(item.header)
            } else {
                binding.tvHeader.gone()
            }
            with(binding) {
                with(this@MenuHeaderViewHolder.adapter) {
                    this.uiModels = item.menuItem
                    rvItemMenu.adapter = this
                }
            }
        }
    }

    override fun onItemClick(templateClicks: TemplateClicks) {
        listener.onItemClick(templateClicks)
    }

    override fun getLayoutId(position: Int): Int {
        return R.layout.item_menu_header
    }
}
