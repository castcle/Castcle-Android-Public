package com.castcle.ui.feed

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.ui.base.BaseViewCoroutinesModel
import com.castcle.ui.util.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
//  Created by sklim on 19/8/2021 AD at 11:33.

abstract class FeedFragmentViewModel : BaseViewCoroutinesModel() {

    abstract val isGuestMode: Boolean

    abstract val feedContentMock: LiveData<List<ContentFeedUiModel>>

    abstract val showLoading: Observable<Boolean>

    abstract val onError: Observable<Throwable>

    abstract val userProfile: LiveData<User>

    abstract val onUpdateContentLike: Observable<Unit>

    abstract val feedContentPage: Flow<PagingData<ContentFeedUiModel>>

    abstract fun fetchUserProfile(): Completable

    abstract fun getAllFeedContent(feedRequest: MutableStateFlow<FeedRequestHeader>)

    abstract fun getAllFeedGustsContent(feedRequest: MutableStateFlow<FeedRequestHeader>)

    abstract val input: Input

    abstract fun setFetchFeedContent(feedRequest: FeedRequestHeader)

    abstract fun getTopTrends()

    abstract val trendsResponse: LiveData<List<SearchUiModel>>

    abstract fun checkCastPostWithImageStatus(): Observable<Boolean>

    abstract val castPostResponse: SingleLiveEvent<ContentFeedUiModel>

    abstract fun checkContentIsMe(
        castcleId: String,
        onProfileMe: () -> Unit,
        onPageMe: () -> Unit,
        non: () -> Unit
    )

    abstract val castcleId: String

    abstract fun reportContent(contentId: String): Completable

    abstract fun blockUser(userId: String): Completable

    abstract fun unblockUser(userId: String): Completable

    interface Input {
        fun recastContent(contentUiModel: ContentFeedUiModel): Completable

        fun updateLikeContent(likeContentRequest: LikeContentRequest)

        fun setDefaultFeedRequestHeader()

        fun deleteContentFeed(contentId: String): Completable
    }
}
