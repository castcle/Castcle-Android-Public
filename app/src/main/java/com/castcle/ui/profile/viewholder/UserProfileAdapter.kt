package com.castcle.ui.profile.viewholder

import FeedContentShortUserProfileViewHolder
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.data.staticmodel.ContentType.*
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

class UserProfileAdapter :
    PagingDataAdapter<ContentUiModel, UserProfileAdapter.ViewHolder<ContentUiModel>>(
        ContentUiModelPagedListDiffCallBack
    ), ItemClickable<Click> by ItemClickableImpl() {

    private val click: (Click) -> Unit = {
        notifyItemClick(it)
    }

    override fun onBindViewHolder(holder: ViewHolder<ContentUiModel>, position: Int) {
        getItem(position)?.let { holder.bindUiModel(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ContentUiModel> {
        return when (viewType) {
            R.layout.layout_feed_template_short ->
                FeedContentShortUserProfileViewHolder.newInstance(parent, click)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getContentType(position)) {
            BLOG.type -> R.layout.layout_feed_template_blog
            SHORT.type -> R.layout.layout_feed_template_short
            IMAGE.type -> R.layout.layout_feed_template_image
            else -> R.layout.layout_feed_template_short
        }
    }

    private fun getContentType(position: Int): String {
        return getItem(position)?.contentType ?: ""
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
}

object ContentUiModelPagedListDiffCallBack : DiffUtil.ItemCallback<ContentUiModel>() {

    override fun areItemsTheSame(oldItem: ContentUiModel, newItem: ContentUiModel): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: ContentUiModel, newItem: ContentUiModel): Boolean {
        return oldItem == newItem
    }
}