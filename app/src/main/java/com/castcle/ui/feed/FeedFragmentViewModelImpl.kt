package com.castcle.ui.feed

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.castcle.common_model.ContentBaseUiModel.CommonContentBaseUiModel.ContentFeedUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.common_model.model.feed.api.response.FeedContentResponse
import com.castcle.common_model.model.userprofile.User
import com.castcle.networking.api.feed.datasource.FeedRepository
import com.castcle.usecase.userprofile.GetCastcleIdSingleUseCase
import com.castcle.usecase.userprofile.GetUserProfileSingleUseCase
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
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
//  Created by sklim on 19/8/2021 AD at 11:34.

class FeedFragmentViewModelImpl @Inject constructor(
    private val getCastcleIdSingleUseCase: GetCastcleIdSingleUseCase,
    private val userProfileSingleUseCase: GetUserProfileSingleUseCase,
    private val feedNonAuthRepository: FeedRepository
) : FeedFragmentViewModel() {

    private val _showLoading = BehaviorSubject.create<Boolean>()

    private val _error = PublishSubject.create<Throwable>()

    private val _userProfile = MutableLiveData<User>()
    override val userProfile: LiveData<User>
        get() = _userProfile

    private lateinit var _feedUiMode: Flow<PagingData<FeedContentResponse>>

    private var _feedMockUiModel = MutableLiveData<ContentFeedUiModel>()
    override val feedMockUiModel: LiveData<ContentFeedUiModel>
        get() = _feedMockUiModel

    override val isGuestMode: Boolean
        get() = getCastcleIdSingleUseCase.execute(Unit).blockingGet()

    private fun setUserProfileData(user: User) {
        _userProfile.value = user
    }

    override fun fetchUserProfile(): Completable =
        userProfileSingleUseCase
            .execute(Unit)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doFinally { _showLoading.onNext(false) }
            .doOnNext(::setUserProfileData)
            .doOnError(_error::onNext).firstOrError()
            .ignoreElement()

    private fun getAllCharacters() = launchPagingAsync({
        val feedRequestHeader = FeedRequestHeader(
            "Feed",
            "Foryou"
        )
        feedNonAuthRepository.getFeed(feedRequestHeader).cachedIn(viewModelScope)
    }, {
        _feedUiMode = it
    })

    override fun getMockFeed() {
        val gson = Gson()
    }
}
