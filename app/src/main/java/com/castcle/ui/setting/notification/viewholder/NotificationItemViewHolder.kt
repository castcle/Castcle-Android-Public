package com.castcle.ui.setting.notification.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.castcle.android.R
import com.castcle.android.databinding.ItemNotificationBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.notification.NotificationPayloadModel
import com.castcle.extensions.getColorResource
import com.castcle.extensions.loadCircleImage
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.NotificationItemClick
import com.castcle.ui.setting.notification.NotificationPagingAdapter

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
//  Created by sklim on 29/10/2021 AD at 14:16.

class NotificationItemViewHolder(
    private val binding: ItemNotificationBinding,
    private val clickItem: (Click) -> Unit
) : NotificationPagingAdapter.ViewHolder<NotificationPayloadModel>(binding.root) {

    override fun bindUiModel(uiModel: NotificationPayloadModel) {
        super.bindUiModel(uiModel)
        with(binding) {
            ivAvatar.loadCircleImage(uiModel.avatar)
            tvMessage.text = uiModel.message
            if (uiModel.read) {
                tvMessage.setTextColor(root.context.getColorResource(R.color.gray_light))
            }
        }
        itemView.subscribeOnClick {
            clickItem.invoke(NotificationItemClick.ReadNotificationClick(uiModel))
        }.addToDisposables()
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            clickItem: (Click) -> Unit
        ): NotificationItemViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            val binding = ItemNotificationBinding.inflate(inflate, parent, false)
            return NotificationItemViewHolder(binding, clickItem)
        }
    }
}