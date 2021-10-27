package com.castcle.ui.setting.changepassword

import androidx.lifecycle.MutableLiveData
import com.castcle.common_model.model.setting.VerificationUiModel
import com.castcle.ui.base.BaseViewModel
import com.castcle.ui.util.SingleLiveEvent
import com.castcle.usecase.auth.GetVerificationPassSingleUseCase
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
//  Created by sklim on 1/9/2021 AD at 18:14.

abstract class ChangePasswordViewModel : BaseViewModel() {

    abstract val onVerificationResponse: SingleLiveEvent<VerificationUiModel>

    abstract val onError: Observable<Throwable>

    abstract val showLoading: Observable<Boolean>

    abstract val input: Input

    interface Input {
        fun verificationPassword(pass: String)
    }
}

class ChangePasswordViewModelImpl @Inject constructor(
    private val getVerificationPassSingleUseCase: GetVerificationPassSingleUseCase
) : ChangePasswordViewModel(),
    ChangePasswordViewModel.Input {

    override val input: Input
        get() = this

    private val _onVerificationResponse = SingleLiveEvent<VerificationUiModel>()
    override val onVerificationResponse: SingleLiveEvent<VerificationUiModel>
        get() = _onVerificationResponse

    private val _onError = BehaviorSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _onError

    private val _showLoading = BehaviorSubject.create<Boolean>()
    override val showLoading: Observable<Boolean>
        get() = _showLoading

    override fun verificationPassword(pass: String) {
        postVerificationPassword(pass)
    }

    private fun postVerificationPassword(pass: String) {
        getVerificationPassSingleUseCase.execute(pass)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doFinally { _showLoading.onNext(false) }
            .subscribeBy(
                onSuccess = {
                    _onVerificationResponse.postValue(it)
                },
                onError = {
                    _showLoading.onNext(false)
                    _onError.onNext(it)
                }).addToDisposables()
    }
}
