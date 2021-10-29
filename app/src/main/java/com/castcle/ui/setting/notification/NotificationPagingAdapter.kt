package com.castcle.ui.setting.notification

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.common_model.model.notification.NotificationPayloadModel
import com.castcle.data.staticmodel.NotiItemType
import com.castcle.ui.common.events.*
import com.castcle.ui.setting.notification.viewholder.NotificationItemViewHolder
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

class NotificationPagingAdapter :
    PagingDataAdapter<NotificationPayloadModel, NotificationPagingAdapter.ViewHolder<NotificationPayloadModel>>(
        NotificationPagedListDiffCallBack
    ), ItemClickable<Click> by ItemClickableImpl() {

    private val click: (Click) -> Unit = {
        notifyItemClick(it)
    }

    override fun onBindViewHolder(holder: ViewHolder<NotificationPayloadModel>, position: Int) {
        getItem(position)?.let { holder.bindUiModel(it) }
    }

    fun updateStateRead(contentUiModel: NotificationPayloadModel) {
        val index = snapshot().indexOf(contentUiModel)
        val notificationItem = snapshot()[index]
        notificationItem?.read = true
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<NotificationPayloadModel> {
        return when (viewType) {
            R.layout.item_notification ->
                NotificationItemViewHolder.newInstance(parent, click)
            R.layout.item_notification_head_line ->
                NotificationItemViewHolder.newInstance(parent, click)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getContentType(position)) {
            NotiItemType.NOTI_HEAD_LINE.type -> R.layout.item_notification
            NotiItemType.NOTI_ITEM.type -> R.layout.item_notification_head_line
            else -> R.layout.layout_feed_template_short
        }
    }

    private fun getContentType(position: Int): String {
        return if (getItem(position)?.timeLine == true) {
            NotiItemType.NOTI_HEAD_LINE.type
        } else {
            NotiItemType.NOTI_ITEM.type
        }
    }

    abstract class ViewHolder<UiModel : NotificationPayloadModel>(itemView: View) :
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

object NotificationPagedListDiffCallBack : DiffUtil.ItemCallback<NotificationPayloadModel>() {

    override fun areItemsTheSame(
        oldItem: NotificationPayloadModel,
        newItem: NotificationPayloadModel
    ): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(
        oldItem: NotificationPayloadModel,
        newItem: NotificationPayloadModel
    ): Boolean {
        return oldItem == newItem
    }
}
