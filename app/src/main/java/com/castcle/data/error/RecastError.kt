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

class RecastError(
    cause: Throwable?,
) : AppError(
    cause = cause,
    readableMessage = getErrorMessage(cause)
) {
    fun reCastNotFound() = CODE_CANNOT_CAST == code && statusCode == STATUS_CODE_NOT_FOUND

    fun postCannotAccess() = CODE_CANNOT_ACCESS == code

    fun postNotFount() = CODE_POST_NOT_FOUND == code
}

const val CODE_CANNOT_CAST = "404"
const val CODE_CANNOT_ACCESS = "403"
const val CODE_POST_NOT_FOUND = "400"
const val STATUS_CODE_NOT_FOUND = 404