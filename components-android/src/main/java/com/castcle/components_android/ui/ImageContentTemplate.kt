package com.castcle.components_android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.*
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.extensions.loadGranularRoundedCornersContentImage
import com.castcle.extensions.loadRoundedCornersImage

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

    private val binding: LayoutImageContentTemplateBinding by lazy {
        LayoutImageContentTemplateBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun bindImageContent(itemUiModel: ContentUiModel) {
        val listImage = itemUiModel.payLoadUiModel.photo.imageContent
        when (listImage.size) {
            SING_IMAGE -> {
                val bindingSingle =
                    ItemImageSingleContentBinding.inflate(
                        LayoutInflater.from(context),
                        this,
                        true
                    )
                bindingSingle.ivImageView.loadGranularRoundedCornersContentImage(
                    listImage.first(), CORNER, CORNER, CORNER, CORNER
                )
            }
            TWO_IMAGE -> {
                val bindingTwo =
                    ItemImageTwoContentBinding.inflate(
                        LayoutInflater.from(context),
                        this,
                        true
                    )
                bindingTwo.ivImageOne.loadGranularRoundedCornersContentImage(
                    listImage.first(),
                    topLeft = CORNER,
                    bottomLeft = CORNER
                )
                bindingTwo.ivImageTwo.loadGranularRoundedCornersContentImage(
                    listImage[1],
                    topRight = CORNER,
                    bottomRight = CORNER
                )
            }
            THREE_IMAGE -> {
                val bindingThree =
                    ItemImageThreeContentBinding.inflate(
                        LayoutInflater.from(context),
                        this,
                        true
                    )
                bindingThree.ivImageOne.loadGranularRoundedCornersContentImage(
                    listImage.first(),
                    topLeft = CORNER,
                )
                bindingThree.ivImageTwo.loadGranularRoundedCornersContentImage(
                    listImage[1],
                    topRight = CORNER,
                )
                bindingThree.ivImageThree.loadGranularRoundedCornersContentImage(
                    listImage[2],
                    bottomLeft = CORNER,
                    bottomRight = CORNER
                )
            }
            FOUR_IMAGE -> {
                val bindingFour =
                    ItemImageFourContentBinding.inflate(
                        LayoutInflater.from(context),
                        this,
                        true
                    )
                bindingFour.ivImageOne.loadGranularRoundedCornersContentImage(
                    listImage.first(),
                    topLeft = CORNER
                )
                bindingFour.ivImageTwo.loadGranularRoundedCornersContentImage(
                    listImage[1],
                    topRight = CORNER,
                )
                bindingFour.ivImageThree.loadGranularRoundedCornersContentImage(
                    listImage[2],
                    bottomLeft = CORNER
                )
                bindingFour.ivImageFour.loadGranularRoundedCornersContentImage(
                    listImage[3],
                    bottomRight = CORNER
                )
            }
        }
    }
}

private const val SING_IMAGE = 1
private const val TWO_IMAGE = 2
private const val THREE_IMAGE = 3
private const val FOUR_IMAGE = 4
const val CORNER = 20f
