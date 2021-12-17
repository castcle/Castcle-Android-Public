package com.castcle.ui.feed

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.common_model.model.setting.PageHeaderUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.data.staticmodel.FeedContentType
import com.castcle.data.staticmodel.ModeType
import com.castcle.networking.api.feed.datasource.FeedRepository
import com.castcle.ui.util.SingleLiveEvent
import com.castcle.usecase.feed.*
import com.castcle.usecase.search.GetTopTrendsSingleUseCase
import com.castcle.usecase.setting.GetCachePageDataCompletableUseCase
import com.castcle.usecase.userprofile.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.BehaviorSubject
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
//  Created by sklim on 19/8/2021 AD at 11:34.
@SuppressLint("StaticFieldLeak")
class FeedFragmentViewModelImpl @Inject constructor(
    private val appContext: Context,
    private val isGuestModeSingleUseCase: IsGuestModeSingleUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
    private val feedNonAuthRepository: FeedRepository,
    private val likeContentCompletableUseCase: LikeContentCompletableUseCase,
    private val getTopTrendsSingleUseCase: GetTopTrendsSingleUseCase,
    private val checkCastUpLoadingFlowableCase: CheckCastUpLoadingFlowableCase,
    private val getCachePageDataCompletableUseCase: GetCachePageDataCompletableUseCase,
    private val deleteContentCompletableUseCase: DeleteContentCompletableUseCase,
    private val recastContentSingleUseCase: RecastContentCompletableUseCase,
    private val getCastcleIdSingleUseCase: GetCastcleIdSingleUseCase,
    private val reportContentUseCase: ReportContentUseCase,
    private val blockUserUseCase: BlockUserUseCase,
    private val unBlockUserUseCase: UnBlockUserUseCase,
) : FeedFragmentViewModel(), FeedFragmentViewModel.Input {

    override val input: Input
        get() = this

    private val _showLoading = BehaviorSubject.create<Boolean>()
    override val showLoading: Observable<Boolean>
        get() = _showLoading

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    private val _userProfile = MutableLiveData<User>()
    override val userProfile: LiveData<User>
        get() = _userProfile

    private lateinit var _feedUiMode: Flow<PagingData<ContentFeedUiModel>>
    override val feedContentPage: Flow<PagingData<ContentFeedUiModel>>
        get() = _feedUiMode

    override val isGuestMode: Boolean
        get() = isGuestModeSingleUseCase.execute(Unit).blockingGet()

    override val castcleId: String
        get() = getCastcleIdSingleUseCase.execute(Unit).blockingGet()

    private fun setUserProfileData(user: Optional<User>) {
        if (user.isPresent) {
            _userProfile.value = user.get()
        }
    }

    private val defaultFeedRequestHeader = FeedRequestHeader(
        featureSlug = FeedContentType.FEED_SLUG.type,
        circleSlug = FeedContentType.CIRCLE_SLUG_FORYOU.type,
        mode = ModeType.MODE_CURRENT.type,
        isMeId = castcleId
    )

    private var _queryFeedRequest = MutableStateFlow(defaultFeedRequestHeader)

    private val _userCachePage = MutableLiveData<List<String>>()

    init {
        if (isGuestMode) {
            getAllFeedGustsContent(_queryFeedRequest)
        } else {
            getAllFeedContent(_queryFeedRequest)
        }
    }

    private val _feedContentMock = MutableLiveData<List<ContentFeedUiModel>>()
    override val feedContentMock: LiveData<List<ContentFeedUiModel>>
        get() = _feedContentMock

    override fun setDefaultFeedRequestHeader() {
        setFetchFeedContent(defaultFeedRequestHeader)
    }

    override fun setFetchFeedContent(feedRequest: FeedRequestHeader) {
        _queryFeedRequest.value = feedRequest
    }

    override fun fetchUserProfile(): Completable =
        cachedUserProfileSingleUseCase
            .execute(Unit)
            .firstOrError()
            .zipWith(
                getCachePageDataCompletableUseCase.execute(Unit)
            ).doOnSuccess { (user, page) ->
                onBindCacheUserprofile(user, page)
            }.doOnSubscribe { _showLoading.onNext(true) }
            .doFinally { _showLoading.onNext(false) }
            .doOnError(_error::onNext)
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

    override fun reportContent(contentId: String): Completable {
        return reportContentUseCase.execute(contentId)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _showLoading.onNext(false) }
            .doFinally { _showLoading.onNext(false) }
    }

    override fun blockUser(userId: String): Completable {
        return blockUserUseCase.execute(userId)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _showLoading.onNext(false) }
            .doFinally { _showLoading.onNext(false) }
    }

    override fun unblockUser(userId: String): Completable {
        return unBlockUserUseCase.execute(userId)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _showLoading.onNext(false) }
            .doFinally { _showLoading.onNext(false) }
    }

    override fun getAllFeedContent(feedRequest: MutableStateFlow<FeedRequestHeader>) =
        launchPagingAsync({
            _showLoading.onNext(true)
            feedNonAuthRepository.getFeed(feedRequest).cachedIn(viewModelScope)
        }, {
            _showLoading.onNext(false)
            _feedUiMode = it
        })

    override fun getAllFeedGustsContent(feedRequest: MutableStateFlow<FeedRequestHeader>) =
        launchPagingAsync({
            _showLoading.onNext(true)
            feedNonAuthRepository.getFeedGuests(feedRequest).cachedIn(viewModelScope)
        }, {
            _showLoading.onNext(false)
            _feedUiMode = it
        })

    private var _onUpdateContentLike = BehaviorSubject.create<Unit>()
    override val onUpdateContentLike: Observable<Unit>
        get() = _onUpdateContentLike

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

    private var _trendsResponse = MutableLiveData<List<SearchUiModel>>()
    override val trendsResponse: LiveData<List<SearchUiModel>>
        get() = _trendsResponse

    override fun getTopTrends() {
        getTopTrendsSingleUseCase.execute(Unit)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doFinally { _showLoading.onNext(false) }
            .subscribeBy(
                onSuccess = {
                    setTrendResponse(it)
                },
                onError = {
                    _showLoading.onNext(false)
                    _error.onNext(it)
                }
            ).addToDisposables()
    }

    private fun setTrendResponse(list: List<SearchUiModel>) {
        _trendsResponse.value = list
    }

    private var _castPostResponse = SingleLiveEvent<ContentFeedUiModel>()
    override val castPostResponse: SingleLiveEvent<ContentFeedUiModel>
        get() = _castPostResponse

    override fun checkCastPostWithImageStatus(): Observable<Boolean> {
        return checkCastUpLoadingFlowableCase.execute(Unit)
            .map { (status, userResponse) ->
                userResponse.takeIf {
                    it.isNotBlank()
                }?.let {
                    checkCastPostResponse(userResponse)
                }
                status
            }.doOnError {
                _error.onNext(it)
            }.toObservable()
    }

    private fun checkCastPostResponse(userResponse: String) {
        if (userResponse.isNotBlank()) {
            val castPostRes = userResponse.toContentFeedUiModel()
            _castPostResponse.value = castPostRes
        }
    }

    override fun deleteContentFeed(contentId: String): Completable {
        return deleteContentCompletableUseCase.execute(
            DeleteContentCompletableUseCase.Input(
                contentId = contentId
            )
        )
    }

    override fun recastContent(contentUiModel: ContentFeedUiModel): Completable {
        val castcleId = _userProfile.value?.castcleId ?: ""

        val recastRequest = RecastRequest(
            reCasted = contentUiModel.recasted,
            contentId = contentUiModel.contentId,
            authorId = castcleId
        )
        return recastContentSingleUseCase.execute(recastRequest)
    }
}
