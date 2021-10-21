package com.castcle.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory

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
//  Created by sklim on 20/10/2021 AD at 11:48.

class ImageResize {

    fun resize(
        preferredWidth: Int,
        preferredHeight: Int,
        path: String,
        resizeType: ResizeType,
        skipLargerResize: Boolean = true,
    ): Bitmap {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            inSampleSize = calculateInSampleSize(this, preferredWidth, preferredHeight)
            inJustDecodeBounds = false
            if (!skipLargerResize || preferredWidth <= outWidth && preferredHeight <= outHeight) {
                val outRatio = outWidth.toFloat() / outHeight.toFloat()
                val reqRatio = preferredWidth.toFloat() / preferredHeight.toFloat()
                when {
                    (resizeType == ResizeType.Fill && outRatio > reqRatio) ||
                        (resizeType == ResizeType.Crop && outRatio <= reqRatio) -> {
                        inDensity = outWidth
                        inTargetDensity = preferredWidth * inSampleSize
                    }
                    (resizeType == ResizeType.Fill && outRatio <= reqRatio) ||
                        (resizeType == ResizeType.Crop && outRatio > reqRatio) -> {
                        inDensity = outHeight
                        inTargetDensity = preferredHeight * inSampleSize
                    }
                }
            }
            BitmapFactory.decodeFile(path, this)
        }
    }

    fun getOriginalImageSize(path: String): Pair<Int, Int> {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            outWidth to outHeight
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (width: Int, height: Int) = options.run { outWidth to outHeight }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}

const val MAX_IMAGE_WIDTH = 1080
const val MAX_IMAGE_HGIHT = 1920