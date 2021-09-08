package com.castcle.components_android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.castcle.android.components_android.databinding.*
import com.castcle.common_model.model.setting.PageHeaderUiModel
import com.castcle.components_android.ui.adapter.PageHeaderTemplateAdapter
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
//  Created by sklim on 7/9/2021 AD at 18:17.

class PageContentTemplate(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs), OnItemClickListener {

    private val _clickPage = BehaviorSubject.create<TemplateClicks>()
    val clickPage: BehaviorSubject<TemplateClicks>
        get() = _clickPage

    val binding: LayoutPageHeaderBinding by lazy {
        LayoutPageHeaderBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    private var adapter: PageHeaderTemplateAdapter

    init {
        with(binding) {
            rvPageItem.adapter = PageHeaderTemplateAdapter(this@PageContentTemplate).also {
                adapter = it
            }
        }
    }

    override fun onItemClick(templateClicks: TemplateClicks) {
        clickPage.onNext(templateClicks)
    }

    fun bindPage(pageItem: PageHeaderUiModel) {
        adapter.uiModels = pageItem.pageUiItem
    }

}