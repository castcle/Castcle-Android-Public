package com.castcle.usecase.login

import android.app.Activity
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.feed.domain.dao.FeedCacheDao
import com.castcle.common_model.model.feed.domain.dao.PageKeyDao
import com.castcle.components_android.ui.base.addToDisposables
import com.castcle.data.error.LoginError
import com.castcle.data.model.dao.user.UserDao
import com.castcle.data.storage.AppPreferences
import com.castcle.session_memory.SessionManagerRepository
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.splashscreen.SplashScreenActivity
import com.castcle.usecase.base.CompletableUseCase
import io.reactivex.Completable
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
//  Created by sklim on 31/8/2021 AD at 15:14.

class LogoutCompletableUseCase @Inject constructor(
    rxSchedulerProvider: RxSchedulerProvider,
    private val appPreferences: AppPreferences,
    private val secureStorage: SecureStorage,
    private val sessionManagerRepository: SessionManagerRepository,
    private val onBoardNavigator: OnBoardNavigator,
    private val userDao: UserDao,
    private val pageKeyDao: PageKeyDao,
    private val feedCacheDao: FeedCacheDao
) : CompletableUseCase<Activity>(
    rxSchedulerProvider.io(),
    rxSchedulerProvider.main(),
    ::LoginError
) {
    override fun create(input: Activity): Completable {
        appPreferences.clearAll()
            .subscribe()
            .addToDisposables()
        sessionManagerRepository.clearSession()
        userDao.deleteUser()
        pageKeyDao.clearAll()
        feedCacheDao.deleteFeedCache()
        with(secureStorage) {
            userTokenType = null
            userAccessToken = null
            userRefreshToken = null
        }
        (input as OnBoardActivity).run {
            SplashScreenActivity.start(this)
            finish()
        }
        onBoardNavigator.navigateBack()
        return Completable.complete()
    }
}
