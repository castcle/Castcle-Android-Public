package com.castcle.ui.signin.createaccount

import com.castcle.common.lib.extension.doIfTakeLongerThan
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel.CastcleIdVerifyUiModel
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel.DisplayNameVerifyUiModel
import com.castcle.common_model.model.signin.domain.*
import com.castcle.common_model.model.userprofile.domain.UserUpdateRequest
import com.castcle.networking.service.common.TIMEOUT_SHOWING_SPINNER
import com.castcle.ui.base.BaseViewModel
import com.castcle.ui.signin.createdisplayname.VerifyProfileState
import com.castcle.usecase.signin.*
import com.castcle.usecase.userprofile.GetUserProfileSingleUseCase
import com.castcle.usecase.userprofile.UpdateUserProfileCompletableUseCase
import io.reactivex.*
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

abstract class CreateAccountFragmentViewModel : BaseViewModel() {

    abstract val showLoading: Observable<Boolean>

    abstract val error: Observable<Throwable>

    abstract val verifyProfileState: Observable<VerifyProfileState>

    abstract val responseCastcleIdSuggest: Observable<DisplayNameVerifyUiModel>

    abstract val responseCastcleIdExsit: Observable<CastcleIdVerifyUiModel>

    abstract fun requestUpdateProfile(userRequest: UserUpdateRequest): Completable

    abstract val input: Input

    interface Input {
        fun displayName(displayName: String)

        fun checkCastcleId(castcleId: String)

        fun authRegisterWithSocial(
            registerRequest: RegisterWithSocialRequest
        ): Completable

    }
}

class CreateAccountFragmentViewModelImpl @Inject constructor(
    private val rxSchedulerProvider: RxSchedulerProvider,
    private val updateUserProfileCompletableUseCase: UpdateUserProfileCompletableUseCase,
    private val registerWithSocialCompletableUseCase: RegisterWithSocialCompletableUseCase,
    private val suggestDisplayNameSingleUseCase: SuggestDisplayNameSingleUseCase,
    private val checkCastcleIdExsitSingleUseCase: CheckCastcleIdExsitSingleUseCase,
    private val getUserProfileSingleUseCase: GetUserProfileSingleUseCase,
) : CreateAccountFragmentViewModel(), CreateAccountFragmentViewModel.Input {

    override val showLoading: Observable<Boolean>
        get() = _showLoading
    private val _showLoading = BehaviorSubject.create<Boolean>()

    override val error: Observable<Throwable>
        get() = _error
    private val _error = PublishSubject.create<Throwable>()

    override val verifyProfileState: Observable<VerifyProfileState>
        get() = _verifyState
    private val _verifyState = BehaviorSubject.create<VerifyProfileState>()

    private val _displayName = BehaviorSubject.create<String>()

    private val _castcleId = BehaviorSubject.create<String>()

    private var _disPlayNameState = VerifyProfileState.NONE

    override val input: Input
        get() = this

    override fun authRegisterWithSocial(
        registerRequest: RegisterWithSocialRequest
    ): Completable {
        return registerWithSocialCompletableUseCase.execute(
            registerRequest
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.doOnError {
            _showLoading.onNext(false)
        }.ignoreElement()
    }

    override fun requestUpdateProfile(userRequest: UserUpdateRequest): Completable {
        return updateUserProfileCompletableUseCase
            .execute(userRequest)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _showLoading.onNext(false) }
            .doFinally { _showLoading.onNext(false) }
    }

    private fun getUserProfile() {
        getUserProfileSingleUseCase
            .execute(Unit)
            .subscribe()
            .addToDisposables()
    }

    override fun displayName(displayName: String) {
        _displayName.onNext(displayName)
    }

    override fun checkCastcleId(castcleId: String) {
        _castcleId.onNext(castcleId)
    }

    private fun handlerDisplayName(authVerifyBaseUiModel: AuthVerifyBaseUiModel) {
        if (authVerifyBaseUiModel is CastcleIdVerifyUiModel) {
            val displayNameExist = authVerifyBaseUiModel.exist
            if (!displayNameExist && authVerifyBaseUiModel.message.isNotBlank()) {
                VerifyProfileState.CASTCLE_ID_PASS.also {
                    _disPlayNameState = it
                    _verifyState.onNext(it)
                }
            } else {
                _verifyState.onNext(VerifyProfileState.CASTCLE_ID_ERROR)
            }
        }
    }

    override val responseCastcleIdExsit: Observable<CastcleIdVerifyUiModel>
        get() = _castcleId
            .debounce(
                TIMEOUT_SEARCH_REQUEST,
                TimeUnit.MILLISECONDS,
                rxSchedulerProvider.main().get()
            ).switchMapSingle { it ->
                onCheckCastcleIdExsit(it)
                    .doIfTakeLongerThan(TIMEOUT_SHOWING_SPINNER, TimeUnit.MILLISECONDS) {
                        _showLoading.onNext(true)
                    }.doOnSuccess {
                        handlerDisplayName(it)
                    }.doFinally {
                        _showLoading.onNext(false)
                    }.onErrorResumeNext {
                        _error.onNext(it)
                        Single.just(CastcleIdVerifyUiModel())
                    }
            }

    private fun onCheckCastcleIdExsit(castcleId: String): Single<CastcleIdVerifyUiModel> {
        return checkCastcleIdExsitSingleUseCase.execute(
            CastcleIdRequest(castcleId = castcleId)
        ).doOnError {
            _error.onNext(it)
        }.doOnSubscribe {
            _showLoading.onNext(true)
        }
    }

    override val responseCastcleIdSuggest: Observable<DisplayNameVerifyUiModel>
        get() = _displayName
            .debounce(
                TIMEOUT_SEARCH_REQUEST,
                TimeUnit.MILLISECONDS,
                rxSchedulerProvider.main().get()
            ).switchMapSingle { it ->
                fetchSuggestCastcleId(it)
                    .doIfTakeLongerThan(TIMEOUT_SHOWING_SPINNER, TimeUnit.MILLISECONDS) {
                        _showLoading.onNext(true)
                    }
                    .doFinally {
                        _showLoading.onNext(false)
                    }.onErrorResumeNext {
                        _error.onNext(it)
                        Single.just(DisplayNameVerifyUiModel())
                    }
            }

    private fun fetchSuggestCastcleId(displayName: String): Single<DisplayNameVerifyUiModel> {
        return suggestDisplayNameSingleUseCase.execute(
            DisplayNameRequest(displayName = displayName)
        ).doOnError {
            _error.onNext(it)
        }.doOnSubscribe {
            _showLoading.onNext(true)
        }
    }
}

private const val TIMEOUT_SEARCH_REQUEST = 500L
private const val EMAIL_REGUEST = "email"
