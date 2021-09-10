package com.castcle.ui.signin.profilechooseimage

import com.castcle.common_model.model.userprofile.UserUpdateRequest
import com.castcle.ui.base.BaseViewModel
import com.castcle.usecase.userprofile.UpdateUserProfileCompletableUseCase
import io.reactivex.Completable
import io.reactivex.Observable
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
//  Created by sklim on 1/9/2021 AD at 18:14.

abstract class ProfileChooseFragmentViewModel : BaseViewModel() {

    abstract val showLoading: Observable<Boolean>

    abstract fun requestUpdateProfile(userRequest: UserUpdateRequest): Completable
}

class ProfileChooseFragmentViewModelImpl @Inject constructor(
    private val updateUserProfileCompletableUseCase: UpdateUserProfileCompletableUseCase
) : ProfileChooseFragmentViewModel() {

    override val showLoading: Observable<Boolean>
        get() = _showLoading
    private val _showLoading = BehaviorSubject.create<Boolean>()

    override fun requestUpdateProfile(userRequest: UserUpdateRequest): Completable {
        return updateUserProfileCompletableUseCase
            .execute(userRequest)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _showLoading.onNext(false) }
    }

}
