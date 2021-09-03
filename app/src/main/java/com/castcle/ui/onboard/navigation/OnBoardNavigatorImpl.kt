package com.castcle.ui.onboard.navigation

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.castcle.android.R
import com.castcle.common_model.model.login.AuthBundle
import com.castcle.ui.base.BaseNavigatorImpl
import com.castcle.ui.signin.password.PasswordFragmentArgs
import com.castcle.ui.webview.WebViewFragmentArgs
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

    override fun navigetToDisplayNameFragment(authBundle: AuthBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.passwordFragment) {
                    navController.navigate(
                        R.id.actionPasswordFragmentToCreateDisplayNameFragment,
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

    override fun naivgetToProfileChooseImageFragment(authBundle: AuthBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.createDisplayProfileFragment) {
                    navController.navigate(
                        R.id.actionCreateDisplayNameFragmentToProfileChooseFragment,
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

    override fun naivgetToProfileVerifyEmailFragment(authBundle: AuthBundle) {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.profileChooseFragment) {
                    navController.navigate(
                        R.id.actionProfileChooseFragmentToVerifyEmailFragment,
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

    override fun navigateToAboutYouFragment() {
        val navController = findNavController()
        when (navController.graph.id) {
            R.id.onboard_nav_graph -> {
                if (navController.currentDestination?.id == R.id.profileChooseFragment) {
                    navController.navigate(
                        R.id.actionProfileChooseFragmentToVerifyEmailFragment
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
}
