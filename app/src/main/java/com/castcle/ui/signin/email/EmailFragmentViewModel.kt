package com.castcle.ui.signin.email

import com.castcle.common.lib.extension.doIfTakeLongerThan
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel.EmailVerifyUiModel
import com.castcle.common_model.model.signin.reuquest.EmailRequest
import com.castcle.networking.service.common.TIMEOUT_SEARCH_REQUEST
import com.castcle.networking.service.common.TIMEOUT_SHOWING_SPINNER
import com.castcle.ui.base.BaseViewModel
import com.castcle.usecase.signin.CheckEmailExsitSingleUseCase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.*
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

abstract class EmailFragmentViewModel : BaseViewModel() {

    abstract val showLoading: Observable<Boolean>

    abstract val error: Observable<Throwable>

    abstract val input: Input

    abstract val responseCheckEmailExsit: Observable<EmailVerifyUiModel>

    abstract val email: String

    interface Input {
        fun checkEmailExist(email: String)
    }
}

class EmailFragmentViewModelImpl @Inject constructor(
    private val rxSchedulerProvider: RxSchedulerProvider,
    private val checkEmailExsitSingleUseCase: CheckEmailExsitSingleUseCase
) : EmailFragmentViewModel(), EmailFragmentViewModel.Input {

    override val showLoading: Observable<Boolean>
        get() = _showLoading
    private val _showLoading = BehaviorSubject.create<Boolean>()

    override val error: Observable<Throwable>
        get() = _error
    private val _error = PublishSubject.create<Throwable>()

    override val input: Input
        get() = this

    override val email: String
        get() = _email.blockingFirst()

    private var _email = BehaviorSubject.create<String>()

    private var _emailCache: EmailVerifyUiModel? = null

    override fun checkEmailExist(email: String) =
        _email.onNext(email)

    override val responseCheckEmailExsit: Observable<EmailVerifyUiModel>
        get() = _email
            .debounce(
                TIMEOUT_SEARCH_REQUEST,
                TimeUnit.MILLISECONDS,
                rxSchedulerProvider.main().get()
            ).switchMapSingle { it ->
                getEmailExist(it)
                    .doIfTakeLongerThan(TIMEOUT_SHOWING_SPINNER, TimeUnit.MILLISECONDS) {
                        _showLoading.onNext(true)
                    }
                    .doFinally {
                        _showLoading.onNext(false)
                    }.onErrorResumeNext {
                        _error.onNext(it)
                        Single.just(EmailVerifyUiModel())
                    }
            }

    private fun getEmailExist(email: String): Single<EmailVerifyUiModel> {
        return checkEmailExsitSingleUseCase.execute(
            EmailRequest(email = email)
        ).map {
            _emailCache = it
            it
        }.doOnError {
            _error.onNext(it)
        }.doOnSubscribe {
            _showLoading.onNext(true)
        }
    }
}
