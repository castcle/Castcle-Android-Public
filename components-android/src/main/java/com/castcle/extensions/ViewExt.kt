package com.castcle.extensions

import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes

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
//  Created by sklim on 24/8/2021 AD at 09:53.

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.enabled() {
    this.isEnabled = true
}

fun View.disabled() {
    this.isEnabled = false
}

fun View.visibleOrGone(visible: Boolean, delay: Long) {
    if (delay <= 0L) {
        visibleOrGone(visible)
    } else {
        postDelayed({ visibleOrGone(visible) }, delay)
    }
}

fun View.visibleOrGone(visible: Boolean) {
    if (visible) visible() else gone()
}

fun View.visibleOrInvisible(visible: Boolean) {
    if (visible) visible() else invisible()
}

fun TextView.setClickableText(
    text: CharSequence,
    vararg onLinkClickListener: OnSpanClickListener,
    ignoreCase: Boolean = false
) {
    movementMethod = LinkMovementMethod.getInstance()

    val spannable = SpannableString(text)
    onLinkClickListener.forEach {
        val startIndex = spannable.indexOf(string = it.span, ignoreCase = ignoreCase)
        if (startIndex >= 0) {
            val endIndex = startIndex + it.span.length
            spannable.setSpan(it, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    this.text = spannable
}

abstract class OnSpanClickListener(val span: String) : ClickableSpan() {

    override fun onClick(widget: View) {
        onClick(widget as TextView, span)
        // Prevent CheckBox state from being toggled when link is clicked
        widget.cancelPendingInputEvents()
    }

    abstract fun onClick(textView: TextView, span: String)

    override fun updateDrawState(ds: TextPaint) {
        // override this method in order to keep predefined text font and color
    }
}

fun TextView.setTextStyle(@StyleRes styleResId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(styleResId)
    } else {
        setTextAppearance(context, styleResId)
    }
}

fun View.setBottomPaddingRes(@DimenRes dimenRes: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, resources.getDimensionPixelSize(dimenRes))
}

fun View.setTopPaddingRes(@DimenRes dimenRes: Int) {
    setPadding(paddingLeft, resources.getDimensionPixelSize(dimenRes), paddingRight, paddingBottom)
}

fun View.setTopPadding(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, paddingBottom)
}
