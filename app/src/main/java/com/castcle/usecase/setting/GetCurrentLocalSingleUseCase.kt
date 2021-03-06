package com.castcle.usecase.setting

import android.content.Context
import android.os.Build
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.data.error.SettingsError.GetCurrentLocaleError
import com.castcle.usecase.base.SingleUseCase
import io.reactivex.Single
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
//  Created by sklim on 28/9/2021 AD at 17:20.

class GetCurrentLocalSingleUseCase @Inject constructor(
    rxSchedulerProvider: RxSchedulerProvider,
    private val appContext: Context,
    private val getAppLanguageSingleUseCase: GetAppLanguageSingleUseCase
) : SingleUseCase<Unit, String>(
    rxSchedulerProvider.io(),
    rxSchedulerProvider.main(),
    ::GetCurrentLocaleError
) {

    override fun create(input: Unit): Single<String> {
        return getAppLanguageSingleUseCase.execute(input)
            .filter { it.isNotEmpty() }
            .toSingle()
            .onErrorReturnItem(deviceLocale.language)
    }

    @Suppress("DEPRECATION")
    private val deviceLocale: Locale
        get() {
            val config = appContext.resources.configuration
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.locales[0]
            } else {
                config.locale
            }
        }
}
