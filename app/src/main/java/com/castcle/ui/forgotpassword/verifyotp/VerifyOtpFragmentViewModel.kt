package com.castcle.ui.forgotpassword.verifyotp

import com.castcle.common_model.model.CountDownTimerLiveData
import com.castcle.common_model.model.engagement.domain.*
import com.castcle.common_model.model.setting.VerificationUiModel
import com.castcle.ui.base.BaseViewModel
import com.castcle.usecase.auth.RequestOtpSingleUseCase
import com.castcle.usecase.auth.RequestVerifyOtpSingleUseCase
import io.reactivex.*
import io.reactivex.rxkotlin.subscribeBy
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

abstract class VerifyOtpFragmentViewModel : BaseViewModel() {

    abstract fun verifyOtpRequest(verifyOtpRequest: VerifyOtpRequest): Single<VerificationUiModel>

    abstract val countDownTimerLiveData: CountDownTimerLiveData

    abstract val showLoading: Observable<Boolean>

    abstract val error: Observable<Throwable>

    abstract val onVerifyOtpResponse: Observable<VerificationUiModel>

    abstract val input: Input

    interface Input {
        fun requestOtpWithEmail(email: String)
    }
}

class VerifyOtpFragmentViewModelImpl @Inject constructor(
    private val requestVerifyOtpSingleUseCase: RequestVerifyOtpSingleUseCase,
    private val requestOtpSingleUseCase: RequestOtpSingleUseCase,
) : VerifyOtpFragmentViewModel(), VerifyOtpFragmentViewModel.Input {

    override val showLoading: Observable<Boolean>
        get() = _showLoading
    private val _showLoading = BehaviorSubject.create<Boolean>()

    override val error: Observable<Throwable>
        get() = _error
    private val _error = PublishSubject.create<Throwable>()

    private var _countDownTimerLiveData =
        CountDownTimerLiveData(DEFAULT_TIMER, DEFAULT_TIMER_INTERVAL)

    override val countDownTimerLiveData: CountDownTimerLiveData
        get() = _countDownTimerLiveData

    override val input: Input
        get() = this

    private var _onVerifyOtpResponse = BehaviorSubject.create<VerificationUiModel>()
    override val onVerifyOtpResponse: Observable<VerificationUiModel>
        get() = _onVerifyOtpResponse

    override fun verifyOtpRequest(verifyOtpRequest: VerifyOtpRequest): Single<VerificationUiModel> {
        return requestVerifyOtpSingleUseCase.execute(verifyOtpRequest)
    }

    override fun requestOtpWithEmail(email: String) {
        requestOtpSingleUseCase.execute(
            OtpRequest(
                channel = CHANNEL_EMAIL,
                payload = OtpPayloadRequest(
                    email = email
                )
            )
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.doFinally {
            _showLoading.onNext(false)
        }.subscribeBy(
            onSuccess = {
                _onVerifyOtpResponse.onNext(it)
            }, onError = {
                _error.onNext(it)
            }
        ).addToDisposables()
    }
}

private const val DEFAULT_TIMER = 60_000L
private const val DEFAULT_TIMER_INTERVAL = 100L
