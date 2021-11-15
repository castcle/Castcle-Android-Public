package com.castcle.components_android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.castcle.android.components_android.databinding.LayoutQuoteBarTemplateBinding
import com.castcle.android.components_android.databinding.LayoutUserBarTemplateBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.components_android.ui.custom.event.TemplateEventClick
import com.castcle.extensions.*
import com.perfomer.blitz.setTimeAgo
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
//  Created by sklim on 26/8/2021 AD at 11:13.

class UserBarTemplate(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    val binding: LayoutUserBarTemplateBinding by lazy {
        LayoutUserBarTemplateBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    val bindingQute: LayoutQuoteBarTemplateBinding by lazy {
        LayoutQuoteBarTemplateBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    private lateinit var itemUiModel: ContentUiModel

    private val _itemClick = BehaviorSubject.create<TemplateEventClick>()
    val itemClick: BehaviorSubject<TemplateEventClick>
        get() = _itemClick

    init {
        binding.ivAvatar.subscribeOnClick {
            _itemClick.onNext(
                TemplateEventClick.AvatarClick(
                    contentUiModel = itemUiModel
                )
            )
        }
    }

    fun bindUiModel(itemUiModel: ContentUiModel, onBindQuote: Boolean = false) {
        this.itemUiModel = itemUiModel
        if (!onBindQuote) {
            bindingQute.group.gone()
            with(binding) {
                with(itemUiModel.payLoadUiModel) {
                    ivAvatar.loadCircleImage(author.avatar)
                    tvUserName.text = author.displayName
                    ivStatusFollow.visibleOrGone(author.followed)
                    tvStatusFollow.visibleOrGone(author.followed)
                    created.toTime()?.let {
                        tvDataTime.setTimeAgo(it)
                    }
                }
            }
        } else {
            binding.group.gone()
            with(bindingQute) {
                with(itemUiModel.payLoadUiModel) {
                    ivAvatar.loadCircleImage(author.avatar)
                    tvUserName.text = author.displayName
                    ivStatusFollow.visibleOrGone(author.followed)
                    tvStatusFollow.visibleOrGone(author.followed)
                    created.toTime()?.let {
                        tvDataTime.setTimeAgo(it)
                    }
                }
            }
        }
    }
}
