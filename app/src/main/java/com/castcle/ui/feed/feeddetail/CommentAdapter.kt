package com.castcle.ui.feed.feeddetail

import com.castcle.ui.feed.feeddetail.viewholder.CommentItemViewHolder
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.castcle.common_model.model.feed.ContentDbModel
import com.castcle.ui.common.events.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
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
//  Created by sklim on 24/8/2021 AD at 15:04.

class CommentAdapter : PagingDataAdapter<ContentDbModel, CommentAdapter.ViewHolder<ContentDbModel>>(
    ContentUiModelPagedListDiffCallBack
), ItemClickable<Click> by ItemClickableImpl() {

    private val click: (Click) -> Unit = {
        notifyItemClick(it)
    }

    override fun onBindViewHolder(holder: ViewHolder<ContentDbModel>, position: Int) {
        getItem(position)?.let { holder.bindUiModel(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ContentDbModel> {
        return CommentItemViewHolder.newInstance(parent, click)
    }

    abstract class ViewHolder<UiModel : ContentDbModel>(itemView: View) :
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

object ContentUiModelPagedListDiffCallBack : DiffUtil.ItemCallback<ContentDbModel>() {

    override fun areItemsTheSame(oldItem: ContentDbModel, newItem: ContentDbModel): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: ContentDbModel, newItem: ContentDbModel): Boolean {
        return oldItem == newItem
    }
}
