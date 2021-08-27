package com.castcle.components_android.ui.custom.timeago

import kotlin.math.roundToLong

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
//  Created by sklim on 27/8/2021 AD at 14:38.

enum class Periods(
    val propertyKey: String,
    private val predicate: DistancePredicate
) {
    NOW("timeago.now", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance in 0L..(0.99).toLong()
        }
    }),
    ONE_MINUTE_PAST("timeago.oneminute.past", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance == 1L
        }
    }),
    X_MINUTES_PAST("timeago.xminutes.past", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance in 2..44
        }
    }),
    ABOUT_AN_HOUR_PAST("timeago.aboutanhour.past", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance in 45..89
        }
    }),
    X_HOURS_PAST("timeago.xhours.past", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance in 90..1439
        }
    }),
    ONE_DAY_PAST("timeago.oneday.past", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance in 1440..2519
        }
    }),
    X_DAYS_PAST("timeago.xdays.past", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance in 2520..10079
        }
    }),
    ONE_MINUTE_FUTURE("timeago.oneminute.future", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance.toInt() == -1
        }
    }),
    X_MINUTES_FUTURE("timeago.xminutes.future", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance <= -2 && distance >= -44
        }
    }),
    ABOUT_AN_HOUR_FUTURE("timeago.aboutanhour.future", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance <= -45 && distance >= -89
        }
    }),
    X_HOURS_FUTURE("timeago.xhours.future", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance <= -90 && distance >= -1439
        }
    }),
    ONE_DAY_FUTURE("timeago.oneday.future", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance <= -1440 && distance >= -2519
        }
    }),
    X_DAYS_FUTURE("timeago.xdays.future", object : DistancePredicate {
        override fun validateDistanceMinutes(distance: Long): Boolean {
            return distance <= -2520 && distance >= -10079
        }
    });

    companion object {

        fun findByDistanceMinutes(distanceMinutes: Long): Periods? {
            val values = values()
            for (item in values) {
                val successful = item.predicate
                    .validateDistanceMinutes(distanceMinutes)
                if (successful) {
                    return item
                }
            }
            return null
        }
    }
}

private interface DistancePredicate {
    fun validateDistanceMinutes(distance: Long): Boolean
}