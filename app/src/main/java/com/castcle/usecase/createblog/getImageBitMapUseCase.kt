package com.castcle.usecase.createblog

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.castcle.android.R
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.createblog.MediaItem
import com.castcle.common_model.model.createblog.MediaItem.ImageMediaItem
import com.castcle.data.error.ImageError.GenerateImageError
import com.castcle.usecase.base.SingleUseCase
import io.reactivex.Single
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.*
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
//  Created by sklim on 15/9/2021 AD at 17:57.

class GetImagePathMapUseCase @Inject constructor(
    rxSchedulerProvider: RxSchedulerProvider,
    private val appContext: Context
) : SingleUseCase<Unit, List<MediaItem>>(
    rxSchedulerProvider.io(),
    rxSchedulerProvider.main(),
    ::GenerateImageError
) {
    var outputFile: File? = null
    var outputStream: OutputStream? = null
    private val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        ) else MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    override fun create(input: Unit): Single<List<MediaItem>> {
        return Single.just(getPathStream())
    }

    private fun getPathStream(): List<MediaItem> {
        val imagePath = queryPathImage()
        imagePath.add(
            0, MediaItem.OpenCamera(
                id = "",
                uri = "",
                imgRes = R.drawable.ic_camera,
                displayName = "",
                path = ""
            )
        )
        return imagePath.toList()
    }

    @SuppressLint("Recycle")
    private fun queryPathImage(): MutableList<MediaItem> {
        val allImagrPath = mutableListOf<MediaItem>()

        if (Build.VERSION.SDK_INT >= BUILD_VERSION_CODE_Q) {
            val sourceUri = getSourceUri()
            val queryString = getQuerySelection()

//            val cursor = appContext.contentResolver.query(
//                sourceUri, null,
//                queryString, null, MediaStore.Images.Media.DATE_ADDED
//            )
            val cursor = appContext.contentResolver.query(
                uriExternal,
                projection,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=?",
                arrayOf("image/jpeg", "image/png", "image/x-ms-bmp"),
                MediaStore.Images.Media.DATE_ADDED
            )

            if (cursor?.moveToLast() == true) {
                do {
                    val path = cursor.getString(cursor.getColumnIndex(projection[2]))

                    val id = cursor.getLong(cursor.getColumnIndex(projection[0]))
                    val name = cursor.getString(cursor.getColumnIndex(projection[1]))
                    val dateModifiedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                    val dateModified =
                        Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))

                    val contentUri = ContentUris.withAppendedId(
                        uriExternal,
                        id
                    )

                    if (name != null) {
                        val item = ImageMediaItem(
                            0,
                            id.toString(),
                            contentUri.toString(),
                            name,
                            dateModified,
                            path = path
                        )
                        allImagrPath.add(item)
                    }

                    if (allImagrPath.size == LIMIT_QUERY) {
                        break
                    }

                } while (cursor.moveToPrevious())
            }
            cursor?.close()
        } else {
            val parentDir = appContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            )
            val mainDir = File(parentDir, "DCIM/%")
            if (mainDir.exists().not()) mainDir.mkdir()

            outputFile = File(
                mainDir,
                File.separator + RECEIPT_SCREENSHOT_PREFIX_NAME + RECEIPT_SCREENSHOT_EXTENSION
            )
        }

        return allImagrPath
    }

    private fun makeSafeFile(path: String?): File? {
        return if (path == null || path.isEmpty()) {
            null
        } else try {
            File(path)
        } catch (ignored: Exception) {
            null
        }
    }

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED
    )

    private fun getSourceUri(): Uri {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    private fun getQuerySelection(): String? {
        return (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            TimeUnit.MICROSECONDS.toSeconds(
                formatter.parse("$day.$month.$year")?.time ?: 0
            )
        }

}

private val uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI
private val uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
private const val RECEIPT_SCREENSHOT_PREFIX_NAME = "Receipt-"
private const val RECEIPT_SCREENSHOT_EXTENSION = ".png"
private const val BUILD_VERSION_CODE_Q = 29
private const val LIMIT_QUERY = 30
private const val DEFAULT_FOLDER_NAME = "SDCARD"