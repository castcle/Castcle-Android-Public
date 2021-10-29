package com.castcle.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.common_model.model.userprofile.*
import com.castcle.data.repository.UserProfileRepository
import com.castcle.usecase.feed.LikeCommentCompletableUseCase
import com.castcle.usecase.feed.LikeContentCompletableUseCase
import com.castcle.usecase.userprofile.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
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
    private val userProfileDataSouce: UserProfileRepository,
    private val putToFollowUserCompletableUseCase: PutToFollowUserCompletableUseCase,
    private val getViewPageSingleUseCase: GetViewPageFlowableUseCase,
    private val uploadProfileAvatarCompletableUseCase: UploadProfileAvatarCompletableUseCase,
    private val checkAvatarUpLoadingFlowableCase: CheckAvatarUpLoadingFlowableCase,
    private val likedCommentCompletableUseCase: LikeCommentCompletableUseCase,
    private val likeContentCompletableUseCase: LikeContentCompletableUseCase
) : ProfileFragmentViewModel() {

    private lateinit var _userProfileContentRes: Flow<PagingData<ContentUiModel>>

    private lateinit var _userViewProfileContentRes: Flow<PagingData<ContentUiModel>>

    private var _userProfileData = MutableLiveData<User>()

    override val userUpLoadRes: LiveData<User>
        get() = _userProfileData

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
            }.toObservable()
    }

    private fun setUserProfileData(userData: User) {
        _userProfileData.value = userData
    }

    override val userProfileContentRes: Flow<PagingData<ContentUiModel>>
        get() = _userProfileContentRes

    override val userViewProfileContentRes: Flow<PagingData<ContentUiModel>>
        get() = _userViewProfileContentRes

    override fun fetachUserProfileContent(contentRequestHeader: FeedRequestHeader) {
        launchPagingAsync({
            when (contentRequestHeader.viewType) {
                ProfileType.PROFILE_TYPE_ME.type -> {
                    userProfileDataSouce.getUserProfileContent(contentRequestHeader)
                }
                ProfileType.PROFILE_TYPE_PEOPLE.type -> {
                    userProfileDataSouce.getUserViewProfileContent(contentRequestHeader)
                }
                ProfileType.PROFILE_TYPE_PAGE.type -> {
                    userProfileDataSouce.getViewPageProfileContent(contentRequestHeader)
                }
                else -> userProfileDataSouce.getUserViewProfileContent(contentRequestHeader)
            }
        }, onSuccess = {
            _userProfileContentRes = it
        })
    }

    override fun fetachUserViewProfileContent(contentRequestHeader: FeedRequestHeader) {
        launchPagingAsync({
            userProfileDataSouce.getUserViewProfileContent(contentRequestHeader)
        }, onSuccess = {
            _userProfileContentRes = it
        })
    }

    override fun getUserViewProfile(castcleId: String) {
        getUserViewProfileSingleUseCase.execute(
            ViewProfileRequest(castcleId = castcleId)
        ).firstOrError()
            .subscribeBy(
                onSuccess = {
                    _userProfileYouContentMock.onNext(it)
                },
                onError = {
                    _error.onNext(it)
                }
            ).addToDisposables()
    }

    override fun putToFollowUser(castcleId: String): Completable {
        val castcleIdMe = _userProfileData.value?.castcleId ?: ""
        return putToFollowUserCompletableUseCase.execute(
            FollowRequest(
                castcleIdFollower = castcleId,
                castcleId = castcleIdMe
            )
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.doFinally {
            _showLoading.onNext(false)
        }
    }

    private var _viewPageRes = BehaviorSubject.create<User>()
    override val viewPageRes: Observable<User>
        get() = _viewPageRes

    override fun getViewPage(castcleId: String) {
        getViewPageSingleUseCase.execute(castcleId)
            .doOnSubscribe {
                _showLoading.onNext(true)
            }.doFinally {
                _showLoading.onNext(false)
            }.firstOrError()
            .subscribeBy(
                onSuccess = {
                    _viewPageRes.onNext(it)
                },
                onError = {
                    _error.onNext(it)
                }
            ).addToDisposables()
    }

    override fun upLoadAvatar(imageUri: ImagesRequest): Completable {
        return uploadProfileAvatarCompletableUseCase.execute(imageUri.toStringImageRequest())
    }

    override fun checkAvatarUploading(): Observable<Boolean> {
        return checkAvatarUpLoadingFlowableCase.execute(Unit)
            .map { (status, userResponse) ->
                userResponse.takeIf {
                    it.isNotBlank()
                }.let {
                    checkUserResponse(userResponse)
                }
                status
            }.doOnError {
                _error.onNext(it)
            }.toObservable()
    }

    private fun checkUserResponse(userResponse: String) {
        if (userResponse.isNotBlank()) {
            val userData = userResponse.toUserModel()
            setUserProfileData(userData)
        }
    }

    override fun likedContent(
        contentId: String,
        likedStatus: Boolean
    ): Completable {
        return likeContentCompletableUseCase.execute(
            LikeContentCompletableUseCase.Input(
                contentId = contentId,
                likeStatus = likedStatus
            )
        )
    }
}
