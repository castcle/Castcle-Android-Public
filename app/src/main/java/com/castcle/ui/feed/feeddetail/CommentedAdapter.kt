package com.castcle.ui.feed.feeddetail

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.ui.common.events.*
import com.castcle.ui.feed.feeddetail.viewholder.CommentedItemViewHolder
import com.castcle.ui.feed.feeddetail.viewholder.ProgressBarViewHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.extensions.LayoutContainer
import kotlin.math.abs
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
//  Created by sklim on 12/10/2021 AD at 12:34.

class CommentedAdapter : RecyclerView.Adapter<CommentedAdapter.ViewHolder<ContentUiModel>>(),
    DiffUpdateAdapter,
    ItemClickable<Click> by ItemClickableImpl() {

    private val click: (Click) -> Unit = {
        notifyItemClick(it)
    }

    var uiModels: List<ContentUiModel?> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(
            old,
            new,
            { oldItem, newItem -> oldItem == newItem }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onInsertComment(commented: ContentUiModel) {
        uiModels.toMutableList().add(commented)
        if (uiModels.isEmpty()) {
            notifyDataSetChanged()
        } else {
            notifyItemInserted(uiModels.lastIndex.plus(1))
        }
    }

    fun onUpdateReplyLiked(commentedId: String, replyId: String) {
        val updateLiked = uiModels
        var index = 0
        updateLiked.find {
            it?.id == commentedId
        }?.apply {
            payLoadUiModel.replyUiModel?.find {
                id == replyId
            }?.let {
                it.likedUiModel.liked = true
                it.likedUiModel.count = it.likedUiModel.count.plus(1)
            }
        }
        uiModels = updateLiked
        notifyItemChanged(index)
    }

    fun onUpdateReplyUnLiked(commentedId: String, replyId: String) {
        val updateLiked = uiModels
        var index = 0
        updateLiked.find {
            it?.id == commentedId
        }?.apply {
            payLoadUiModel.replyUiModel?.find {
                id == replyId
            }?.let {
                it.likedUiModel.liked = false
                it.likedUiModel.count = abs(it.likedUiModel.count.minus(1))
            }
        }
        uiModels = updateLiked
        notifyItemChanged(index)
    }

    fun onUpdateLiked(commentedId: String) {
        val updateLiked = uiModels
        var index = 0
        updateLiked.find {
            it?.id == commentedId
        }?.apply {
            index = uiModels.indexOf(this)
            payLoadUiModel.likedUiModel.liked = true
            payLoadUiModel.likedUiModel.count =
                payLoadUiModel.likedUiModel.count.plus(1)
        }
        uiModels = updateLiked
        notifyItemChanged(index)
    }

    fun onUpdateUnLiked(commentedId: String) {
        val updateLiked = uiModels
        var index = 0
        updateLiked.find {
            it?.id == commentedId
        }?.apply {
            index = uiModels.indexOf(this)
            payLoadUiModel.likedUiModel.liked = false
            payLoadUiModel.likedUiModel.count =
                abs(payLoadUiModel.likedUiModel.count - 1)
        }
        uiModels = updateLiked
        notifyItemChanged(index)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            uiModels[position] == null && position == itemCount - 1 -> VIEW_TYPE_PROGRESS_BAR
            else -> VIEW_TYPE_ITEM
        }
    }

    fun showLoading() {
        if (uiModels.isNotEmpty() && uiModels.last() == null) return

        uiModels = uiModels.toMutableList().also {
            it.add(null)
        }
    }

    fun hideLoading() {
        if (uiModels.isNotEmpty() && uiModels.last() != null) return

        uiModels = uiModels.toMutableList().also {
            it.removeLastOrNull()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<ContentUiModel>, position: Int) {
        uiModels[position]?.let { holder.bindUiModel(it) }
    }

    override fun getItemCount(): Int {
        return uiModels.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ContentUiModel> {
        return when (viewType) {
            VIEW_TYPE_ITEM -> CommentedItemViewHolder.newInstance(parent, click)
            VIEW_TYPE_PROGRESS_BAR -> ProgressBarViewHolder(parent, false)
            else -> throw UnsupportedOperationException("View type $viewType is not supported")
        }
    }

    abstract class ViewHolder<UiModel : ContentUiModel>(itemView: View) :
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

    companion object {
        private const val VIEW_TYPE_PROGRESS_BAR = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}