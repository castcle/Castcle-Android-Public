package com.castcle.components_android.ui.custom.timeago

import android.view.View
import android.widget.TextView

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
//  Created by sklim on 5/1/2022 AD at 18:21.

internal class BlitzAttachCusListener(
    private val target: TextView
) : View.OnAttachStateChangeListener {

    internal var time: Long = 0
        set(value) {
            if (field == value) return
            field = value
            updateRunnableSubscription()
        }

    internal var showSeconds: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            updateRunnableSubscription()
        }

    private val runnable = object : Runnable {
        override fun run() {
            val diff = System.currentTimeMillis() - time
            val blitzTime = getBlitzTimeCus(diff)

            target.diffedValue = blitzTime.getText(target.resources, diff, showSeconds, time)
            target.postDelayed(this, blitzTime.updateRateMs)
        }
    }

    override fun onViewDetachedFromWindow(v: View?) {
        target.removeCallbacks(runnable)
    }

    override fun onViewAttachedToWindow(v: View?) {
        updateRunnableSubscription()
    }

    fun dropCounter() {
        target.removeCallbacks(runnable)
    }

    private fun updateRunnableSubscription() {
        target.removeCallbacks(runnable)
        target.post(runnable)
    }

}

internal var TextView.diffedValue: String
    get() = text.toString()
    set(value) {
        if (text.toString() == value) return
        text = value
    }

