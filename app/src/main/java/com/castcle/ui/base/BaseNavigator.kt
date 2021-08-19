package com.castcle.ui.base

import android.app.Activity
import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.castcle.data.error.NavigationError
import com.castcle.extensions.getResourceName
import timber.log.Timber

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
//  Created by sklim on 18/8/2021 AD at 16:22.

interface BaseNavigator {

    fun findNavController(): NavController

    fun requestNavController(): NavController?

    fun navigateBack()
}

abstract class BaseNavigatorImpl(private val activity: Activity) : BaseNavigator {

    override fun requestNavController(): NavController? =
        try {
            findNavController()
        } catch (e: IllegalStateException) {
            // Log Crashlytics as non-fatal for monitoring
            Timber.e(e)
            null
        }

    override fun navigateBack() {
        findNavController().navigateUp()
    }

    protected fun unsupportedNavigation() {
        val navController = findNavController()
        val currentGraph = activity.getResourceName(navController.graph.id)
        val currentDestination = activity.getResourceName(navController.currentDestination?.id)
        handleError(NavigationError.UnsupportedNavigationError(currentGraph, currentDestination))
    }

    private fun handleError(error: Throwable) {
        if (activity is BaseActivity<*>) {
            Timber.e(error)
            activity.displayError(error)
        } else {
            throw error
        }
    }

    protected fun popBackTo(@IdRes destinationId: Int, inclusive: Boolean = false) {
        findNavController().popBackStack(destinationId, inclusive)
    }
}
