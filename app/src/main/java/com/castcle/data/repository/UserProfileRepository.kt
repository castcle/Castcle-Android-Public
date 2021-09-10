package com.castcle.data.repository

import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.userprofile.*
import com.castcle.data.model.dao.UserDao
import com.castcle.data.storage.AppPreferences
import com.castcle.networking.api.user.UserApi
import io.reactivex.*
import io.reactivex.subjects.BehaviorSubject
import java.util.*
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
//  Created by sklim on 31/8/2021 AD at 18:00.

interface UserProfileRepository {
    val currentUser: Flowable<User>

    val cachedUser: Flowable<Optional<User>>

    fun uppdateUserProfile(userUpdateRequest: UserUpdateRequest): Completable
}

class UserProfileRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi,
    private val userProfileMapper: UserProfileMapper,
    private val appPreferences: AppPreferences,
) : UserProfileRepository {

    override val currentUser: Flowable<User>
        get() = getUserProfile()

    override val cachedUser: Flowable<Optional<User>>
        get() = getCachedUserProfile()

    override fun uppdateUserProfile(userUpdateRequest: UserUpdateRequest): Completable {
        return userApi.updateUserProfile(userUpdateRequest)
            .map(userProfileMapper)
            .map { it.toUserProfile() }
            .doOnNext {
                _onMemoryUser = it
            }.ignoreElements()
    }

    private val _remoteUser = BehaviorSubject.create<User>()

    private var _onMemoryUserUpdatedDate: GregorianCalendar? = null

    private var _onMemoryUser: User? = null
        set(user) {
            _onMemoryUserUpdatedDate = GregorianCalendar()
            user?.run(userDao::refresh)
            field = user
        }

    private var _isLoadingUser = false

    private fun getUserProfile(): Flowable<User> {
        val fetchOnThisDay = _onMemoryUserUpdatedDate?.isSameDate(GregorianCalendar())?.not()
            ?: true
        val shouldFetchRemote = fetchOnThisDay && _isLoadingUser.not()

        return _onMemoryUser?.let { Flowable.just(it) }
            ?: if (shouldFetchRemote) {
                getUserRemoteProfile()
            } else {
                _remoteUser.toFlowable(BackpressureStrategy.LATEST)
            }
    }

    private fun getUserRemoteProfile(): Flowable<User> {
        return userApi.getUserProfile()
            .map(userProfileMapper)
            .map { it.toUserProfile() }
            .doOnSuccess {
                setCastcleId(it.castcleId)
                _onMemoryUser = it
                _remoteUser.onNext(it)
            }
            .doOnSubscribe { _isLoadingUser = true }
            .doFinally { _isLoadingUser = false }
            .toFlowable()
    }

    private fun getCachedUserProfile(): Flowable<Optional<User>> {
        return Flowable.concat(
            _onMemoryUser?.let {
                Flowable.just(Optional.of(it))
            } ?: userDao.getUser().map {
                Optional.of(it)
            }.onErrorReturn {
                Optional.empty()
            }.toFlowable(),
            getUserProfile()
                .map { Optional.of(it) }
        )
    }

    private fun setCastcleId(castcleId: String) {
        appPreferences.castcleId = castcleId
    }

    private fun GregorianCalendar.isSameDate(date: GregorianCalendar): Boolean {
        return get(Calendar.YEAR) == date.get(Calendar.YEAR)
            && get(Calendar.MONTH) == date.get(Calendar.MONTH)
            && get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
    }
}
