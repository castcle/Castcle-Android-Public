package com.castcle.components_android.ui.custom.timeago

import android.content.Context
import android.content.res.Resources
import android.widget.TextView
import com.castcle.android.components_android.R
import com.castcle.extensions.convertLongToTime
import com.perfomer.blitz.getTimeAgo
import java.util.*

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
//  Created by sklim on 5/1/2022 AD at 17:42.

fun TextView.setTimeAgoCus(
    date: Date,
    showSeconds: Boolean = false,
    autoUpdate: Boolean = true
) {
    setTimeAgoCus(date.time, showSeconds, autoUpdate)
}

fun TextView.setTimeAgoCus(
    time: Long,
    showSeconds: Boolean = false,
    autoUpdate: Boolean = true
) {
    if (!autoUpdate) {
        this.diffedValue = context.getTimeAgo(time, showSeconds)
    }

    val tag = getTag(R.id.blitz)
    val stateChangeListener: BlitzAttachCusListener

    if (tag is BlitzAttachCusListener) {
        stateChangeListener = tag
    } else {
        stateChangeListener = BlitzAttachCusListener(this)
        addOnAttachStateChangeListener(stateChangeListener)
        setTag(R.id.blitz, stateChangeListener)
    }

    stateChangeListener.showSeconds = showSeconds
    stateChangeListener.time = time
}

fun Context.getTimeAgoCus(time: Long, showSeconds: Boolean = false): String {
    return resources.getTimeAgoCus(time, showSeconds)
}

fun Resources.getTimeAgoCus(time: Long, showSeconds: Boolean = false): String {
    val diff = System.currentTimeMillis() - time
    return getBlitzTimeCus(diff).getText(this, diff, showSeconds, time)
}

internal fun getBlitzTimeCus(diff: Long): BlitzTimeCustom {
    val timeUnits = BlitzTimeCustom.values()
    return timeUnits.find { diff < it.differenceMs } ?: timeUnits.last()
}

internal fun BlitzTimeCustom.getText(
    resources: Resources,
    diff: Long,
    showSeconds: Boolean,
    time: Long
): String {
    return when {
        dividerMs == null -> {
            resources.getString(singleStringResource!!)
        }
        fullDay -> {
            convertLongToTime(time)
        }
        else -> {
            if (fullDay) {
                convertLongToTime(time)
            } else if (this == BlitzTimeCustom.SECONDS && !showSeconds) {
                resources.getString(R.string.blitz_now)
            } else {
                val quantity = (diff / dividerMs).toInt()
                resources.getQuantityString(pluralResource!!, quantity, quantity)
            }
        }
    }
}