package com.castcle.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
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

fun Activity.isExternalLink(uri: Uri): Boolean {
    return isHttpsOrSupportedHttpDeepLink(uri) &&
        uri.toString().contains(getString(R.string.deep_link_host)).not()
}

fun Activity.isHttpsOrSupportedHttpDeepLink(uri: Uri): Boolean {
    return uri.scheme == getString(R.string.link_scheme)
}

private fun Activity.navigateUri(uri: Uri, isOpenExternal: Boolean) {
    when {
        canAppHandleDeepLink(uri) -> {
            (this as? OnBoardActivity)?.let { onBoardActivity ->
                OnBoardActivity.start(onBoardActivity, uri)
            }
        }
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

private fun Activity.canAppHandleDeepLink(uri: Uri): Boolean {
    return when (this) {
        is OnBoardActivity -> onBoardNavigator.canHandleDeepLink(uri)
        else -> false
    }
}

private fun Activity.shouldNavigateToExternal(uri: Uri, isOpenExternal: Boolean): Boolean {
    return isOpenExternal
        || isHttps(uri).not()
}

fun Activity.isHttps(uri: Uri): Boolean {
    return uri.scheme == getString(R.string.link_scheme)
}

fun Activity.registerKeyboardListener(listener: KeyboardListener) {
    val content = findViewById<View>(android.R.id.content)
    listener.bindContent(content)
    content.viewTreeObserver.addOnGlobalLayoutListener(listener)
}

fun Activity.hideSoftKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Activity.getSoftInputMode(): Int {
    return window.attributes.softInputMode
}

fun Activity.setSoftInputMode(mode: Int) {
    window.setSoftInputMode(mode)
}

fun Activity.unregisterKeyboardListener(listener: KeyboardListener) {
    val content = findViewById<View>(android.R.id.content)
    content.viewTreeObserver.removeOnGlobalLayoutListener(listener)
}

abstract class KeyboardListener : ViewTreeObserver.OnGlobalLayoutListener {

    private var isShown = false

    lateinit var content: View

    internal fun bindContent(content: View) {
        this.content = content
    }

    override fun onGlobalLayout() {
        val rec = Rect()
        content.getWindowVisibleDisplayFrame(rec)

        // Finding screen height
        val screenHeight = content.rootView.height

        // Finding keyboard height
        val keypadHeight = screenHeight - rec.bottom

        val shown = keypadHeight > screenHeight * SCREEN_HEIGHT_MULTIPLIER
        if (isShown == shown) return
        isShown = shown
        onVisibilityStateChanged(isShown)
    }

    abstract fun onVisibilityStateChanged(isShown: Boolean)
}

private const val SCREEN_HEIGHT_MULTIPLIER = 0.15