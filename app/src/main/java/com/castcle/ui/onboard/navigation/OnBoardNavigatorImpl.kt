package com.castcle.ui.onboard.navigation

import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import androidx.annotation.Keep
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.*
import com.castcle.android.R
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.login.domain.*
import com.castcle.common_model.model.setting.VerificationUiModel
import com.castcle.common_model.model.userprofile.LinksRequestUiModel
import com.castcle.data.staticmodel.BottomNavigateStatic
import com.castcle.extensions.containsSomeOf
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.BaseNavigatorImpl
import com.castcle.ui.common.dialog.profilechoose.ProfileChooseDialogFragmentArgs
import com.castcle.ui.common.dialog.recast.RecastDialogFragmentArgs
import com.castcle.ui.common.dialog.user.UserChooseDialogFragmentArgs
import com.castcle.ui.createbloc.CreateQuoteFragmentArgs
import com.castcle.ui.createpost.CreatePostFragmentArgs
import com.castcle.ui.feed.feeddetail.FeedDetailFragmentArgs
import com.castcle.ui.profile.CropAvatarImageFragmentArgs
import com.castcle.ui.profile.ProfileFragmentArgs
import com.castcle.ui.report.ReportFragmentArgs
import com.castcle.ui.search.trend.TrendFragmentArgs
import com.castcle.ui.setting.applanguage.AppLanguageFragmentArgs
import com.castcle.ui.setting.changepassword.complete.CompleteFragmentArgs
import com.castcle.ui.setting.changepassword.createnewpassword.CreatePasswordFragmentArgs
import com.castcle.ui.setting.deleteaccount.DeletePageFragmentArgs
import com.castcle.ui.signin.aboutyou.AboutYouFragmentArgs
import com.castcle.ui.signin.aboutyou.addlink.AddLinksFragmentArgs
import com.castcle.ui.signin.createaccount.CreateAccountFragmentArgs
import com.castcle.ui.signin.createdisplayname.CreateDisplayNameFragmentArgs
import com.castcle.ui.signin.password.PasswordFragmentArgs
import com.castcle.ui.signin.profilechooseimage.ProfileChooseFragmentArgs
import com.castcle.ui.signin.verifyemail.ResentVerifyEmailFragmentArgs
import com.castcle.ui.signin.verifyemail.VerifyEmailFragmentArgs
import com.castcle.ui.webview.WebViewFragmentArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.aprilapps.easyphotopicker.MediaFile
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
//  Created by sklim on 18/8/2021 AD at 17:43.

class OnBoardNavigatorImpl @Inject constructor(
    private val localizedResources: LocalizedResources,
    private val activity: FragmentActivity
) : BaseNavigatorImpl(activity), OnBoardNavigator {

    override fun navigateToNotiflyLoginDialogFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToNotiflyLoginDialoginDialogFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.searchFragment -> {
                        navController.navigate(
                            R.id.actionSearchFragmentToNotiflyLoginDialoginDialogFragment
                        )
                    }
                    R.id.trendFragment -> {
                        navController.navigate(
                            R.id.actionTrendFragmentToNotiflyLoginDialoginDialogFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToWebView(url: String) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.dialogLoginFragment -> {
                        navController.navigate(
                            R.id.webviewFragment,
                            WebViewFragmentArgs(url).toBundle()
                        )
                    }
                    else -> {
                        navController.navigate(
                            R.id.webviewFragment,
                            WebViewFragmentArgs(url).toBundle()
                        )
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToLoginFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.dialogLoginFragment -> {
                        navController.navigate(
                            R.id.actionDialogLoginFragmentToLoginFragment
                        )
                    }
                    R.id.completeFragment -> {
                        navController.navigate(
                            R.id.actionCompleteFragmentToLoginFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun nvaigateToFeedFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.loginFragment -> {
                        navController.navigate(
                            R.id.actionLoginFragmentToFeedFragment
                        )
                    }
                    R.id.verifyEmailFragment -> {
                        navController.navigate(
                            R.id.verifyEmailFragmentToFeedFragment
                        )
                    }
                    R.id.aboutYouFragment -> {
                        navController.navigate(
                            R.id.actionAboutYouFragmentToFeedFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToGreetingFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.loginFragment -> {
                        navController.navigate(
                            R.id.actionLoginFragmentToGreetingFragment
                        )
                    }
                    R.id.dialogLoginFragment -> {
                        navController.navigate(
                            R.id.actionDialogLoginFragmentToGreetingFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToEmailFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                if (navController.currentDestination?.id == R.id.greetingFragment) {
                    navController.navigate(
                        R.id.actionGreetingFragmenToEmailFragment
                    )
                } else {
                    unsupportedNavigation()
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToPassword(authBundle: AuthBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                if (navController.currentDestination?.id == R.id.emailFragment) {
                    navController.navigate(
                        R.id.actionEmailFragmentToPasswordFragment,
                        PasswordFragmentArgs(authBundle).toBundle()
                    )
                } else {
                    unsupportedNavigation()
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigetToDisplayNameFragment(
        registerBundle: RegisterBundle,
        isCreatePage: Boolean
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.passwordFragment -> {
                        navController.navigate(
                            R.id.actionPasswordFragmentToCreateDisplayNameFragment,
                            CreateDisplayNameFragmentArgs(isCreatePage, registerBundle).toBundle()
                        )
                    }
                    R.id.greetingPageFragment -> {
                        navController.navigate(
                            R.id.actionGreetingPageFragmentToCreateDisplayProfileFragment,
                            CreateDisplayNameFragmentArgs(isCreatePage, registerBundle).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun naivgetToProfileChooseImageFragment(
        profileBundle: ProfileBundle,
        isCreatePage: Boolean
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                if (navController.currentDestination?.id == R.id.createDisplayProfileFragment) {
                    navController.navigate(
                        R.id.actionCreateDisplayNameFragmentToProfileChooseFragment,
                        ProfileChooseFragmentArgs(
                            profileBundle = profileBundle,
                            isCreatePage = isCreatePage
                        ).toBundle()
                    )
                } else {
                    unsupportedNavigation()
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun naivgetToProfileVerifyEmailFragment(profileBundle: ProfileBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                if (navController.currentDestination?.id == R.id.profileChooseFragment) {
                    navController.navigate(
                        R.id.actionProfileChooseFragmentToVerifyEmailFragment,
                        VerifyEmailFragmentArgs(profileBundle).toBundle()
                    )
                } else {
                    unsupportedNavigation()
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToAboutYouFragment(
        profileBundle: ProfileBundle,
        isCreatePage: Boolean
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.verifyEmailFragment -> {
                        navController.navigate(
                            R.id.verifyEmailFragmentToAboutYouFragment,
                            AboutYouFragmentArgs(
                                isCreatePage = isCreatePage,
                                profileBundle = profileBundle,
                            ).toBundle()
                        )
                    }
                    R.id.profileChooseFragment -> {
                        navController.navigate(
                            R.id.actionProfileChooseFragmentToAboutYouFragment,
                            AboutYouFragmentArgs(
                                isCreatePage = isCreatePage,
                                profileBundle = profileBundle,
                            ).toBundle()
                        )
                    }
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToAboutYouFragment,
                            AboutYouFragmentArgs(
                                isCreatePage = isCreatePage,
                                profileBundle = profileBundle,
                            ).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToSettingFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToSettingFragment
                        )
                    }
                    R.id.completeFragment -> {
                        navController.navigate(
                            R.id.actionCompleteFragmentToSettingFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.searchFragment -> {
                        navController.navigate(
                            R.id.actionSearchFragmentToSettingFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun findNavController(): NavController {
        return activity.findNavController(R.id.navHostContainer)
    }

    override fun navigateByDeepLink(
        deepLink: Uri,
        shouldPopStackToEntry: Boolean
    ): Boolean {
        val navController = findNavController()
        val targetGraph = findGraphThatHasDeepLink(deepLink)
        val bottomNavigationView: BottomNavigationView? =
            activity.findViewById(R.id.bottomNavView)

        return when (targetGraph) {
            null -> false
            navController.graph -> internalNavigate(deepLink)
            else -> {
                bottomNavigationView?.let {
                    if (shouldPopStackToEntry) {
                        popBackStackToEntry()
                    }
                    it.selectedItemId = targetGraph.id
                    it.post { internalNavigate(deepLink) }
                    true
                } ?: false
            }
        }
    }

    override fun canHandleDeepLink(deepLink: Uri): Boolean {
        val targetGraph = findGraphThatHasDeepLink(deepLink)
        return targetGraph.isNotNull()
    }

    fun Any?.isNotNull(): Boolean {
        return this != null
    }

    override fun handleDeepLink(intent: Intent, shouldPopStackToEntry: Boolean) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.let { deepLink ->
                navigateByDeepLink(deepLink, shouldPopStackToEntry)
            }
        }
    }

    private fun internalNavigate(deepLink: Uri): Boolean {
        val navController = findNavController()
        if (isMainMenuBottomNavigation(deepLink)) {
            popBackStackToEntry(true)
        }

        return try {
            if (navController.graph.hasDeepLink(deepLink)) {
                navigateToStartDestinationOfNestedGraph(deepLink)
                navController.navigate(deepLink)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun navigateToStartDestinationOfNestedGraph(deepLink: Uri) {
        val nestedStartDestinationDeeLink = when (findNestedGraphThatHasDeepLink(deepLink)) {
            NestedGraph.FEED_GRAPH -> Triple(
                R.id.feedFragment,
                localizedResources.getString(R.string.deep_link_path_prefix_feed),
                localizedResources.getString(R.string.nav_deep_link_feed)
            )
            else -> null
        }

        nestedStartDestinationDeeLink?.run {
            val (destinationId, destinationPath, destinationUrl) = this
            val navController = findNavController()
            if (navController.currentDestination?.id != destinationId
                && deepLink.toString().contains(destinationPath).not()
            ) {
                navController.navigate(destinationUrl.toUri())
            }
        }
    }

    private fun findNestedGraphThatHasDeepLink(deepLink: Uri): NestedGraph? {
        return NestedGraph.values().firstOrNull {
            val graph = findNavController().navInflater.inflate(it.id)
            graph.hasDeepLink(deepLink)
        }
    }

    private fun findGraphThatHasDeepLink(deepLink: Uri): NavGraph? {
        val navController = findNavController()
        if (navController.graph.hasDeepLink(deepLink)) {
            // Prioritize the current graph if it contains the deepLink
            return navController.graph
        }

        availableGraphs.forEach {
            val graph = navController.navInflater.inflate(it)
            if (graph.hasDeepLink(deepLink)) {
                return graph
            }
        }
        return null
    }

    private val availableGraphs = BottomNavigateStatic.bottomMenu.map {
        it.navGraph
    }

    private fun isMainMenuBottomNavigation(deepLink: Uri): Boolean {
        val inputs = arrayOf(
            localizedResources.getString(R.string.deep_link_path_https_feed),
            localizedResources.getString(R.string.deep_link_path_https_search)
        )

        return deepLink.toString().containsSomeOf(*inputs, ignoreCase = true)
    }

    override fun popBackStackToEntry(popInclusive: Boolean) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> navController.popBackStack(
                R.id.feedFragment, false
            )
            R.id.bloc_nav_graph -> navController.popBackStack()
            R.id.search_nav_graph -> navController.popBackStack(
                R.id.searchFragment,
                popInclusive
            )
        }
    }

    override fun navigateCreateBlogFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToCreateBlogFragment
                        )
                    }
                    R.id.searchFragment -> {

                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToResentVerifyEmail(email: String) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                if (navController.currentDestination?.id == R.id.settingFragment) {
                    navController.navigate(
                        R.id.actionSettingFragmentToResentVerifyFragment,
                        ResentVerifyEmailFragmentArgs(email).toBundle()
                    )
                } else {
                    unsupportedNavigation()
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToRecastDialogFragment(contentUiModel: ContentFeedUiModel) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToRecastDialogFragment,
                            RecastDialogFragmentArgs(contentUiModel).toBundle()
                        )
                    }
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToRecastDialogFragment,
                            RecastDialogFragmentArgs(contentUiModel).toBundle()
                        )
                    }
                    R.id.trendFragment -> {
                        navController.navigate(
                            R.id.actionTrendFragmentToRecastDialogFragment,
                            RecastDialogFragmentArgs(contentUiModel).toBundle()
                        )
                    }
                    R.id.feedDetailFragment -> {
                        navController.navigate(
                            R.id.actionFeedDetailFragmentToRecastDialogFragment,
                            RecastDialogFragmentArgs(contentUiModel).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToCreateQuoteFragment(
        contentUiModel: ContentFeedUiModel,
        profileEditBundle: ProfileBundle
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.dialogRecastFragment -> {
                        navController.navigate(
                            R.id.actionDialogRecastFragmentToCreateQuoteFragment,
                            CreateQuoteFragmentArgs(
                                contentUiModel,
                                profileEditBundle
                            ).toBundle()
                        )
                    }
                    R.id.trendFragment -> {
                        navController.navigate(
                            R.id.actionTrendFragmentToCreateQuoteFragment,
                            CreateQuoteFragmentArgs(
                                contentUiModel,
                                profileEditBundle
                            ).toBundle()
                        )
                    }
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToCreateQuoteFragment,
                            CreateQuoteFragmentArgs(
                                contentUiModel,
                                profileEditBundle
                            ).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToLanguageFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.settingFragment -> {
                        navController.navigate(
                            R.id.actionSettingFragmentToLanguageFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToAppLanguageFragment(isAppLanguage: Boolean) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.languageFragment -> {
                        navController.navigate(
                            R.id.actionLanguageFragmentToAppLanguageFragment,
                            AppLanguageFragmentArgs(isAppLanguage).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToSettingProfileFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.settingFragment -> {
                        navController.navigate(
                            R.id.actionSettingFragmentToSettingProfileFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToChangePasswordFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.settingProfileFragment -> {
                        navController.navigate(
                            R.id.actionSettingProfileFragmentToChangePasswordFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToCreatePasswordFragment(verificationUiModel: VerificationUiModel) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.changePasswordFragment -> {
                        navController.navigate(
                            R.id.actionChangePasswordFragmentToCreatePasswordFragment,
                            CreatePasswordFragmentArgs(verificationUiModel).toBundle()
                        )
                    }
                    R.id.verifyOtpFragment -> {
                        navController.navigate(
                            R.id.actionVerifyOtpFragmentToCreatePasswordFragment,
                            CreatePasswordFragmentArgs(verificationUiModel).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToCompleteFragment(
        onDeletePage: Boolean,
        onAccountPage: Boolean,
        onForGotPass: Boolean
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.createPasswordFragment -> {
                        navController.navigate(
                            R.id.actionCreatePasswordToCompleteFragment,
                            CompleteFragmentArgs(
                                onDeletePage = onAccountPage,
                                onDeleteAccount = onAccountPage,
                                onForgotPassword = onForGotPass
                            ).toBundle()
                        )
                    }
                    R.id.deletePageFragment -> {
                        navController.navigate(
                            R.id.actionDeletePageFragmentToCompleteFragment,
                            CompleteFragmentArgs(
                                onDeletePage = onDeletePage,
                                onDeleteAccount = onAccountPage
                            ).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToFeedDetailFragment(
        contentUiModel: ContentFeedUiModel,
        isContent: Boolean
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToFeedDetailFragment,
                            FeedDetailFragmentArgs(contentUiModel, isContent).toBundle()
                        )
                    }
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToFeedDetailFragment,
                            FeedDetailFragmentArgs(contentUiModel, isContent).toBundle()
                        )
                    }
                    R.id.trendFragment -> {
                        navController.navigate(
                            R.id.actionTrendFragmentToFeedDetailFragment,
                            FeedDetailFragmentArgs(contentUiModel, isContent).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToFeedDetailFragment,
                            FeedDetailFragmentArgs(contentUiModel, isContent).toBundle()
                        )
                    }
                    R.id.trendFragment -> {
                        navController.navigate(
                            R.id.actionTrendFragmentToFeedDetailFragment,
                            FeedDetailFragmentArgs(contentUiModel, isContent).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToSearchTrendFragmrnt() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.searchFragment -> {
                        navController.navigate(
                            R.id.actionSearchFragmentToSearchTrendFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToTrendFragment(trendSlug: String) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.searchFragment -> {
                        navController.navigate(
                            R.id.actionSearchFragmentToTrendFragment,
                            TrendFragmentArgs(trendSlug).toBundle()
                        )
                    }
                    R.id.searchTrendFragment -> {
                        navController.navigate(
                            R.id.actionSearchTrendFragmentToTrendFragment,
                            TrendFragmentArgs(trendSlug).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToProfileFragment(
        castcle: String,
        profileType: String,
        isMe: Boolean
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.searchTrendFragment -> {
                        navController.navigate(
                            R.id.actionSearchTrendFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType).toBundle()
                        )
                    }
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType).toBundle()
                        )
                    }
                    R.id.settingFragment -> {
                        navController.navigate(
                            R.id.actionSettingFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType, true).toBundle()
                        )
                    }
                    R.id.trendFragment -> {
                        navController.navigate(
                            R.id.actionTrendFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType, isMe).toBundle()
                        )
                    }
                    R.id.aboutYouFragment -> {
                        navController.navigate(
                            R.id.actionAboutYouFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType, true).toBundle()
                        )
                    }
                    R.id.reportFragment -> {
                        navController.navigate(
                            R.id.actionReportFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType, true).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType).toBundle()
                        )
                    }
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType).toBundle()
                        )
                    }
                    R.id.settingFragment -> {
                        navController.navigate(
                            R.id.actionSettingFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType, true).toBundle()
                        )
                    }
                    R.id.addLinksFragment -> {
                        navController.navigate(
                            R.id.actionAddLinksFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType).toBundle()
                        )
                    }
                    R.id.aboutYouFragment -> {
                        navController.navigate(
                            R.id.actionAboutYouFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType, true).toBundle()
                        )
                    }
                    R.id.reportFragment -> {
                        navController.navigate(
                            R.id.actionReportFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType, true).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            R.id.profile_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToProfileFragment,
                            ProfileFragmentArgs(castcle, profileType).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToGreetingPageFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.settingFragment -> {
                        navController.navigate(
                            R.id.actionSettingFragmentToGreetingPageFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToAddLinksFragment(linksRequest: LinksRequestUiModel) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.aboutYouFragment -> {
                        navController.navigate(
                            R.id.actionAboutYouFragmentToAddLinksFragment,
                            AddLinksFragmentArgs(linksRequest).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToDialogChooseFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToDialogChooseFragment
                        )
                    }
                    R.id.createAccountFragment -> {
                        navController.navigate(
                            R.id.actionCreateAccountToChooseDialogFragment,
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToCropAvatarImage(mediaFile: MediaFile) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToCropAvatarImageFragment,
                            CropAvatarImageFragmentArgs(mediaFile).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToProfileChooseDialogFragment(isAccount: Boolean) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToProfileChooseDialogFragment,
                            ProfileChooseDialogFragmentArgs(isAccount).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }


    override fun navigateToUserChooseDialogFragment(userDisplay: String) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.userChooseDialogFragment,
                            UserChooseDialogFragmentArgs(userDisplay).toBundle()
                        )
                    }
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.userChooseDialogFragment,
                            UserChooseDialogFragmentArgs(userDisplay).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToProfileDeletePageFragment(profileEditBundle: ProfileBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToDeletePageFragment,
                            DeletePageFragmentArgs(profileEditBundle).toBundle()
                        )
                    }
                    R.id.settingProfileFragment -> {
                        navController.navigate(
                            R.id.actionSettingProfileFragmentToDeletePageFragment,
                            DeletePageFragmentArgs(profileEditBundle).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToReportFragment(
        castcle: String,
        profileType: String,
        displayName: String,
        goToProfileFragment: Boolean
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToReportFragment,
                            ReportFragmentArgs(
                                castcle,
                                profileType,
                                displayName,
                                goToProfileFragment
                            ).toBundle()
                        )
                    }
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToReportFragment,
                            ReportFragmentArgs(
                                castcle,
                                profileType,
                                displayName,
                                goToProfileFragment
                            ).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToNotificationFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.settingFragment -> {
                        navController.navigate(
                            R.id.actionSettingFragmentToNotificationFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToForgotPassword() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.loginFragment -> {
                        navController.navigate(
                            R.id.actionLoginFragmentToSearchAccountFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToVerifyOtpFragment(otpRequest: ProfileBundle.ProfileOtp) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.searchAccountFragment -> {
                        navController.navigate(
                            R.id.actionSearchAccountFragmentToVerifyFragment,
                            VerifyEmailFragmentArgs(otpRequest).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToTwitterLoginFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.dialogLoginFragment -> {
                        navController.navigate(
                            R.id.actionDialogLoginFragmentToTwitterLoginFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToCreateAccountFragment(registerBundle: RegisterBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph, R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.dialogLoginFragment -> {
                        navController.navigate(
                            R.id.actionDialogLoginFragmentToCreateAccountFragment,
                            CreateAccountFragmentArgs(registerBundle).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToCreatePostFragment(
        createPostBundle: CreatePostBundle,
        isFromProfile: Boolean
    ) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToCreatePostFragment,
                            CreatePostFragmentArgs(createPostBundle, isFromProfile).toBundle()
                        )
                    }
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToCreatePostFragment,
                            CreatePostFragmentArgs(createPostBundle, isFromProfile).toBundle()
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    override fun navigateToEditContentDialogFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph,
            R.id.search_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.profileFragment -> {
                        navController.navigate(
                            R.id.actionProfileFragmentToEditContentDialogFragment
                        )
                    }
                    R.id.trendFragment -> {
                        navController.navigate(
                            R.id.actionTrendFragmentToEditContentDialogFragment
                        )
                    }
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToEditContentDialogFragment
                        )
                    }
                    else -> {
                        unsupportedNavigation()
                    }
                }
            }
            else -> {
                unsupportedNavigation()
            }
        }
    }

    @Keep
    enum class NestedGraph(@IdRes val id: Int) {
        FEED_GRAPH(R.navigation.onboard_nav_graph);
    }
}
