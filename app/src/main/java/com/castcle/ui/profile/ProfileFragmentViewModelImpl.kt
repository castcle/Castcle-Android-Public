package com.castcle.ui.profile

import androidx.paging.LoadState
import androidx.paging.PagingData
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.common_model.model.userprofile.User
import com.castcle.data.repository.UserProfileRepository
import com.castcle.usecase.userprofile.GetUserProfileSingleUseCase
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.*
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
//  Created by sklim on 10/9/2021 AD at 11:39.

class ProfileFragmentViewModelImpl @Inject constructor(
    private val getUserProfileSingleUseCase: GetUserProfileSingleUseCase,
    private val userProfileDataSouce: UserProfileRepository
) : ProfileFragmentViewModel() {

    private val _error = PublishSubject.create<Throwable>()

    private lateinit var _userProfileContentRes: Flow<PagingData<ContentUiModel>>

    override val userProfileRes: Observable<User>
        get() = fetchUserProfile()

    private fun fetchUserProfile(): Observable<User> {
        return getUserProfileSingleUseCase
            .execute(Unit)
            .doOnError(_error::onNext)
            .toObservable()
    }

    override val userProfileContentRes: Flow<PagingData<ContentUiModel>>
        get() = _userProfileContentRes

    override fun fetachUserProfileContent() {
        launchPagingAsync({
            userProfileDataSouce.getUserPofileContent()
        },onSuccess =  {
            _userProfileContentRes = it
        })
    }
}
