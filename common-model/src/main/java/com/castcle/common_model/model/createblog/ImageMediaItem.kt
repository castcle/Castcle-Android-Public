package com.castcle.common_model.model.createblog

import androidx.annotation.DrawableRes
import java.util.*

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
//  Created by sklim on 16/9/2021 AD at 09:40.
sealed class MediaItem(
    open var id: String,
    open var uri: String = "",
    open var displayName: String = "",
    @DrawableRes open val imgRes: Int,
    open var isSelected: Boolean = false
) {
    data class ImageMediaItem(
        override val imgRes: Int,
        override var id: String,
        override var uri: String,
        override var displayName: String,
        var size: Long,
        var date: Date? = null,
        override var isSelected: Boolean = false
    ) : MediaItem(id, uri, displayName, imgRes, isSelected)

    data class OpenCamera(
        override var id: String,
        override var uri: String,
        override val imgRes: Int,
        override var displayName: String,
        override var isSelected: Boolean = false
    ) : MediaItem(id, uri, displayName, imgRes, isSelected)

    data class OpenGallery(
        override var id: String,
        override var uri: String,
        override val imgRes: Int,
        override var displayName: String,
        override var isSelected: Boolean = false
    ) : MediaItem(id, uri, displayName, imgRes, isSelected)
}

fun ArrayList<String>.toImageMediaItem(): List<MediaItem> {
    return map {
        MediaItem.ImageMediaItem(
            uri = it,
            size = 0,
            id = UUID.randomUUID().toString(),
            imgRes = 0,
            displayName = ""
        )
    }
}

fun List<MediaItem>.toListUri(): List<String> {
    return map { it.uri }
}
//MediaItem