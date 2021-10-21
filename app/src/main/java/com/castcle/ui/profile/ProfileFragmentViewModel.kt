package com.castcle.ui.profile

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.common_model.model.userprofile.ImagesRequest
import com.castcle.common_model.model.userprofile.User
import com.castcle.data.staticmodel.ContentType
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

    abstract val userProfileContentRes: Flow<PagingData<ContentUiModel>>

    abstract val userViewProfileContentRes: Flow<PagingData<ContentUiModel>>

    abstract fun fetachUserProfileContent(contentRequestHeader: FeedRequestHeader)

    abstract fun fetachUserViewProfileContent(contentRequestHeader: FeedRequestHeader)

    abstract fun getFeedResponse(contentType: ContentType)

    abstract fun getUserViewProfile(castcleId: String)

    abstract val userProfileRes: Observable<User>

    abstract val userProfileYouRes: Observable<User>

    abstract val userUpLoadRes: LiveData<User>

    abstract val userProfileContentMock: Observable<List<ContentUiModel>>

    abstract val feedContentPage: Flow<PagingData<ContentUiModel>>

    abstract val showLoading: Observable<Boolean>

    abstract val onError: Observable<Throwable>

    abstract fun putToFollowUser(castcleId: String): Completable

    abstract fun getViewPage(castcleId: String)

    abstract val viewPageRes: Observable<User>

    abstract fun upLoadAvatar(imageUri: ImagesRequest): Completable

    abstract fun checkAvatarUploading(): Observable<Boolean>

    abstract fun likedContent(
        contentId: String,
        likedStatus: Boolean
    ): Completable
}
