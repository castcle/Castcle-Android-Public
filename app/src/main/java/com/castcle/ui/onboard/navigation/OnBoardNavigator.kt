package com.castcle.ui.onboard.navigation

import android.content.Intent
import android.net.Uri
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.login.*
import com.castcle.common_model.model.setting.VerificationUiModel
import com.castcle.common_model.model.userprofile.LinksRequestUiModel
import com.castcle.ui.base.BaseNavigator
import pl.aprilapps.easyphotopicker.MediaFile

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
//  Created by sklim on 18/8/2021 AD at 16:21.

interface OnBoardNavigator : BaseNavigator {

    fun navigateToNotiflyLoginDialogFragment()

    fun navigateToWebView(url: String)

    fun navigateToLoginFragment()

    fun nvaigateToFeedFragment()

    fun navigateToGreetingFragment()

    fun navigateToEmailFragment()

    fun navigateToPassword(authBundle: AuthBundle)

    fun navigetToDisplayNameFragment(registerBundle: RegisterBundle, isCreatePage: Boolean = false)

    fun naivgetToProfileChooseImageFragment(
        profileBundle: ProfileBundle,
        isCreatePage: Boolean = false
    )

    fun naivgetToProfileVerifyEmailFragment(profileBundle: ProfileBundle)

    fun navigateToAboutYouFragment(
        profileBundle: ProfileBundle,
        isCreatePage: Boolean = false,
        onEditProfile: Boolean = false,
        onEditPage: Boolean = false
    )

    fun navigateToSettingFragment()

    fun navigateByDeepLink(deepLink: Uri, shouldPopStackToEntry: Boolean = true): Boolean

    fun canHandleDeepLink(deepLink: Uri): Boolean

    fun handleDeepLink(intent: Intent, shouldPopStackToEntry: Boolean)

    fun popBackStackToEntry(popInclusive: Boolean = false)

    fun navigateCreateBlogFragment()

    fun navigateToResentVerifyEmail(email: String)

    fun navigateToRecastDialogFragment(contentUiModel: ContentUiModel)

    fun navigateToCreateQuoteFragment(contentUiModel: ContentUiModel)

    fun navigateToLanguageFragment()

    fun navigateToAppLanguageFragment(isAppLanguage: Boolean)

    fun navigateToSettingProfileFragment()

    fun navigateToChangePasswordFragment()

    fun navigateToCreatePasswordFragment(verificationUiModel: VerificationUiModel)

    fun navigateToCompleteFragment(onDeletePage: Boolean = false, onAccountPage: Boolean = false)

    fun navigateToFeedDetailFragment(contentUiModel: ContentUiModel, isContent: Boolean = false)

    fun navigateToSearchTrendFragmrnt()

    fun navigateToTrendFragment(trendSlug: String)

    fun navigateToProfileFragment(castcle: String, profileType: String)

    fun navigateToGreetingPageFragment()

    fun navigateToAddLinksFragment(linksRequest: LinksRequestUiModel)

    fun navigateToDialogChooseFragment()

    fun navigateToCropAvatarImage(mediaFile: MediaFile)

    fun navigateToProfileChooseDialogFragment()

    fun navigateToProfileDeletePageFragment(profileEditBundle: ProfileBundle)

    fun navigateToNotificationFragment()
}
