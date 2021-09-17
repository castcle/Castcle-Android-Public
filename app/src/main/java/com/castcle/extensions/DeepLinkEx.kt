package com.castcle.extensions

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import androidx.core.net.toUri
import com.castcle.android.R
import kotlinx.android.parcel.Parcelize
import java.lang.String.format

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
//  Created by sklim on 9/9/2021 AD at 15:08.

@Parcelize
data class Input(
    val contentData: String?,
    val type: DeepLinkTarget,
) : Parcelable

enum class DeepLinkTarget(val resource: String) {
    VERIFY_EMAIL("resentverify-email/%s"),
    USER_PROFILE_ME("profile/me/%s"),
    USER_PROFILE_YOU("profile/you/%s"),
    HOME_FEED("feed")
}

fun makeDeepLinkUrl(context: Context, input: Input): Uri {
    val schema = context.getString(R.string.deep_link_scheme_castcle).toUrlScheme()
    return with(input) {
        if (contentData?.isNotBlank() == true) {
            "$schema${format(type.resource, contentData)}"
        } else {
            "$schema${type.resource}"
        }
    }.toUri()
}