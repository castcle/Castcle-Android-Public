package com.castcle.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.castcle.android.R
import com.castcle.ui.onboard.OnBoardActivity

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
//  Created by sklim on 24/8/2021 AD at 09:54.

fun Activity.openUri(url: String, isOpenExternal: Boolean = false) {
    val uri = Uri.parse(url)
    navigateUri(uri, isOpenExternal)
}

private fun Activity.navigateUri(uri: Uri, isOpenExternal: Boolean) {
    when {
        shouldNavigateToExternal(uri, isOpenExternal) -> {
            val intent = Intent(Intent.ACTION_VIEW, uri)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        else -> {
            when (this) {
                is OnBoardActivity -> onBoardNavigator.navigateToWebView(uri.toString())
            }
        }
    }
}

private fun Activity.shouldNavigateToExternal(uri: Uri, isOpenExternal: Boolean): Boolean {
    return isOpenExternal
        || isHttps(uri).not()
}

fun Activity.isHttps(uri: Uri): Boolean {
    return uri.scheme == getString(R.string.link_scheme)
}
