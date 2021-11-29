package com.castcle.ui.search.trend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.userprofile.User
import com.castcle.networking.api.feed.datasource.FeedRepository
import com.castcle.usecase.feed.LikeContentCompletableUseCase
import com.castcle.usecase.userprofile.GetCachedUserProfileSingleUseCase
import com.castcle.usecase.userprofile.IsGuestModeSingleUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
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
//  Created by sklim on 7/10/2021 AD at 18:39.

class TrendFragmentViewModelImpl @Inject constructor(
    private val trendRepository: FeedRepository,
    private val isGuestModeSingleUseCase: IsGuestModeSingleUseCase,
    private val likeContentCompletableUseCase: LikeContentCompletableUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
) : TrendFragmentViewModel() {

    private lateinit var _feedTrendResponse: Flow<PagingData<ContentUiModel>>
    override val feedTrendResponse: Flow<PagingData<ContentUiModel>>
        get() = _feedTrendResponse

    override val isGuestMode: Boolean
        get() = isGuestModeSingleUseCase.execute(Unit).blockingGet()

    private var _onUpdateContentLike = BehaviorSubject.create<Unit>()
    override val onUpdateContentLike: Observable<Unit>
        get() = _onUpdateContentLike

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    private var _cacheUserProfile = MutableLiveData<User>()
    override val userProfile: LiveData<User>
        get() = _cacheUserProfile

    override fun getTesnds(contentRequestHeader: FeedRequestHeader) =
        launchPagingAsync({
            trendRepository.getTrend(contentRequestHeader)
        }, onSuccess = {
            _feedTrendResponse = it
        })

    override fun fetchUserProfile() {
        cachedUserProfileSingleUseCase.execute(Unit)
            .firstOrError()
            .subscribeBy(
                onSuccess = {
                    setCacUserProfile(it)
                }, onError = {
                    _error.onNext(it)
                }
            ).addToDisposables()
    }

    private fun setCacUserProfile(user: Optional<User>) {
        if (user.isPresent) {
            _cacheUserProfile.value = user.get()
        }
    }

    override fun updateLikeContent(likeContentRequest: LikeContentRequest) {
        postLikeContent(likeContentRequest)
            .subscribeBy(
                onComplete = {
                    _onUpdateContentLike.onNext(Unit)
                },
                onError = {
                    _error.onNext(it)
                }
            )
            .addToDisposables()
    }

    private fun postLikeContent(contentUiModel: LikeContentRequest): Completable {
        return likeContentCompletableUseCase
            .execute(contentUiModel).doOnError {
                _error.onNext(it)
            }
    }
}
