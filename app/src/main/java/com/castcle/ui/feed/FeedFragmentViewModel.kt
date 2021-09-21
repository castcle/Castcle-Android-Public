package com.castcle.ui.feed

import androidx.lifecycle.LiveData
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.ui.base.BaseViewCoroutinesModel
import io.reactivex.Completable
import io.reactivex.Observable

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

    abstract val feedContentMock: LiveData<List<ContentUiModel>>

    abstract fun getFeedResponseMock()

    abstract val userProfile: LiveData<User>

    abstract val onUpdateContentLike: Observable<ContentUiModel>

    abstract fun getMockFeed()

    abstract fun fetchUserProfile(): Completable

    abstract val input: Input

    interface Input {
        fun updateLikeContent(contentUiModel: ContentUiModel)
    }
}
