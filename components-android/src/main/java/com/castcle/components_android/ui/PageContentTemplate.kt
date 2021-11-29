package com.castcle.components_android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.components_android.databinding.*
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.setting.PageUiModel
import com.castcle.components_android.ui.adapter.PageHeaderTemplateAdapter
import com.castcle.components_android.ui.base.*
import com.castcle.components_android.ui.custom.event.EndlessRecyclerViewScrollListener
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
//  Created by sklim on 7/9/2021 AD at 18:17.

class PageContentTemplate(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs), OnItemClickListener {

    private val _clickPage = BehaviorSubject.create<TemplateClicks>()
    val clickPage: BehaviorSubject<TemplateClicks>
        get() = _clickPage

    private val _onNextPage = BehaviorSubject.create<Unit>()
    val onNextPage: BehaviorSubject<Unit>
        get() = _onNextPage

    val binding: LayoutPageHeaderBinding by lazy {
        LayoutPageHeaderBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    private var adapterPage: PageHeaderTemplateAdapter

    init {
        val linearLayoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )
        with(binding) {
            with(rvPageItem) {
                addOnScrollListener(object :
                    EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                        _onNextPage.onNext(Unit)
                    }
                })
                adapter = PageHeaderTemplateAdapter(this@PageContentTemplate).also {
                    adapterPage = it
                }
                itemAnimator = null
            }

            tvAddPage.subscribeOnClick {
                _clickPage.onNext(TemplateClicks.AddPageClick)
            }.addToDisposables()
        }
    }

    override fun onItemClick(templateClicks: TemplateClicks) {
        clickPage.onNext(templateClicks)
    }

    fun bindPage(pageItem: List<PageUiModel>) {
        adapterPage.uiModels = pageItem
        with(binding.skeletonLoading) {
            shimmerLayoutLoading.run {
                stopShimmer()
                setShimmer(null)
                gone()
            }
        }
    }
}
