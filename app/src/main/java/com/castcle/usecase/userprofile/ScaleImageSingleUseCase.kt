package com.castcle.usecase.userprofile

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.userprofile.Content
import com.castcle.data.error.Ignored
import com.castcle.extensions.toBase64String
import com.castcle.usecase.base.SingleUseCase
import io.reactivex.Single
import java.io.File
import javax.inject.Inject


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
//  Created by sklim on 2/9/2021 AD at 11:34.

class ScaleImageSingleUseCase @Inject constructor(
    private val context: Context,
    rxSchedulerProvider: RxSchedulerProvider,
) : SingleUseCase<String, Content>(
    rxSchedulerProvider.io(),
    rxSchedulerProvider.main(),
    ::Ignored
) {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun create(input: String): Single<Content> {
        return Single.fromCallable {
            Content(scaleAndCompressImageFile(input.toUri()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun scaleAndCompressImageFile(uri: Uri): String {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitMaps = ImageDecoder.decodeBitmap(
            source
        )

        val tempFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, context.cacheDir)
        bitMaps.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, tempFile.outputStream())

        return tempFile.toBase64String()
    }

    private fun File.calculateBitmapOptions(): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        return options
    }
}

private const val TEMP_FILE_PREFIX = "scaled_"
private const val TEMP_FILE_SUFFIX = ".jpeg"
private const val JPEG_QUALITY = 90
