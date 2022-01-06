package com.castcle.ui.common

import FeedContentQuoteViewHolder
import FeedContentShortImageViewHolder
import FeedContentShortViewHolder
import FeedContentShortWebViewHolder
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.webview.ContentType.*
import com.castcle.ui.common.CommonAdapter.ViewHolder
import com.castcle.ui.common.events.*
import com.castcle.ui.common.viewholder.feed.FeedContentBlogViewHolder
import com.castcle.ui.common.viewholder.feed.FeedContentImageViewHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.extensions.LayoutContainer
import kotlin.math.abs

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

class CommonAdapter : PagingDataAdapter<ContentFeedUiModel, ViewHolder<ContentFeedUiModel>>(
    ContentUiModelPagedListDiffCallBack
), ItemClickable<Click> by ItemClickableImpl() {

    private val click: (Click) -> Unit = {
        notifyItemClick(it)
    }

    override fun onBindViewHolder(holder: ViewHolder<ContentFeedUiModel>, position: Int) {
        getItem(position)?.let { holder.bindUiModel(it) }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateContentPost(contentUiModel: ContentFeedUiModel) {
        val content = snapshot()
        content.items.toMutableList().addAll(0, listOf(contentUiModel))
        notifyItemInserted(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDeleteContent(contentUiModel: ContentFeedUiModel) {
        val index = snapshot().indexOf(contentUiModel)
        notifyItemRemoved(index)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCommented(commentedCount: Int, contentUiModel: ContentFeedUiModel) {
        val contentIndex = snapshot().indexOf(contentUiModel)
        if (contentIndex > -1) {
            snapshot().items[contentIndex].apply {
                commentCount = commentCount.plus(commentedCount)
            }
            notifyItemChanged(contentIndex)
        }

    }

    fun updateStateItemRecast(contentUiModel: ContentFeedUiModel) {
        val index = snapshot().indexOf(contentUiModel)
        if (index > -1) {
            val recastUiModel = snapshot()[index]
            recastUiModel?.recasted = true
            recastUiModel?.recastCount = recastUiModel?.recastCount?.plus(1)!!
            notifyItemChanged(index)
        }
    }

    fun updateStateItemReported(contentUiModel: ContentFeedUiModel) {
        val index = snapshot().indexOf(contentUiModel)
        if (index > -1) {
            val recastUiModel = snapshot()[index]
            recastUiModel?.reported = true
            notifyItemChanged(index)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateStateItemFollowing(contentUiModel: ContentFeedUiModel) {
        snapshot().map {
            if (it?.userContent?.castcleId == contentUiModel.userContent.castcleId) {
                it.followed = true
            }
        }
        notifyDataSetChanged()
    }

    fun updateStateItemUnRecast(contentUiModel: ContentFeedUiModel) {
        val index = snapshot().indexOf(contentUiModel)
        if (index > -1) {
            val recastUiModel = snapshot()[index]
            recastUiModel?.recasted = false
            recastUiModel?.recastCount = recastUiModel?.recastCount?.minus(1)?.let { abs(it) }!!
            notifyItemChanged(index)
        }
    }

    fun updateStateItemLike(contentUiModel: ContentFeedUiModel) {
        val index = snapshot().indexOf(contentUiModel)
        if (index > -1) {
            val likeUiModel = snapshot()[index]
            likeUiModel?.liked = !likeUiModel?.liked!!
            likeUiModel.likeCount = likeUiModel.likeCount.plus(1)
            notifyItemChanged(index)
        }
    }

    fun updateStateItemUnLike(contentUiModel: ContentFeedUiModel) {
        val index = snapshot().indexOf(contentUiModel)
        if (index > -1) {
            val likeUiModel = snapshot()[index]
            likeUiModel?.liked = !likeUiModel?.liked!!
            likeUiModel.likeCount = abs(likeUiModel.likeCount.plus(-1))
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<ContentFeedUiModel> {
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
            SHORT.type, LONG.type -> optionalTypeShort(position)
            RECAST.type -> optionalTypeRecastShort(position)
            IMAGE.type -> R.layout.layout_feed_template_image
            QUOTE.type -> R.layout.layout_feed_template_quote
            else -> R.layout.layout_feed_template_short
        }
    }

    private fun optionalTypeShort(position: Int): Int {
        return when {
            getItem(position)?.link == null &&
                getItem(position)?.photo?.isNotEmpty() == true ->
                R.layout.layout_feed_template_short_image
            getItem(position)?.link?.url.isNullOrBlank() &&
                getItem(position)?.photo?.isNotEmpty() == true ->
                R.layout.layout_feed_template_short_image
            getItem(position)?.link?.url?.isNotBlank() == true ->
                R.layout.layout_feed_template_short_web
            else -> R.layout.layout_feed_template_short
        }
    }

    private fun optionalTypeRecastShort(position: Int): Int {
        return when {
            getItem(position)?.contentQuoteCast?.link == null &&
                getItem(position)?.contentQuoteCast?.photo?.isNotEmpty() == true ->
                R.layout.layout_feed_template_short_image
            getItem(position)?.contentQuoteCast?.link?.url.isNullOrBlank() &&
                getItem(position)?.contentQuoteCast?.photo?.isNotEmpty() == true ->
                R.layout.layout_feed_template_short_image
            getItem(position)?.contentQuoteCast?.link?.url?.isNotBlank() == true ->
                R.layout.layout_feed_template_short_web
            else -> R.layout.layout_feed_template_short
        }
    }

    private fun getContentType(position: Int): String {
        return getItem(position)?.type ?: ""
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

object ContentUiModelPagedListDiffCallBack : DiffUtil.ItemCallback<ContentFeedUiModel>() {

    override fun areItemsTheSame(
        oldItem: ContentFeedUiModel,
        newItem: ContentFeedUiModel
    ): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(
        oldItem: ContentFeedUiModel,
        newItem: ContentFeedUiModel
    ): Boolean {
        return oldItem.id == newItem.id
    }
}
