package com.castcle.ui.signin.createdisplayname

import com.castcle.common.lib.extension.doIfTakeLongerThan
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.login.RegisterBundle
import com.castcle.common_model.model.setting.CreatePageRequest
import com.castcle.common_model.model.setting.CreatePageResponse
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel.CastcleIdVerifyUiModel
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel.DisplayNameVerifyUiModel
import com.castcle.common_model.model.signin.reuquest.*
import com.castcle.networking.service.common.TIMEOUT_SHOWING_SPINNER
import com.castcle.ui.base.BaseViewModel
import com.castcle.usecase.setting.CreatePageSingleUseCase
import com.castcle.usecase.signin.*
import com.castcle.usecase.userprofile.GetUserProfileSingleUseCase
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

abstract class CreateDisplayNameFragmentViewModel : BaseViewModel() {

    abstract val showLoading: Observable<Boolean>

    abstract val error: Observable<Throwable>

    abstract val verifyProfileState: Observable<VerifyProfileState>

    abstract val responseCastcleIdSuggest: Observable<DisplayNameVerifyUiModel>

    abstract val input: Input

    interface Input {
        fun displayName(displayName: String)

        fun checkCastcleId(castcleId: String)

        fun authRegisterWithEmail(
            registerBundle: RegisterBundle.RegisterWithEmail,
            displayName: String,
            castcleId: String
        ): Completable

        fun createPage(
            displayName: String,
            castcleId: String
        ): Single<CreatePageResponse>
    }
}

class CreateDisplayNameFragmentViewModelImpl @Inject constructor(
    private val rxSchedulerProvider: RxSchedulerProvider,
    private val registerCompletableUseCase: AuthenticationRegisterCompletableUseCase,
    private val suggestDisplayNameSingleUseCase: SuggestDisplayNameSingleUseCase,
    private val checkCastcleIdExsitSingleUseCase: CheckCastcleIdExsitSingleUseCase,
    private val getUserProfileSingleUseCase: GetUserProfileSingleUseCase,
    private val createPageSingleUseCase: CreatePageSingleUseCase
) : CreateDisplayNameFragmentViewModel(), CreateDisplayNameFragmentViewModel.Input {

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

    private var _castcleIdState = VerifyProfileState.NONE

    private val _castcleIdSuggest = BehaviorSubject.create<String>()

    override val input: Input
        get() = this

    override fun authRegisterWithEmail(
        registerBundle: RegisterBundle.RegisterWithEmail,
        displayName: String,
        castcleId: String
    ): Completable {
        return registerCompletableUseCase.execute(
            RegisterRequest(
                channel = EMAIL_REGUEST,
                payload = RegisterPayLoad(
                    email = registerBundle.email,
                    castcleId = castcleId,
                    displayName = displayName,
                    password = registerBundle.password ?: ""
                )
            )
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.doOnError {
            _showLoading.onNext(false)
        }.doOnComplete {
            getUserProfile()
        }
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
        responseCastcleIdExsit
            .subscribe(::handlerDisplayName)
            .addToDisposables()
    }

    private fun handlerDisplayName(authVerifyBaseUiModel: AuthVerifyBaseUiModel) {
        if (authVerifyBaseUiModel is CastcleIdVerifyUiModel) {
            val displayNameExsit = authVerifyBaseUiModel.exist
            if (!displayNameExsit && authVerifyBaseUiModel.message.isNotBlank()) {
                VerifyProfileState.CASTCLE_ID_PASS.also {
                    _disPlayNameState = it
                    _verifyState.onNext(it)
                }
            } else {
                _verifyState.onNext(VerifyProfileState.CASTCLE_ID_ERROR)
            }
        }
    }

    private val responseCastcleIdExsit: Observable<CastcleIdVerifyUiModel>
        get() = _castcleId
            .debounce(
                TIMEOUT_SEARCH_REQUEST,
                TimeUnit.MILLISECONDS,
                rxSchedulerProvider.main().get()
            ).switchMapSingle { it ->
                onCheckCastcleIdExsit(it)
                    .doIfTakeLongerThan(TIMEOUT_SHOWING_SPINNER, TimeUnit.MILLISECONDS) {
                        _showLoading.onNext(true)
                    }
                    .doFinally {
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

    override fun createPage(displayName: String, castcleId: String): Single<CreatePageResponse> {
        return createPageSingleUseCase.execute(
            CreatePageRequest(
                displayName = displayName,
                castcleId = castcleId
            )
        ).doOnError {
            _showLoading.onNext(false)
        }.doOnSubscribe {
            _showLoading.onNext(true)
        }
    }
}

private const val TIMEOUT_SEARCH_REQUEST = 500L
private const val EMAIL_REGUEST = "email"
