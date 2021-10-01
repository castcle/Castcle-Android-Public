package com.castcle.usecase

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.core.os.ConfigurationCompat
import com.castcle.data.storage.AppPreferences
import com.castcle.localization.LANGUAGE_CODE_EN
import java.util.*
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
//  Created by sklim on 29/9/2021 AD at 11:01.

interface OverrideLocaleApp {

    fun execute(activityContext: Context)

    fun updateResource(
        configuration: Configuration,
        activityContext: Context
    )
}

class OverrideLocaleAppImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : OverrideLocaleApp {
    override fun execute(activityContext: Context) {
        val localeCode = appPreferences.language ?: LANGUAGE_CODE_EN
        val localeToUpdate = getSystemLocaleCode(activityContext, localeCode)
        val activityResources = activityContext.resources
        val configuration = Configuration(activityResources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(localeToUpdate)
            activityContext.createConfigurationContext(configuration)
        } else {
            configuration.locale = localeToUpdate
        }

        updateResource(configuration, activityContext)
    }

    override fun updateResource(
        configuration: Configuration,
        activityContext: Context
    ) {
        val activityResources = activityContext.resources
        activityResources.updateConfiguration(configuration, activityResources.displayMetrics)
    }

    private fun getSystemLocaleCode(activityContext: Context, appLocale: String?): Locale {
        return if (appLocale == null) {
            ConfigurationCompat.getLocales(
                activityContext.resources.configuration
            ).get(0)
        } else {
            Locale(appLocale)
        }
    }

}