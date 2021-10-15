package com.castcle.ui.feed.feeddetail

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.castcle.common_model.model.feed.ReplyUiModel
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.ui.common.events.*
import com.castcle.ui.feed.feeddetail.viewholder.CommentedChildItemViewHolder
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

class CommentedChildAdapter : RecyclerView.Adapter<CommentedChildAdapter.ViewHolder<ReplyUiModel>>(),
    DiffUpdateAdapter,
    ItemClickable<Click> by ItemClickableImpl() {

    private val click: (Click) -> Unit = {
        notifyItemClick(it)
    }

    var uiModels: List<ReplyUiModel> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(
            old,
            new,
            { oldItem, newItem -> oldItem == newItem }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<ReplyUiModel>, position: Int) {
        holder.bindUiModel(uiModels[position])
    }

    override fun getItemCount(): Int {
        return uiModels.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ReplyUiModel> {
        return CommentedChildItemViewHolder.newInstance(parent, click)
    }

    abstract class ViewHolder<UiModel : ReplyUiModel>(itemView: View) :
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
