package com.castcle.data.error

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
//  Created by sklim on 31/8/2021 AD at 15:17.

class RegisterErrorError(
    cause: Throwable?,
) : AppError(
    cause = cause,
    readableMessage = getErrorMessage(cause)
) {

    fun hasAuthenticationEmailNotFound() = STATUC_CODE_NOT_FOUND == statusCode

    fun hasAuthenticationEmailInSystem() = CODE_INVALID_EMAIL_IN_SYSTEM == code

    fun hasAuthenticationRoleInvalid() = CODE_INVALID_ROLE == code

    fun hasAuthenticationChannelInvalid() = CODE_INVALID_CHANNEL == code

    fun hasAuthenticationUserInSystem() = CODE_INVALID_USER == code

    fun hasAuthenticationCastcleIdInSystem() = CODE_INVALID_CASTCLE_ID == code

    fun hasAuthenticationTokenExprierd() = CODE_TOKEN_EXPLIERD == code
}

const val CODE_INVALID_EMAIL = "3003"
const val CODE_INVALID_CHANNEL = "3005"
const val CODE_INVALID_ROLE = "30013"
const val CODE_INVALID_EMAIL_IN_SYSTEM = "3014"
const val CODE_INVALID_CASTCLE_ID = "30017"
const val CODE_INVALID_USER = "30016"
const val CODE_TOKEN_EXPLIERD = "1003"