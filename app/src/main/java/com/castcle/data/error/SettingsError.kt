package com.castcle.data.error

import androidx.annotation.StringRes
import com.castcle.android.R

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
//  Created by sklim on 28/9/2021 AD at 20:08.

sealed class SettingsError(
    cause: Throwable?,
    @StringRes readableMessageRes: Int? = null
) : AppError(cause, null, readableMessageRes) {

    class GetLanguageError(cause: Throwable?) : SettingsError(
        cause, R.string.error_get_language
    )

    class GetCurrentLocaleError(cause: Throwable?) : SettingsError(
        cause, R.string.error_get_language
    )

    class SetLanguageError(cause: Throwable?) : SettingsError(
        cause, R.string.error_set_language
    )

    class LogoutError(cause: Throwable?) : SettingsError(
        cause, R.string.error_logout
    )
}
