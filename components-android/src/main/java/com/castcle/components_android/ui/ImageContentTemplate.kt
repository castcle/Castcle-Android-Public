package com.castcle.components_android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.castcle.android.components_android.databinding.LayoutImageContentTemplateBinding
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.components_android.ui.adapter.ImageTemplateFloxBoxAdapter
import com.google.android.flexbox.*

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
//  Created by sklim on 30/8/2021 AD at 11:55.

class ImageContentTemplate(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private lateinit var adapterImageTemplate: ImageTemplateFloxBoxAdapter
    private val fbLayoutManager: FlexboxLayoutManager = FlexboxLayoutManager(
        context
    )

    private var binding: LayoutImageContentTemplateBinding =
        LayoutImageContentTemplateBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    init {
        fbLayoutManager.flexWrap = FlexWrap.WRAP
        fbLayoutManager.flexDirection = FlexDirection.ROW
        fbLayoutManager.justifyContent = JustifyContent.SPACE_AROUND
        fbLayoutManager.justifyContent = JustifyContent.SPACE_BETWEEN
        fbLayoutManager.alignItems = AlignItems.FLEX_START
    }

    fun bindImageContent(itemUiModel: ContentUiModel) {
        with(binding) {
            rcImageContent.layoutManager = fbLayoutManager
            rcImageContent.adapter = ImageTemplateFloxBoxAdapter().also {
                adapterImageTemplate = it
            }
        }
        var listImage = itemUiModel.payLoadUiModel.photo.imageContent
        if (listImage.size > LIMNIT_MAX_IMAGE) {
            listImage = listImage.take(LIMNIT_MAX_IMAGE)
        }
        adapterImageTemplate.items = listImage
    }
}

private const val LIMNIT_MAX_IMAGE = 4
const val CORNER = 20f
