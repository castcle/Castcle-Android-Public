package com.castcle.common_model.model.login.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
//  Created by sklim on 6/9/2021 AD at 10:55.

sealed class ProfileBundle : Parcelable {

    @Parcelize
    data class ProfileWithEmail(
        var email: String = "",
        var displayName: String? = "",
        var castcleId: String = "",
        var imageAvatar: String? = "",
        var imageCover: String? = ""
    ) : ProfileBundle()

    @Parcelize
    data class CreatePage(
        var email: String = "",
        var displayName: String? = "",
        var castcleId: String = "",
        var imageAvatar: String? = "",
        var imageCover: String? = ""
    ) : ProfileBundle()

    @Parcelize
    data class ProfileEdit(
        var overview: String = "",
        var dob: String? = "",
        var castcleId: String = "",
        var profileType: String = "",
        val facebookLinks: String = "",
        val mediumLinks: String = "",
        val twitterLinks: String = "",
        val websiteLinks: String = "",
        val youtubeLinks: String = ""
    ) : ProfileBundle()

    @Parcelize
    data class PageEdit(
        var overview: String = "",
        var dob: String? = "",
        var castcleId: String = "",
        var profileType: String = "",
        val facebookLinks: String = "",
        val mediumLinks: String = "",
        val twitterLinks: String = "",
        val websiteLinks: String = "",
        val youtubeLinks: String = ""
    ) : ProfileBundle()

    @Parcelize
    data class ViewProfile(
        var overview: String = "",
        var dob: String? = "",
        var castcleId: String = "",
        var profileType: String = "",
        val facebookLinks: String = "",
        val mediumLinks: String = "",
        val twitterLinks: String = "",
        val websiteLinks: String = "",
        val youtubeLinks: String = ""
    ) : ProfileBundle()

    @Parcelize
    data class ProfileDelete(
        var castcleId: String = "",
        var profileType: String = "",
        var avatar: String = "",
        val displayName: String?
    ) : ProfileBundle()

    @Parcelize
    data class ProfileOtp(
        var email: String = "",
        var refCode: String = "",
        var expiresTime: String = "",
    ) : ProfileBundle()

    @Parcelize
    data class ReCastPage(
        var castcleId: String = "",
        val displayName: String = "",
        val avaterUrl: String = ""
    ) : ProfileBundle()
}

fun ProfileBundle.ProfileWithEmail.toCreatePage(): ProfileBundle.CreatePage {
    return ProfileBundle.CreatePage(
        email = email,
        displayName = displayName,
        castcleId = castcleId,
        imageAvatar = imageAvatar,
        imageCover = imageCover
    )
}