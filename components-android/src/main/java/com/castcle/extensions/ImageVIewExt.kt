package com.castcle.extensions

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.castcle.android.components_android.R

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
//  Created by sklim on 26/8/2021 AD at 10:42.

fun ImageView.loadRoundedCornersImage(
    url: String,
    @Px roundRadius: Int = context.resources.getDimensionPixelSize(R.dimen.default_round_radius)
) {
    Glide.with(context)
        .load(url)
        .error(R.drawable.ic_img_placeholder)
        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(roundRadius)))
        .into(this)
}

fun ImageView.loadGranularRoundedCornersImage(
    url: String,
    topLeft: Float = 8f,
    topRight: Float = 8f,
    bottomLeft: Float = 0f,
    bottomRight: Float = 0f,
    @Px roundRadius: Int = context.resources.getDimensionPixelSize(R.dimen.default_round_radius)
) {
    Glide.with(context)
        .load(url)
        .error(R.drawable.ic_img_placeholder)
        .apply(
            RequestOptions().transform(
                CenterCrop(), GranularRoundedCorners(
                    topLeft,
                    topRight,
                    bottomRight,
                    bottomLeft
                )
            )
        )
        .into(this)
}

fun ImageView.loadGranularRoundedCornersContentImage(
    url: String,
    topLeft: Float = 0f,
    topRight: Float = 0f,
    bottomLeft: Float = 0f,
    bottomRight: Float = 0f,
    @Px roundRadius: Int = context.resources.getDimensionPixelSize(R.dimen.default_round_radius)
) {
    Glide.with(context)
        .load(url)
        .circleCrop()
        .error(R.drawable.ic_img_placeholder)
        .apply(
            RequestOptions().transform(
                CenterCrop(), GranularRoundedCorners(
                    topLeft,
                    topRight,
                    bottomRight,
                    bottomLeft
                )
            )
        )
        .into(this)
}

fun ImageView.loadRoundedCornersImageWithDefaultSize(
    url: String,
    @Px roundRadius: Int = context.resources.getDimensionPixelSize(R.dimen.default_round_radius)
) {
    Glide.with(context)
        .load(url)
        .error(R.drawable.ic_img_placeholder)
        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(roundRadius)))
        .override(
            Target.SIZE_ORIGINAL,
            Target.SIZE_ORIGINAL
        )
        .into(this)
}

fun ImageView.loadCircleImage(
    url: String,
    @DrawableRes errorDrawableRes: Int = R.drawable.ic_avatar_placeholder
) {
    Glide.with(context)
        .load(url)
        .error(errorDrawableRes)
        .circleCrop()
        .into(this)
}

fun ImageView.loadCircleImage(
    url: String,
    transform: (Bitmap) -> Bitmap
) {
    Glide.with(context)
        .load(url)
        .error(R.drawable.ic_avatar_placeholder)
        .circleCrop()
        .into(object : DrawableImageViewTarget(this) {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                val newResource = transform(resource.toBitmap()).toDrawable(resources)
                super.onResourceReady(newResource, transition)
            }
        })
}

fun ImageView.loadImageWithoutTransformation(
    url: String,
    @DrawableRes errorDrawableRes: Int = R.drawable.ic_img_placeholder
) {
    Glide.with(context)
        .load(url)
        .error(errorDrawableRes)
        .into(this)
}

fun ImageView.loadImage(url: String) {
    Glide.with(context)
        .load(url)
        .error(R.drawable.ic_img_placeholder)
        .centerCrop()
        .into(this)
}

fun ImageView.loadImage(url: String, requestOptions: RequestOptions) {
    Glide.with(context)
        .load(url)
        .centerCrop()
        .apply(requestOptions)
        .into(this)
}

fun ImageView.loadImageResource(@DrawableRes resId: Int) {
    Glide.with(context)
        .load(resId)
        .centerCrop()
        .into(this)
}