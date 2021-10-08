package com.castcle.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.castcle.android.R
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.api.response.FeedResponse
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.ViewProfileRequest
import com.castcle.data.repository.UserProfileRepository
import com.castcle.data.staticmodel.ContentType
import com.castcle.networking.api.feed.datasource.FeedRepository
import com.castcle.usecase.userprofile.GetUserProfileSingleUseCase
import com.castcle.usecase.userprofile.GetUserViewProfileSingleUseCase
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
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
//  Created by sklim on 10/9/2021 AD at 11:39.
@SuppressLint("StaticFieldLeak")
class ProfileFragmentViewModelImpl @Inject constructor(
    private val appContext: Context,
    private val getUserProfileSingleUseCase: GetUserProfileSingleUseCase,
    private val getUserViewProfileSingleUseCase: GetUserViewProfileSingleUseCase,
    private val feedNonAuthRepository: FeedRepository,
    private val userProfileDataSouce: UserProfileRepository
) : ProfileFragmentViewModel() {

    private lateinit var _userProfileContentRes: Flow<PagingData<ContentUiModel>>

    private var _userProfileData = MutableLiveData<User>()

    override val userProfileRes: Observable<User>
        get() = fetchUserProfile()

    private lateinit var _feedContent: Flow<PagingData<ContentUiModel>>
    override val feedContentPage: Flow<PagingData<ContentUiModel>>
        get() = _feedContent

    private val _userProfileContentMock = BehaviorSubject.create<List<ContentUiModel>>()
    override val userProfileContentMock: Observable<List<ContentUiModel>>
        get() = _userProfileContentMock

    private val _userProfileYouContentMock = BehaviorSubject.create<User>()
    override val userProfileYouRes: Observable<User>
        get() = _userProfileYouContentMock

    private val _showLoading = BehaviorSubject.create<Boolean>()
    override val showLoading: Observable<Boolean>
        get() = _showLoading

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    private fun fetchUserProfile(): Observable<User> {
        return getUserProfileSingleUseCase
            .execute(Unit)
            .doOnNext {
                setUserProfileData(it)
            }
            .doOnError(_error::onNext)
            .toObservable()
    }

    private fun setUserProfileData(userData: User) {
        _userProfileData.value = userData
    }

    override val userProfileContentRes: Flow<PagingData<ContentUiModel>>
        get() = _userProfileContentRes

    override fun fetachUserProfileContent(contentRequestHeader: FeedRequestHeader) {
        launchPagingAsync({
            userProfileDataSouce.getUserPofileContent(contentRequestHeader)
        }, onSuccess = {
            _userProfileContentRes = it
        })
    }

    override fun fetachUserViewProfileContent(contentRequestHeader: FeedRequestHeader) {
        launchPagingAsync({
            userProfileDataSouce.getUserViewPofileContent(contentRequestHeader)
        }, onSuccess = {
            _userProfileContentRes = it
        })
    }

    override fun getUserViewProfileMock(castcleId: String) {
        getUserViewProfileSingleUseCase.execute(
            ViewProfileRequest(castcleId = castcleId)
        ).subscribeBy {
            _userProfileYouContentMock.onNext(it)
        }.addToDisposables()
    }

    override fun getFeedResponse(contentType: ContentType) {
        val mockData = Gson().fromJson(
            JSONObject(readJSONFromAsset() ?: "").toString(),
            FeedResponse::class.java
        ).payload.toContentFeedUiModel()

        if (contentType == ContentType.CONTENT) {
            _userProfileContentMock.onNext(mockData.feedContentUiModel)
        } else {
            mockData.feedContentUiModel.filter {
                it.contentType == contentType.type
            }.let {
                _userProfileContentMock.onNext(it)
            }
        }
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
