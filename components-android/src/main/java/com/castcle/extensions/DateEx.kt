package com.castcle.extensions

import android.annotation.SuppressLint
import android.os.Build
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.chrono.ChronoLocalDateTime
import org.threeten.bp.chrono.ThaiBuddhistDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.*
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
//  Created by sklim on 26/8/2021 AD at 11:36.

fun String.toTime(): Long {
    val format = SimpleDateFormat(COMMON_DATE_FORMAT, Locale.getDefault())
    return try {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date = format.parse(this)
            date?.toInstant()?.toEpochMilli() ?: 0L
        } else {
            val date = LocalDateTime.parse(
                this,
                DateTimeFormatter.ofPattern(COMMON_DATE_FORMAT, Locale.getDefault())
            )
            date.atOffset(org.threeten.bp.ZoneOffset.UTC).toInstant().toEpochMilli()
        }
    } catch (e: ParseException) {
        0L
    }
}

fun String.toFormatDate(language: String = LANGUAGE_CODE_EN): String {
    return if (isNotBlank()) {
        SimpleDateFormat(COMMON_DATE_FORMAT, Locale.getDefault())
            .parse(this).toFormatString(SOURCE_DATE_FORMAT, language)
    } else {
        ""
    }
}

fun Date?.toFormatString(pattern: String, language: String, isHourOfDay: Boolean = false): String {
    if (this == null) return ""
    val dateTime = if (language == LANGUAGE_CODE_TH) {
        toBuddhistDateTime(isHourOfDay)
    } else {
        toLocalDateTime(isHourOfDay)
    }
    return dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale(language)))
}

fun Date.toBuddhistDateTime(isHourOfDay: Boolean): ChronoLocalDateTime<*> {
    with(Calendar.getInstance()) {
        time = this@toBuddhistDateTime
        val buddhistDate = ThaiBuddhistDate.of(
            get(Calendar.YEAR) + ADDITIONAL_BUDDHIST_YEARS,
            get(Calendar.MONTH) + 1,
            get(Calendar.DAY_OF_MONTH)
        )
        val localTime = LocalTime.of(
            if (isHourOfDay) get(Calendar.HOUR_OF_DAY) else get(Calendar.HOUR),
            get(Calendar.MINUTE),
            get(Calendar.SECOND)
        )
        return buddhistDate.atTime(localTime)
    }
}

fun Date.toLocalDateTime(isHourOfDay: Boolean): ChronoLocalDateTime<*> {
    with(Calendar.getInstance()) {
        time = this@toLocalDateTime
        return LocalDateTime.of(
            get(Calendar.YEAR),
            get(Calendar.MONTH) + 1,
            get(Calendar.DAY_OF_MONTH),
            if (isHourOfDay) get(Calendar.HOUR_OF_DAY) else get(Calendar.HOUR),
            get(Calendar.MINUTE),
            get(Calendar.SECOND)
        )
    }
}

fun Int.getMonthName(language: String = LANGUAGE_CODE_EN): String {
    var month = ""
    val dfs = DateFormatSymbols()
    val months = dfs.months
    if (this in 0..11) {
        month = months[this]
    }
    return month
}

@SuppressLint("SimpleDateFormat")
fun getDateTimeStamp(): Long = Date().time

const val COMMON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val SOURCE_DATE_FORMAT = "MMM dd,yyyy"
const val LANGUAGE_CODE_TH = "th"
const val LANGUAGE_CODE_EN = "en"
const val ADDITIONAL_BUDDHIST_YEARS = 543
