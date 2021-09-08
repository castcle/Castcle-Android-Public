package com.castcle.ui.onboard

import com.castcle.common_model.model.userprofile.User
import com.castcle.usecase.userprofile.GetCastcleIdSingleUseCase
import com.castcle.usecase.userprofile.GetUserProfileSingleUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
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
//  Created by sklim on 19/8/2021 AD at 11:02.

class OnBoardViewModelImpl @Inject constructor(
    private val userProfileSingleUseCase: GetUserProfileSingleUseCase,
    private val getCastcleIdSingleUseCase: GetCastcleIdSingleUseCase
) : OnBoardViewModel() {

    private val _userProfile = BehaviorSubject.create<User>()

    override val user: Observable<User>
        get() = _userProfile

    override val isGuestMode: Boolean
        get() = getCastcleIdSingleUseCase.execute(Unit).blockingGet()

    override fun onRefreshProfile() {
        userProfileSingleUseCase.execute(Unit)
            .subscribeBy(
                onNext = _userProfile::onNext,
                onError = {}
            ).addToDisposables()
    }
}
