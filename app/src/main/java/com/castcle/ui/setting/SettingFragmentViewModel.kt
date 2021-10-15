package com.castcle.ui.setting

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.userprofile.User
import com.castcle.ui.base.BaseViewModel
import com.castcle.usecase.setting.LogoutCompletableUseCase
import com.castcle.usecase.userprofile.GetCachedUserProfileSingleUseCase
import io.reactivex.Completable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
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

abstract class SettingFragmentViewModel : BaseViewModel() {

    abstract val userProfile: LiveData<User>

    abstract fun onLogOut(activity: Activity): Completable

    abstract fun fetchCachedUserProfile(): Completable
}

class SettingFragmentViewModelImpl @Inject constructor(
    private val logoutCompletableUseCase: LogoutCompletableUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase
) : SettingFragmentViewModel() {

    private val _showLoading = BehaviorSubject.create<Boolean>()

    private val _error = PublishSubject.create<Throwable>()

    private val _userProfile = MutableLiveData<User>()
    override val userProfile: LiveData<User>
        get() = _userProfile

    override fun onLogOut(activity: Activity): Completable {
        return logoutCompletableUseCase.execute(activity)
    }

    override fun fetchCachedUserProfile(): Completable =
        cachedUserProfileSingleUseCase
            .execute(Unit)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doFinally { _showLoading.onNext(false) }
            .doOnNext(::setUserProfileData)
            .doOnError(_error::onNext).firstOrError()
            .ignoreElement()

    private fun setUserProfileData(user: Optional<User>) {
        if (user.isPresent) {
            _userProfile.value = user.get()
        }
    }
}
