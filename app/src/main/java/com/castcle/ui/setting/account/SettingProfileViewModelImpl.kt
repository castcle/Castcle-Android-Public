package com.castcle.ui.setting.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.setting.SettingMenuType
import com.castcle.common_model.model.setting.SettingMenuUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.data.staticmodel.StaticSeetingMenu
import com.castcle.data.storage.AppPreferences
import com.castcle.usecase.userprofile.GetCachedUserProfileSingleUseCase
import io.reactivex.Completable
import io.reactivex.Observable
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
//  Created by sklim on 30/9/2021 AD at 12:30.

class SettingProfileViewModelImpl @Inject constructor(
    private val appPreferences: AppPreferences,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
) : SettingProfileViewModel() {

    private val _error = PublishSubject.create<Throwable>()

    private val _userCachePage = MutableLiveData<User>()
    override val userCachePage: LiveData<User>
        get() = _userCachePage

    override fun fetchUserProfile(): Completable =
        cachedUserProfileSingleUseCase
            .execute(Unit)
            .firstOrError()
            .doOnSuccess { user ->
                onBindCacheUserprofile(user)
            }.doOnError(_error::onNext)
            .ignoreElement()

    private fun onBindCacheUserprofile(user: Optional<User>) {
        if (user.isPresent) {
            _userCachePage.value = user.get()
        }
    }

    override fun getProfileSettingMenu(): Observable<List<SettingMenuUiModel>> {
        return Observable.fromCallable {
            getMapProfilSetting()
        }
    }

    private fun getMapProfilSetting(): List<SettingMenuUiModel> {
        val menuDate = StaticSeetingMenu.staticProfileSetting
        menuDate.forEach {
            it.menuItem.find { itemMenu ->
                itemMenu.menuType == SettingMenuType.SETTING_EMAIL
            }?.apply {
                menuDetail = appPreferences.email ?: ""
            }
        }
        return menuDate
    }
}