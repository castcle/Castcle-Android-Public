package com.castcle.ui.setting.language

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.common_model.model.setting.LanguageUiModel
import com.castcle.common_model.model.setting.toApplySelectedLanguage
import com.castcle.data.storage.AppPreferences
import com.castcle.session_memory.SessionManagerRepository
import com.castcle.ui.base.BaseViewModel
import com.castcle.usecase.setting.GetLanguageSingleUseCase
import com.castcle.usecase.setting.GetPreferredLanguageFlowableUseCase
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

abstract class LanguageFragmentViewModel : BaseViewModel() {

    abstract val preferredLanguage: LiveData<List<LanguageUiModel>>

    abstract fun getMeteAppLanguage()

    abstract val onSetLanguageSuccess: Observable<Unit>

    abstract fun getMapPreferredLanguage(
        list: List<LanguageUiModel>
    ): Completable
}

class LanguageFragmentViewModelImpl @Inject constructor(
    private val appPreferences: AppPreferences,
    private val secureStorage: SecureStorage,
    private val sessionManagerRepository: SessionManagerRepository,
    private val getLanguageSingleUseCase: GetLanguageSingleUseCase,
    private val getPreferredLanguageUseCase: GetPreferredLanguageFlowableUseCase
) : LanguageFragmentViewModel() {

    private val _error = PublishSubject.create<Throwable>()

    private val _languageUiModel = MutableLiveData<List<LanguageUiModel>>()
    override val preferredLanguage: LiveData<List<LanguageUiModel>>
        get() = _languageUiModel

    private var _onSetLanguageSuccess = BehaviorSubject.create<Unit>()
    override val onSetLanguageSuccess: Observable<Unit>
        get() = _onSetLanguageSuccess

    @SuppressLint("CheckResult")
    override fun getMeteAppLanguage() {
        getLanguageSingleUseCase.execute(Unit)
            .flatMapSingle {
                getCachePreferredLanguage(it)
            }.subscribeBy {
                setPreferredLanguage(it)
            }.addToDisposables()
    }

    private fun getCachePreferredLanguage(
        list: List<LanguageUiModel>
    ): Single<List<LanguageUiModel>> {
        return getPreferredLanguageUseCase
            .execute(Unit)
            .firstOrError()
            .map {
                list.toApplySelectedLanguage(it)
            }
    }

    override fun getMapPreferredLanguage(
        list: List<LanguageUiModel>
    ): Completable {
        return getPreferredLanguageUseCase
            .execute(Unit)
            .map {
                list.toApplySelectedLanguage(it)
            }.doOnNext {
                setPreferredLanguage(it)
            }.ignoreElements()
    }

    private fun setPreferredLanguage(item: List<LanguageUiModel>) {
        _languageUiModel.value = item
    }
}
