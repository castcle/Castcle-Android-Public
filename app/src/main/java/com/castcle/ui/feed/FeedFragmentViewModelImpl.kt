package com.castcle.ui.feed

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.castcle.android.R
import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.api.response.FeedContentResponse
import com.castcle.common_model.model.feed.api.response.FeedResponse
import com.castcle.common_model.model.userprofile.User
import com.castcle.networking.api.feed.datasource.FeedRepository
import com.castcle.usecase.userprofile.GetCachedUserProfileSingleUseCase
import com.castcle.usecase.userprofile.GetCastcleIdSingleUseCase
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.io.InputStream
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
    private val getCastcleIdSingleUseCase: GetCastcleIdSingleUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
    private val feedNonAuthRepository: FeedRepository
) : FeedFragmentViewModel(), FeedFragmentViewModel.Input {

    override val input: Input
        get() = this

    private val _showLoading = BehaviorSubject.create<Boolean>()

    private val _error = PublishSubject.create<Throwable>()

    private val _userProfile = MutableLiveData<User>()
    override val userProfile: LiveData<User>
        get() = _userProfile

    private lateinit var _feedUiMode: Flow<PagingData<FeedContentResponse>>

    override val isGuestMode: Boolean
        get() = getCastcleIdSingleUseCase.execute(Unit).blockingGet()

    private fun setUserProfileData(user: Optional<User>) {
        _userProfile.value = user.get()
    }

    private val _feedContentMock = MutableLiveData<List<ContentUiModel>>()
    override val feedContentMock: LiveData<List<ContentUiModel>>
        get() = _feedContentMock

    override fun fetchUserProfile(): Completable =
        cachedUserProfileSingleUseCase
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

    override fun updateLikeContent(uuid: String) {
        val feedContent = _feedContentMock.value
        feedContent?.find {
            it.payLoadUiModel.author.displayName == uuid
        }?.apply {
            this.payLoadUiModel.likedUiModel.liked = !this.payLoadUiModel.likedUiModel.liked
        }?.let {
            setFeedContent(feedContent)
        }
    }

    override fun getFeedResponseMock() {
        val mockData = Gson().fromJson(
            JSONObject(readJSONFromAsset() ?: "").toString(),
            FeedResponse::class.java
        ).payload.toContentFeedUiModel()

        setFeedContent(mockData.feedContentUiModel)
    }

    private fun setFeedContent(feedContent: List<ContentUiModel>) {
        _feedContentMock.value = feedContent
    }

    private fun readJSONFromAsset(): String? {
        val json: String?
        try {
            val inputStream: InputStream? = appContext.resources?.openRawResource(
                R.raw.feed_mock
            )
            json = inputStream?.bufferedReader().use { it?.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return json
    }
}
