package com.castcle.ui.search.onsearch

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.ui.common.events.*
import com.castcle.ui.search.onsearch.viewholder.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.extensions.LayoutContainer
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
//  Created by sklim on 24/8/2021 AD at 15:04.

class SearchTrendAdapter : RecyclerView.Adapter<SearchTrendAdapter.ViewHolder<SearchUiModel>>(),
    DiffUpdateAdapter,
    ItemClickable<Click> by ItemClickableImpl() {

    private val click: (Click) -> Unit = {
        notifyItemClick(it)
    }

    var uiModels: List<SearchUiModel> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(
            old,
            new,
            { oldItem, newItem -> oldItem == newItem }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<SearchUiModel>, position: Int) {
        holder.bindUiModel(uiModels[position])
    }

    override fun getItemCount(): Int {
        return uiModels.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<SearchUiModel> {
        return when (viewType) {
            R.layout.item_search_has_tag ->
                SearchHasTagViewHolder.newInstance(parent, click)
            R.layout.item_search_person ->
                SearchPersonViewHolder.newInstance(parent, click)
            R.layout.item_search_suggestion ->
                SearchSuggestionViewHolder.newInstance(parent, click)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getViewType(uiModels[position])
    }

    private fun getViewType(searchUiModel: SearchUiModel): Int {
        return when (searchUiModel) {
            is SearchUiModel.SearchHasTagUiModel -> {
                R.layout.item_search_has_tag
            }
            is SearchUiModel.SearchFollowUiModel -> {
                R.layout.item_search_person
            }
            is SearchUiModel.SearchKeywordUiModel -> {
                R.layout.item_search_suggestion
            }
            else -> 0
        }
    }

    abstract class ViewHolder<UiModel : SearchUiModel>(itemView: View) :
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        lateinit var uiModel: UiModel

        override val containerView: View?
            get() = itemView

        fun Disposable.addToDisposables() = addTo(CompositeDisposable())

        @CallSuper
        open fun bindUiModel(uiModel: UiModel) {
            this.uiModel = uiModel
        }
    }
}
