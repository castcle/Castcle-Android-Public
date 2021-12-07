package com.castcle.components_android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.LayoutUserFooterTemplateBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentFeedUiModel
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

class FeedFooterTemplate(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {
    val binding: LayoutUserFooterTemplateBinding by lazy {
        LayoutUserFooterTemplateBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private lateinit var itemUiModel: ContentFeedUiModel
    private val _itemClick = BehaviorSubject.create<TemplateEventClick>()
    val itemClick: BehaviorSubject<TemplateEventClick>
        get() = _itemClick

    init {
        with(binding) {
            tvLiked.subscribeOnClick {
                _itemClick.onNext(
                    TemplateEventClick.LikeClick(
                        itemUiModel
                    )
                )
            }
            tvCommented.subscribeOnClick {
                _itemClick.onNext(
                    TemplateEventClick.CommentClick(
                        itemUiModel
                    )
                )
            }
            tvReCasted.subscribeOnClick {
                _itemClick.onNext(
                    TemplateEventClick.RecasteClick(
                        itemUiModel
                    )
                )
            }
        }
    }

    fun bindUiModel(itemUiModel: ContentFeedUiModel) {
        this.itemUiModel = itemUiModel
        with(binding) {
            with(itemUiModel) {

                tvLiked.text = likeCount.toCount()
                tvLiked.setTextColor(colorActivated(liked))
                tvLiked.isActivated = liked

                tvCommented.text = commentCount.toCount()
                tvCommented.setTextColor(colorActivated(commented))
                tvCommented.isActivated = commented

                tvReCasted.text = recastCount.toCount()
                tvReCasted.setTextColor(colorActivated(recasted))
                tvReCasted.isActivated = recasted
            }
        }
    }

    private fun colorActivated(status: Boolean): Int {
        return if (status) {
            context.getColorResource(R.color.blue)
        } else {
            context.getColorResource(R.color.white)
        }
    }

}
