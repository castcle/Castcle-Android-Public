package com.castcle.components_android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.castcle.android.components_android.databinding.LayoutMenuTemplateBinding
import com.castcle.common_model.model.setting.SettingMenuUiModel
import com.castcle.components_android.ui.adapter.MenuHeaderTemplateAdapter
import com.castcle.components_android.ui.base.OnItemClickListener
import com.castcle.components_android.ui.base.TemplateClicks
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
//  Created by sklim on 7/9/2021 AD at 15:22.

class MenuTemplate(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs), OnItemClickListener {

    private val _clickMenu = BehaviorSubject.create<TemplateClicks>()
    val clickStatus: BehaviorSubject<TemplateClicks>
        get() = _clickMenu

    val binding: LayoutMenuTemplateBinding by lazy {
        LayoutMenuTemplateBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    private var adapter: MenuHeaderTemplateAdapter

    init {
        with(binding) {
            rvListMenu.adapter = MenuHeaderTemplateAdapter(this@MenuTemplate).also {
                adapter = it
            }
        }
    }

    fun bindingMenu(listMenu: List<SettingMenuUiModel>) {
        adapter.uiModels = listMenu
    }

    override fun onItemClick(templateClicks: TemplateClicks) {
        clickStatus.onNext(templateClicks)
    }
}
