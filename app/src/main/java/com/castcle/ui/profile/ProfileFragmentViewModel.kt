package com.castcle.ui.profile

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.domain.ImagesRequest
import com.castcle.ui.base.BaseViewCoroutinesModel
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

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
//  Created by sklim on 10/9/2021 AD at 11:38.

abstract class ProfileFragmentViewModel : BaseViewCoroutinesModel() {

    abstract val userProfileContentRes: Flow<PagingData<ContentFeedUiModel>>

    abstract val userViewProfileContentRes: Flow<PagingData<ContentFeedUiModel>>

    abstract fun fetachUserProfileContent(contentRequestHeader: FeedRequestHeader)

    abstract fun fetachUserViewProfileContent(contentRequestHeader: FeedRequestHeader)

    abstract fun getUserViewProfile(castcleId: String)

    abstract fun fetchUserProfile()

    abstract val userProfileYouRes: Observable<User>

    abstract val userProfileData: LiveData<User>

    abstract val userProfileContentMock: Observable<List<ContentUiModel>>

    abstract val feedContentPage: Flow<PagingData<ContentUiModel>>

    abstract val showLoading: Observable<Boolean>

    abstract val showLoadingProfile: Observable<Boolean>

    abstract val onError: Observable<Throwable>

    abstract fun putToFollowUser(castcleId: String): Completable

    abstract fun getViewPage(castcleId: String)

    abstract val viewPageRes: Observable<User>

    abstract fun upLoadAvatar(imageUri: ImagesRequest): Completable

    abstract fun checkAvatarUploading(): Observable<Boolean>

    abstract val castcleId: String

    abstract val isGuestMode: Boolean

    abstract fun likedContent(
        likeContentRequest: LikeContentRequest
    ): Completable

    abstract fun recastContent(castcleId: String, contentUiModel: ContentFeedUiModel): Completable

    abstract fun reportUser(userId: String): Completable

    abstract fun blockUser(userId: String): Completable

    abstract fun unblockUser(userId: String): Completable
}
