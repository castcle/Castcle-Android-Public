package com.castcle.ui.onboard

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.setting.*
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.domain.FollowRequest
import com.castcle.ui.util.SingleLiveEvent
import com.castcle.usecase.auth.UpdateFireBaseTokenCompleteUseCase
import com.castcle.usecase.login.LogoutCompletableUseCase
import com.castcle.usecase.setting.*
import com.castcle.usecase.userprofile.*
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
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
    private val isGuestModeSingleUseCase: IsGuestModeSingleUseCase,
    private val logoutCompletableUseCase: LogoutCompletableUseCase,
    private val getAppLanguageSingleUseCase: GetAppLanguageSingleUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
    private val setPreferredLanguageUseCase: SetPreferredLanguageUseCase,
    private val getPreferredLanguageUseCase: GetPreferredLanguageUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
    private val getCachePageDataCompletableUseCase: GetCachePageDataCompletableUseCase,
    private val putToFollowUserCompletableUseCase: PutToFollowUserCompletableUseCase,
    private val getCastcleIdSingleUseCase: GetCastcleIdSingleUseCase,
    private val updateFireBaseTokenCompleteUseCase: UpdateFireBaseTokenCompleteUseCase
) : OnBoardViewModel() {

    private val _userProfile = BehaviorSubject.create<User>()

    override val userRefeshProfile: Observable<User>
        get() = _userProfile

    private val _error = PublishSubject.create<Throwable>()

    override val isGuestMode: Boolean
        get() = isGuestModeSingleUseCase.execute(Unit).blockingGet()

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

    private var _isContentTypeYouId = MutableLiveData<String>()
    override val isContentTypeYouId: LiveData<String>
        get() = _isContentTypeYouId

    private val _userCachePage = MutableLiveData<List<String>>()

    private val _userCacheProfile = MutableLiveData<User>()
    override val userCacheProfile: LiveData<User>
        get() = _userCacheProfile

    init {
        if (!isGuestMode) {
            fetchUserProfile()
        }
    }

    override val castcleId: String
        get() = getCastcleIdSingleUseCase.execute(Unit).blockingGet()

    override fun fetchUserProfile(): Completable =
        cachedUserProfileSingleUseCase
            .execute(Unit)
            .firstOrError()
            .zipWith(
                getCachePageDataCompletableUseCase.execute(Unit)
            ).doOnSuccess { (user, page) ->
                onBindCacheUserprofile(user, page)
            }.doOnError(_error::onNext)
            .ignoreElement()

    private fun onBindCacheUserprofile(user: Optional<User>, page: PageHeaderUiModel?) {
        setUserProfileData(user)
        if (user.isPresent) {
            page?.pageUiItem?.map {
                it.castcleId
            }?.apply {
                toMutableList().add(0, user.get().castcleId)
            }.run {
                _userCachePage.value = this
            }
        }
    }

    override fun checkContentIsMe(
        castcleId: String,
        onProfileMe: () -> Unit,
        onPageMe: () -> Unit,
        non: () -> Unit
    ) {
        if (_userProfile.value?.castcleId == castcleId) {
            onProfileMe.invoke()
            return
        }
        if (_userCachePage.value?.contains(castcleId) == true) {
            onPageMe.invoke()
            return
        }
        non.invoke()
    }

    private fun setUserProfileData(user: Optional<User>) {
        if (user.isPresent) {
            _userCacheProfile.value = user.get()
        }
    }

    override fun setContentTypeYouId(isContentId: String) {
        _isContentTypeYouId.value = isContentId
    }

    override fun onRefreshProfile() {
        userProfileSingleUseCase.execute(Unit)
            .subscribeBy(
                onNext = _userProfile::onNext,
                onError = {}
            ).addToDisposables()
    }

    override fun onAccessTokenExpired(onActivity: Activity): Completable {
        return logoutCompletableUseCase.execute(onActivity)
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
            cache?.let {
                _preferredLanguage.value = it
            }
        }
    }

    private var _trendSlug = MutableLiveData<String>()
    override val trendSlug: LiveData<String>
        get() = _trendSlug

    override fun setTrendSlugData(slug: String) {
        _trendSlug.value = slug
    }

    private val _onLogoutActive = BehaviorSubject.create<Boolean>()
    override val onLogoutActive: Observable<Boolean>
        get() = _onLogoutActive

    override fun onLogoutDialog(isActive: Boolean) {
        _onLogoutActive.onNext(isActive)
    }

    private var _isContentProfileType = MutableLiveData<ProfileType>()
    override val isContentProfileType: LiveData<ProfileType>
        get() = _isContentProfileType

    override fun setProfileType(isProfileType: ProfileType) {
        _isContentProfileType.value = isProfileType
    }

    private var _imageResponse = MutableLiveData<Uri>()
    override val imageResponse: LiveData<Uri>
        get() = _imageResponse

    override fun setImageResponse(imagePath: Uri) {
        _imageResponse.value = imagePath
    }

    private var _onBackToFeed = BehaviorSubject.create<Unit>()
    override val onBackToFeed: Observable<Unit>
        get() = _onBackToFeed

    override fun onBackToHomeFeed() {
        _onBackToFeed.onNext(Unit)
    }

    private var _profileContentLoading = BehaviorSubject.create<Boolean>()
    override val profileContentLoading: Observable<Boolean>
        get() = _profileContentLoading

    override fun onProfileLoading(onLoading: Boolean) {
        _profileContentLoading.onNext(onLoading)
    }

    private var _onRefreshPositionRes = SingleLiveEvent<Unit>()
    override val onRefreshPositionRes: SingleLiveEvent<Unit>
        get() = _onRefreshPositionRes

    override fun onRefreshPosition() {
        _onRefreshPositionRes.value = Unit
    }

    override fun putToFollowUser(castcleId: String, authorId: String): Completable {
        return putToFollowUserCompletableUseCase.execute(
            FollowRequest(
                castcleIdFollower = this.castcleId,
                targetCastcleId = castcleId,
                authorId = authorId
            )
        )
    }

    override fun updateFireBaseToken(token: String): Completable {
        return updateFireBaseTokenCompleteUseCase.execute(token)
    }

    private var _onRegisterSuccess = SingleLiveEvent<Unit>()
    override val onRegisterSuccess: SingleLiveEvent<Unit>
        get() = _onRegisterSuccess

    override fun onRegisterSuccess() {
        _onRegisterSuccess.value = Unit
    }
}
