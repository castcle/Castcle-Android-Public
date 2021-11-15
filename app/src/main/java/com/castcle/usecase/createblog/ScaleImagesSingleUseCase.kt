package com.castcle.usecase.createblog

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.userprofile.domain.Content
import com.castcle.data.error.Ignored
import com.castcle.extensions.toBase64String
import com.castcle.usecase.base.SingleUseCase
import io.reactivex.Single
import me.shouheng.compress.Compress
import me.shouheng.compress.strategy.Strategies
import me.shouheng.compress.strategy.compress.Compressor
import me.shouheng.compress.strategy.config.ScaleMode.Companion.SCALE_SMALLER
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

class ScaleImagesSingleUseCase @Inject constructor(
    private val context: Context,
    rxSchedulerProvider: RxSchedulerProvider,
) : SingleUseCase<List<String>, List<Content>>(
    rxSchedulerProvider.io(),
    rxSchedulerProvider.main(),
    ::Ignored
) {

    private var compressorSourceType = SourceType.FILE

    override fun create(input: List<String>): Single<List<Content>> {
        return Single.fromCallable {
            input.map {
                Content(scaleAndCompressImageFile(it.toUri()))
            }
        }
    }

    private fun scaleAndCompressImageFile(uri: Uri): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            compressorSourceType = SourceType.BITMAP
            getCompress(uri).get().toBase64String()
        } else {
            getCompress(uri).get().toBase64String()
        }
    }

    private fun getCompress(path: Uri): Compressor {
        return when (compressorSourceType) {
            SourceType.FILE -> {
                Compress.with(context, path.toFile()).setConfig()
            }
            SourceType.BITMAP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, path)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    Compress.with(context, bitmap).setConfig()
                } else {
                    Compress.with(context, path.toFile()).setConfig()
                }
            }
        }
    }
}

fun Compress.setConfig(): Compressor {
    return this.setQuality(JPEG_QUALITY)
        .strategy(Strategies.compressor())
        .setScaleMode(SCALE_SMALLER)
}

enum class SourceType {
    FILE, BITMAP
}

const val TEMP_FILE_PREFIX = "scaled_"
const val TEMP_FILE_SUFFIX = ".jpeg"
const val JPEG_QUALITY = 85
