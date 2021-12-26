package com.castcle.extensions

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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

fun ImageView.loadIconImage(
    url: String,
    @Px roundRadius: Int = context.resources.getDimensionPixelSize(R.dimen.default_round_radius)
) {
    val imageLoader = ImageLoader.Builder(context)
        .componentRegistry {
            add(SvgDecoder(context))
        }
        .build()
    this.load(url, imageLoader) {
        crossfade(true)
        crossfade(500)
        transformations(RoundedCornersTransformation(roundRadius.toFloat()))
    }
}

fun ImageView.loadRoundedCornersImageUri(
    url: String,
    thumbnailUrl: String,
    @Px roundRadius: Int = context.resources.getDimensionPixelSize(R.dimen.default_round_radius)
) {
    Glide.with(context)
        .load(url.toUri())
        .thumbnail(
            Glide.with(context)
                .load(thumbnailUrl)
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(roundRadius)))
        ).thumbnail(0.5f)
        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(roundRadius)))
        .transition(DrawableTransitionOptions.withCrossFade())
        .placeholder(R.drawable.ic_img_placeholder)
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
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(R.drawable.ic_img_placeholder)
        .transition(DrawableTransitionOptions.withCrossFade())
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
    thumbnailUrl: String,
    topLeft: Float = 0f,
    topRight: Float = 0f,
    bottomLeft: Float = 0f,
    bottomRight: Float = 0f,
    @Px roundRadius: Int = context.resources.getDimensionPixelSize(R.dimen.default_round_radius)
) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .thumbnail(
            Glide.with(context)
                .load(thumbnailUrl)
        ).thumbnail(0.05f)
        .circleCrop()
        .error(R.drawable.ic_img_placeholder)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(
            RequestOptions().transform(
                CenterCrop(), GranularRoundedCorners(
                    topLeft,
                    topRight,
                    bottomRight,
                    bottomLeft
                )
            )
        ).into(this)
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
        .diskCacheStrategy(DiskCacheStrategy.ALL)
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

fun ImageView.loadImageUrlWithTransformation(
    url: String,
    @DrawableRes errorDrawableRes: Int = R.drawable.ic_img_placeholder,
    transform: (Bitmap) -> Bitmap
) {
    Glide.with(context)
        .load(url)
        .error(errorDrawableRes)
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

fun ImageView.loadImageWithCache(url: String) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .error(R.drawable.ic_img_placeholder)
        .centerCrop()
        .into(this)
}

fun Bitmap.addVerticalMargin(@Px margin: Int): Bitmap {
    return Bitmap.createBitmap(width, height + 2 * margin, Bitmap.Config.ARGB_8888).applyCanvas {
        drawColor(Color.WHITE)
        drawBitmap(this@addVerticalMargin, 0f, margin.toFloat(), null)
    }
}