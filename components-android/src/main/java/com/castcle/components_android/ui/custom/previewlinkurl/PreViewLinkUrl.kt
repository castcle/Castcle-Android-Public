package com.castcle.components_android.ui.custom.previewlinkurl

import android.net.Uri
import kotlinx.coroutines.*
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
//  Created by sklim on 25/8/2021 AD at 17:21.

class PreViewLinkUrl(
    val url: String,
    private val linkType: String = "",
    private var callback: PreViewLinkCallBack?
) {
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    private val imageExtensionArray = arrayOf(".gif", ".png", ".jpg", ".jpeg", ".bmp", ".webp")

    fun fetchUrlPreview(timeOut: Int = 30000) {
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            callback?.onFailed(throwable)
        }
        scope.launch(exceptionHandler) {
            fetch(timeOut)
        }
    }

    private suspend fun fetch(timeOut: Int = 30000) {
        lateinit var urlInfoItem: UrlInfoUiModel
        if (checkIsImageUrl()) {
            urlInfoItem = UrlInfoUiModel(url = url, image = url)
        } else {
            val document = getDocument(url, timeOut)
            urlInfoItem = parseHtml(document, linkType)
            urlInfoItem.url = url
        }
        callback?.onComplete(urlInfoItem)
    }

    private fun checkIsImageUrl(): Boolean {
        val uri = Uri.parse(url)
        var isImage = false
        for (imageExtension in imageExtensionArray) {
            if (uri.path != null && uri.path!!.toLowerCase(Locale.getDefault())
                    .endsWith(imageExtension)
            ) {
                isImage = true
                break
            }
        }
        return isImage
    }

    fun cleanUp() {
        scope.cancel()
        callback = null
    }

}