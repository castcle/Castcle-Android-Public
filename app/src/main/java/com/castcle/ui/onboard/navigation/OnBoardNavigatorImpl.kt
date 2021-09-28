package com.castcle.ui.onboard.navigation

import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import androidx.annotation.Keep
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.*
import com.castcle.android.R
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.login.*
import com.castcle.data.staticmodel.BottomNavigateStatic
import com.castcle.extensions.containsSomeOf
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.BaseNavigatorImpl
import com.castcle.ui.common.dialog.recast.RecastDialogFragment
import com.castcle.ui.common.dialog.recast.RecastDialogFragmentArgs
import com.castcle.ui.createbloc.CreateQuoteFragmentArgs
import com.castcle.ui.signin.createdisplayname.CreateDisplayNameFragmentArgs
import com.castcle.ui.signin.password.PasswordFragmentArgs
import com.castcle.ui.signin.profilechooseimage.ProfileChooseFragmentArgs
import com.castcle.ui.signin.verifyemail.ResentVerifyEmailFragmentArgs
import com.castcle.ui.signin.verifyemail.VerifyEmailFragmentArgs
import com.castcle.ui.webview.WebViewFragmentArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
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
                if (navController.currentDestination?.id == R.id.feedFragment) {
                    navController.navigate(
                        R.id.actionFeedFragmentToNotiflyLoginDialoginDialogFragment
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

    override fun navigateToWebView(url: String) {
        val navController = findNavController()
        navController.navigate(
            R.id.webviewFragment,
            WebViewFragmentArgs(url).toBundle()
        )
    }

    override fun navigateToLoginFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.dialogLoginFragment) {
                    navController.navigate(
                        R.id.actionDialogLoginFragmentToLoginFragment
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

    override fun nvaigateToFeedFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
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
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.loginFragment) {
                    navController.navigate(
                        R.id.actionLoginFragmentToGreetingFragment
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

    override fun navigateToEmailFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
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
            R.id.onboard_nav_graph -> {
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

    override fun navigetToDisplayNameFragment(registerBundle: RegisterBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.passwordFragment) {
                    navController.navigate(
                        R.id.actionPasswordFragmentToCreateDisplayNameFragment,
                        CreateDisplayNameFragmentArgs(registerBundle).toBundle()
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

    override fun naivgetToProfileChooseImageFragment(profileBundle: ProfileBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.createDisplayProfileFragment) {
                    navController.navigate(
                        R.id.actionCreateDisplayNameFragmentToProfileChooseFragment,
                        ProfileChooseFragmentArgs(profileBundle).toBundle()
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
            R.id.onboard_nav_graph -> {
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

    override fun navigateToAboutYouFragment(profileBundle: ProfileBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.verifyEmailFragment) {
                    navController.navigate(
                        R.id.verifyEmailFragmentToAboutYouFragment,
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

    override fun navigateToSettingFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.feedFragment) {
                    navController.navigate(
                        R.id.actionFeedFragmentToSettingFragment
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
                R.id.feedFragment,
                popInclusive
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
                if (navController.currentDestination?.id == R.id.feedFragment) {
                    navController.navigate(
                        R.id.actionFeedFragmentToCreateBlogFragment
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

    override fun navigateToResentVerifyEmail(email: String) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
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

    override fun navigateToRecastDialogFragment(contentUiModel: ContentUiModel) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        navController.navigate(
                            R.id.actionFeedFragmentToRecastDialogFragment,
                            RecastDialogFragmentArgs(contentUiModel).toBundle()
                        )
                    }
                    R.id.feedMockFragment -> {
                        navController.navigate(
                            R.id.actionFeedMockFragmentToRecastDialogFragment,
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

    override fun navigateToCreateQuoteFragment(contentUiModel: ContentUiModel) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                when (navController.currentDestination?.id) {
                    R.id.dialogRecastFragment -> {
                        navController.navigate(
                            R.id.actionDialogRecastFragmentToCreateQuoteFragment,
                            CreateQuoteFragmentArgs(contentUiModel).toBundle()
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
