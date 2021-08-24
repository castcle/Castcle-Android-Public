package com.castcle.ui.onboard.navigation

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.castcle.android.R
import com.castcle.ui.base.BaseNavigatorImpl
import com.castcle.ui.feed.FeedFragmentDirections.Companion.actionFeedFragmentToNotiflyLoginDialoginDialogFragment
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
                        actionFeedFragmentToNotiflyLoginDialoginDialogFragment()
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
