package com.castcle.extensions

import java.text.DecimalFormat
import java.util.regex.*
import kotlin.math.ln
import kotlin.math.pow

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
//  Created by sklim on 27/8/2021 AD at 09:22.

fun Int.toCount(): String {
    val suffixChars = "KMGTPE"
    val formatter = DecimalFormat("###.#")

    if (this < 1000) {
        return this.toString()
    }
    val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
    return formatter.format(
        this.toDouble() / 1000.0.pow(exp.toDouble())
    ) + suffixChars[exp - 1]
}

fun String.isEmail() = this.matches(EMAIL_PATTERN.toRegex())

fun String.isPasswordPatten() = this.matches(PASSWORD_PATTERN.toRegex())

fun String.toUrlScheme(): String {
    return "$this$URL_SCHEME_EXT"
}

fun String.containsSomeOf(vararg keywords: String, ignoreCase: Boolean = false): Boolean {
    return keywords.any { this.contains(it, ignoreCase) }
}

private const val URL_SCHEME_EXT = "://"
private const val EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
    "\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\." +
    "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25})+"

private val PASSWORD_PATTERN: Pattern = Pattern.compile(
    "^" +
        "(?=.*[@#$%^&+=])" +  // at least 1 special character
        "(?=\\S+$)" +  // no white spaces
        ".{6,}" +  // at least c characters
        "$"
)

