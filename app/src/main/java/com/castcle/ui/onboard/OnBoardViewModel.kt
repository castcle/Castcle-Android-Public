package com.castcle.ui.onboard

import androidx.lifecycle.LiveData
import com.castcle.common_model.model.setting.LanguageUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.ui.base.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Observable

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
//  Created by sklim on 19/8/2021 AD at 11:01.

abstract class OnBoardViewModel : BaseViewModel() {

    abstract val user: Observable<User>

    abstract val isGuestMode: Boolean

    abstract val onSetLanguageSuccess: Observable<Unit>

    abstract val preferredLanguage: LiveData<List<LanguageUiModel>>

    abstract val preferredLanguageSelected: LiveData<MutableList<LanguageUiModel>>

    abstract fun getCachePreferredLanguage(): Completable

    abstract fun getCurrentAppLanguage(): Completable

    abstract fun setPerferredLanguage(languageCode: String, isShow: Boolean = true)

    abstract fun setPreferredLanguageData(language: List<LanguageUiModel>)

    abstract fun onAccessTokenExpired(): Completable

    abstract fun onRefreshProfile()

    abstract val currentAppLanguage: LiveData<List<LanguageUiModel>>

    abstract fun setAppLanguage(languageCode: String, isUpdate: Boolean = false)

    abstract fun setCurrentAppLanguage(languageList: List<LanguageUiModel>)

    abstract fun filterSearchLanguage(display: String)
}
