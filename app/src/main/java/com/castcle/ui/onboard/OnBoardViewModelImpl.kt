package com.castcle.ui.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common_model.model.setting.LanguageUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.usecase.login.LogoutCompletableUseCase
import com.castcle.usecase.setting.*
import com.castcle.usecase.userprofile.GetCastcleIdSingleUseCase
import com.castcle.usecase.userprofile.GetUserProfileSingleUseCase
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
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
//  Created by sklim on 19/8/2021 AD at 11:02.

class OnBoardViewModelImpl @Inject constructor(
    private val userProfileSingleUseCase: GetUserProfileSingleUseCase,
    private val getCastcleIdSingleUseCase: GetCastcleIdSingleUseCase,
    private val logoutCompletableUseCase: LogoutCompletableUseCase,
    private val getAppLanguageSingleUseCase: GetAppLanguageSingleUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
    private val setPreferredLanguageUseCase: SetPreferredLanguageUseCase,
    private val getPreferredLanguageUseCase: GetPreferredLanguageUseCase
) : OnBoardViewModel() {

    private val _userProfile = BehaviorSubject.create<User>()

    override val user: Observable<User>
        get() = _userProfile

    private val _error = PublishSubject.create<Throwable>()

    override val isGuestMode: Boolean
        get() = getCastcleIdSingleUseCase.execute(Unit).blockingGet()

    private val _currentAppLanguage = MutableLiveData<List<LanguageUiModel>>()
    override val currentAppLanguage: LiveData<List<LanguageUiModel>>
        get() = _currentAppLanguage

    private var _onSetLanguageSuccess = BehaviorSubject.create<Unit>()
    override val onSetLanguageSuccess: Observable<Unit>
        get() = _onSetLanguageSuccess

    private val _preferredLanguage = MutableLiveData<List<LanguageUiModel>>()
    override val preferredLanguage: LiveData<List<LanguageUiModel>>
        get() = _preferredLanguage

    private val _languageCache = MutableLiveData<MutableList<LanguageUiModel>>()

    private val _preferredLanguageSelected = MutableLiveData<MutableList<LanguageUiModel>>()
    override val preferredLanguageSelected: LiveData<MutableList<LanguageUiModel>>
        get() = _preferredLanguageSelected

    override fun onRefreshProfile() {
        userProfileSingleUseCase.execute(Unit)
            .subscribeBy(
                onNext = _userProfile::onNext,
                onError = {}
            ).addToDisposables()
    }

    override fun onAccessTokenExpired(): Completable {
        return logoutCompletableUseCase.execute(Unit)
    }

    override fun getCurrentAppLanguage(): Completable {
        return getAppLanguageSingleUseCase.execute(Unit)
            .doOnSuccess {
                setAppLanguage(it)
            }.doOnError {
                _error.onNext(it)
            }.ignoreElement()
    }

    override fun setAppLanguage(languageCode: String, isUpdate: Boolean) {
        val language = _currentAppLanguage.value
        language?.find {
            it.isSelected
        }?.let {
            it.isSelected = false
        }
        language?.find {
            it.code == languageCode
        }?.let {
            it.isSelected = !it.isSelected
            setCurrentAppLanguage(language)
            if (isUpdate) {
                setAppLanguageCase(it.code)
                    .subscribeBy {
                        _onSetLanguageSuccess.onNext(Unit)
                    }.addToDisposables()
            }
        }
    }

    override fun setPerferredLanguage(languageCode: String, isShow: Boolean) {
        val language = _preferredLanguage.value
        language?.find {
            it.code == languageCode
        }?.let {
            it.isSelected = !it.isSelected
            it.isShow = isShow
            setPreferredLanguageData(language)
            setPerferredLanguageSelectedData()
        }
    }

    override fun setPreferredLanguageData(language: List<LanguageUiModel>) {
        _preferredLanguage.value = language
        _languageCache.value = language.toMutableList()

    }

    private fun setPerferredLanguageSelectedData() {
        _preferredLanguage.value?.filter {
            it.isSelected
        }?.let {
            _preferredLanguageSelected.value = it.toMutableList()
            val prefer = Gson().toJson(it)
            setPreferredLanguageCase(prefer)
                .subscribe()
                .addToDisposables()
        }
    }

    private fun setPreferredLanguageCase(language: String): Completable {
        return setPreferredLanguageUseCase.execute(language)
            .doOnError {
                _error.onNext(it)
            }
    }

    override fun setCurrentAppLanguage(languageList: List<LanguageUiModel>) {
        _currentAppLanguage.value = languageList
    }

    private fun setAppLanguageCase(languageCode: String): Completable {
        return setAppLanguageUseCase.execute(languageCode)
    }

    override fun getCachePreferredLanguage(): Completable {
        return getPreferredLanguageUseCase.execute(Unit)
            .doOnSuccess {
                _preferredLanguageSelected.value = it.toMutableList()
            }.ignoreElement()
    }

    override fun filterSearchLanguage(display: String) {
        val cache = _languageCache.value
        if (display.isNotBlank()) {
            cache?.filter {
                it.title.contains(display, true)
            }?.let {
                _preferredLanguage.value = it
            }
        } else {
            _preferredLanguage.value = cache
        }
    }
}
