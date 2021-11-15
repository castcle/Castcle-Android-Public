package com.castcle.extensions

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.castcle.data.error.userReadableMessage
import com.castcle.ui.onboard.navigation.OnBoardNavigator

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
//  Created by sklim on 8/9/2021 AD at 17:42.

fun Fragment.addOnBackPressedCallback(block: () -> Unit) {
    activity?.onBackPressedDispatcher?.addCallback(
        this,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                block()
            }
        }
    )
}

fun Fragment.displayError(error: Throwable) {
    val message = if (error.cause?.message?.isBlank() == false) {
        error.cause?.message
    } else {
        error.userReadableMessage(this.requireContext())
    }
    Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).also { it.show() }
}

fun Fragment.displayErrorMessage(error: String) {
    Toast.makeText(this.requireContext(), error, Toast.LENGTH_SHORT).also { it.show() }
}

fun <T> Fragment.setNavigationResult(
    onBoardNavigator: OnBoardNavigator,
    key: String, value: T
) {
    onBoardNavigator.findNavController().previousBackStackEntry?.savedStateHandle?.set(
        key,
        value
    )
}

fun <T> Fragment.getNavigationResult(
    onBoardNavigator: OnBoardNavigator,
    @IdRes idOwner: Int,
    key: String,
    onResult: (result: T) -> Unit
) {
    val navBackStackEntry = onBoardNavigator.findNavController().getBackStackEntry(idOwner)

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME
            && navBackStackEntry.savedStateHandle.contains(key)
        ) {
            val result = navBackStackEntry.savedStateHandle.get<T>(key)
            result?.let(onResult)
            navBackStackEntry.savedStateHandle.remove<T>(key)
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)

    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
}