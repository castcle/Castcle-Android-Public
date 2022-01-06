package com.castcle.components_android.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.LayoutUserBarTemplateBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.feed.RECASTED_TYPE
import com.castcle.components_android.ui.base.addToDisposables
import com.castcle.components_android.ui.custom.event.TemplateEventClick
import com.castcle.components_android.ui.custom.timeago.setTimeAgoCus
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
//  Created by sklim on 26/8/2021 AD at 11:13.

class UserBarTemplate(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private val binding: LayoutUserBarTemplateBinding by lazy {
        LayoutUserBarTemplateBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }

    private lateinit var itemUiModel: ContentFeedUiModel

    private val _itemClick = BehaviorSubject.create<TemplateEventClick>()
    val itemClick: BehaviorSubject<TemplateEventClick>
        get() = _itemClick

    init {
        binding.ivAvatar.subscribeOnClick {
            onBindItemClick(
                TemplateEventClick.AvatarClick(
                    contentUiModel = itemUiModel
                )
            )
        }.addToDisposables()

        binding.tvUserName.subscribeOnClick {
            onBindItemClick(
                TemplateEventClick.AvatarClick(
                    contentUiModel = itemUiModel
                )
            )
        }.addToDisposables()

        binding.ivOptional.subscribeOnClick {
            onBindItemClick(
                TemplateEventClick.OptionalClick(
                    contentUiModel = itemUiModel
                )
            )
        }.addToDisposables()

        binding.tvStatusFollow.subscribeOnClick {
            onBindItemClick(
                TemplateEventClick.FollowingClick(
                    contentUiModel = itemUiModel
                )
            )
        }.addToDisposables()
    }

    private fun onBindItemClick(optionalClick: TemplateEventClick) {
        _itemClick.onNext(optionalClick)
    }

    fun bindUiModel(itemUiModel: ContentFeedUiModel, onBindQuote: Boolean = false) {
        this.itemUiModel = itemUiModel
        with(binding) {
            with(itemUiModel) {
                userContent.avatar.let {
                    ivAvatar.loadCircleImage(it)
                }
                tvUserName.text = userContent.displayName.run {
                    if (userContent.displayName.length > 15) {
                        userContent.displayName.replaceRange(
                            15,
                            userContent.displayName.length,
                            "..."
                        )
                    } else {
                        userContent.displayName
                    }
                }
                if (itemUiModel.isMindId) {
                    tvStatusFollow.gone()
                    ivStatusFollow.gone()
                } else {
                    with(tvStatusFollow) {
                        visibleOrGone(!itemUiModel.followed)
                        ivStatusFollow.gone()
                    }
                }

                if (itemUiModel.authorReference.isNotEmpty() &&
                    referencedCastsType == RECASTED_TYPE
                ) {
                    groupReCasted.visible()
                    val youRecasted = mapRecasted(itemUiModel)
                    tvReCasted.text = youRecasted
                }

                if (createdAt.isNotBlank()) {
                    createdAt.toTime()?.let {
                        tvDataTime.setTimeAgoCus(it, showSeconds = true, autoUpdate = true)
                    }
                }
            }
        }
    }

    private fun mapRecasted(itemUiModel: ContentFeedUiModel): String {
        val recasted = if (itemUiModel.authorReference.size > LIMITE_RECASTED) {
            val reCasted = if (itemUiModel.isMindId) {
                binding.root.context.getString(R.string.feed_content_me_recasted)
            } else {
                itemUiModel.authorReference.first()
            }
            val youRecast = binding.root.context.getString(R.string.feed_content_template_recast)
                .format(reCasted, itemUiModel.authorReference[2])
            binding.root.context.getString(R.string.feed_content_you_recasted).format(
                youRecast
            )
        } else {
            if (itemUiModel.isMindId) {
                binding.root.context.getString(R.string.feed_content_me_recasted)
            } else {
                binding.root.context.getString(R.string.feed_content_you_recasted).format(
                    itemUiModel.authorReference.first()
                )
            }
        }

        return recasted
    }
}

private const val LIMITE_RECASTED = 2
