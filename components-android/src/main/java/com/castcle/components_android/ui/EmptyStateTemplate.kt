package com.castcle.components_android.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.LayoutEmptyTemplateBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.empty.EmptyState.*
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.components_android.ui.base.addToDisposables
import com.castcle.components_android.ui.custom.event.TemplateEventClick
import com.castcle.extensions.*
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
//  Created by sklim on 27/8/2021 AD at 09:43.

class EmptyStateTemplate(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {
    private val binding: LayoutEmptyTemplateBinding by lazy {
        LayoutEmptyTemplateBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private lateinit var itemUiModel: ContentUiModel
    private val _itemClick = BehaviorSubject.create<TemplateEventClick>()
    val itemClick: BehaviorSubject<TemplateEventClick>
        get() = _itemClick

    init {
        with(binding) {
            tvEmptySubTitleAction.subscribeOnClick {
                _itemClick.onNext(
                    TemplateEventClick.LikeClick(
                        itemUiModel,
                        itemUiModel.payLoadUiModel.likedUiModel.participantUiModel
                    )
                )
            }
        }
    }

    fun bindUiState(itemUiModel: EmptyState) {
        val (icon, title, subtitle) = iconState(itemUiModel)
        with(binding) {
            ivEmptyState.background = icon
            tvEmptyTitle.text = title
            if (itemUiModel == FEED_EMPTY) {
                tvEmptySubTitle.gone()
                tvEmptySubTitleAction.visible()
                tvEmptySubTitleAction.text = subtitle
                tvEmptySubTitleAction.subscribeOnClick {
                    _itemClick.onNext(TemplateEventClick.ReTryClick())
                }.addToDisposables()
            } else {
                tvEmptySubTitle.gone()
            }
            tvEmptySubTitle.text = subtitle
        }
    }

    private fun iconState(itemUiModel: EmptyState): Triple<Drawable?, String, String> {
        return when (itemUiModel) {
            FEED_EMPTY -> {
                Triple(
                    context.getDrawableRes(R.drawable.ic_feed_empty_state),
                    context.getString(R.string.empty_state_feed),
                    context.getString(R.string.empty_state_feed_action)
                )
            }
            NOTIFICATION_EMPTY -> {
                Triple(
                    context.getDrawableRes(R.drawable.ic_notification_empty),
                    context.getString(R.string.empty_state_noti),
                    context.getString(R.string.empty_state_noti_subtitle)
                )
            }
            PAGE_EMPTY -> {
                Triple(
                    context.getDrawableRes(R.drawable.ic_page_empty_state),
                    context.getString(R.string.empty_state_page),
                    context.getString(R.string.empty_state_page_subtitle)
                )
            }
            SEARCH_EMPTY -> {
                Triple(
                    context.getDrawableRes(R.drawable.ic_search_empty_state),
                    context.getString(R.string.empty_state_search),
                    context.getString(R.string.empty_state_search_subtitle)
                )
            }
        }
    }
}
