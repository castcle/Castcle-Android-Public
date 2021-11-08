package com.castcle.usecase.userprofile

import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.userprofile.User
import com.castcle.data.error.Ignored
import com.castcle.data.model.dao.user.UserDao
import com.castcle.data.repository.UserProfileRepository
import com.castcle.networking.api.auth.AuthenticationsRepository
import com.castcle.usecase.base.CompletableUseCase
import io.reactivex.Completable
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
//  Created by sklim on 2/11/2021 AD at 15:27.

class UpdateProfileDataCompletableUseCase @Inject constructor(
    rxSchedulerProvider: RxSchedulerProvider,
    private val userRepository: UserProfileRepository
) : CompletableUseCase<User>(
    rxSchedulerProvider.io(),
    rxSchedulerProvider.main(),
    ::Ignored
) {
    override fun create(input: User): Completable {
        return userRepository.insertUserProfile(input)
    }
}
