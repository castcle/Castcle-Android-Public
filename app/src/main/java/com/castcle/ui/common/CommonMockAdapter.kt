package com.castcle.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.data.staticmodel.ContentType.*
import com.castcle.ui.common.events.*
import com.castcle.ui.common.viewholder.feedMock.*
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
class CommonMockAdapter : RecyclerView.Adapter<CommonMockAdapter.ViewHolder<ContentFeedUiModel>>(),
    DiffUpdateAdapter,
    ItemClickable<Click> by ItemClickableImpl() {

    private val click: (Click) -> Unit = {
        notifyItemClick(it)
    }

    var uiModels: List<ContentFeedUiModel> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(
            old,
            new,
            { oldItem, newItem -> oldItem == newItem }
        )
    }

    fun onUpdateItemCommentedCount() {
        val indexItem = 0
        uiModels[indexItem].commented = true
        uiModels[indexItem].commentCount = uiModels[indexItem].commentCount.plus(1)
        notifyItemChanged(indexItem)
    }

    fun onUpdateItemLikeCount() {
        val indexItem = 0
        uiModels.firstOrNull()?.liked = true
        uiModels.firstOrNull()?.likeCount = uiModels.firstOrNull()?.likeCount?.plus(1) ?: 0
        notifyItemChanged(indexItem)
    }

    fun onUpdateItemUnLikeCount() {
        val indexItem = 0
        uiModels.firstOrNull()?.liked = false
        uiModels.firstOrNull()?.likeCount = uiModels.firstOrNull()?.likeCount?.minus(1) ?: 0
        notifyItemChanged(indexItem)
    }

    fun updateStateItemRecast() {
        val indexItem = 0
        uiModels.firstOrNull()?.recasted = true
        uiModels.firstOrNull()?.recastCount = uiModels.firstOrNull()?.recastCount?.plus(1) ?: 0
        notifyItemChanged(indexItem)
    }

    fun updateStateItemUnRecast() {
        val indexItem = 0
        uiModels.firstOrNull()?.recasted = false
        uiModels.firstOrNull()?.recastCount = uiModels.firstOrNull()?.recastCount?.minus(1) ?: 0
        notifyItemChanged(indexItem)
    }

    fun updateStateItemFollowing() {
        val indexItem = 0
        uiModels.firstOrNull()?.userContent?.followed = true
        notifyItemChanged(indexItem)
    }

    fun onUpdateRecastItem(contentUiModel: ContentFeedUiModel) {
        val indexItem = uiModels.indexOf(contentUiModel)
        uiModels[indexItem].recasted = !uiModels[indexItem].recasted
        uiModels[indexItem].recastCount = uiModels[indexItem].recastCount.plus(1)
        notifyItemChanged(indexItem)
    }

    override fun onBindViewHolder(holder: ViewHolder<ContentFeedUiModel>, position: Int) {
        holder.bindUiModel(uiModels[position])
    }

    override fun getItemCount(): Int {
        return uiModels.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommonMockAdapter.ViewHolder<ContentFeedUiModel> {
        return when (viewType) {
            R.layout.layout_feed_template_image ->
                FeedContentImageViewHolder.newInstance(parent, click)
            R.layout.layout_feed_template_blog ->
                FeedContentBlogViewHolder.newInstance(parent, click)
            R.layout.layout_feed_template_short ->
                FeedContentShortViewHolder.newInstance(parent, click)
            R.layout.layout_feed_template_short_web ->
                FeedContentShortWebViewHolder.newInstance(parent, click)
            R.layout.layout_feed_template_short_image ->
                FeedContentShortImageViewHolder.newInstance(parent, click)
            R.layout.layout_feed_template_quote ->
                FeedContentQuoteViewHolder.newInstance(parent, click)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getContentType(position)) {
            BLOG.type -> R.layout.layout_feed_template_blog
            SHORT.type -> optionalTypeShort(position)
            IMAGE.type -> R.layout.layout_feed_template_image
            QUOTE.type -> R.layout.layout_feed_template_quote
            else -> R.layout.layout_feed_template_short
        }
    }

    private fun optionalTypeShort(position: Int): Int {
        return when {
            uiModels[position].link == null &&
                uiModels[position].photo?.isNotEmpty() == true ->
                R.layout.layout_feed_template_short_image
            uiModels[position].link?.url.isNullOrBlank() &&
                uiModels[position].photo?.isNotEmpty() == true ->
                R.layout.layout_feed_template_short_image
            uiModels[position].link?.url?.isNotBlank() == true ->
                R.layout.layout_feed_template_short_web
            else -> R.layout.layout_feed_template_short
        }
    }

    private fun getContentType(position: Int): String {
        return uiModels[position].type
    }

    abstract class ViewHolder<UiModel : ContentFeedUiModel>(itemView: View) :
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
