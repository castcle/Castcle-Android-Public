package com.castcle.ui.setting.notification

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.castcle.common_model.model.notification.NotificationPayloadModel
import com.castcle.common_model.model.notification.NotificationUiModel
import com.castcle.networking.api.notification.NotificationRepository
import com.castcle.ui.base.BaseViewCoroutinesModel
import com.castcle.usecase.notification.*
import io.reactivex.Completable
import io.reactivex.Observable
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
//  Created by sklim on 1/9/2021 AD at 18:14.

abstract class NotificationFragmentViewModel : BaseViewCoroutinesModel() {

    abstract fun fetchNotification(source: String = "")

    abstract val notificationResponse: Flow<PagingData<NotificationPayloadModel>>

    abstract val badgesNotificationResponse: Observable<NotificationUiModel>

    abstract fun putReadNotification(notificationId: String): Completable

    abstract fun putReadAllNotification(): Completable
}

class NotificationFragmentViewModelImpl @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val getBadgesNotificationSingleUseCase: GetBadgesNotificationSingleUseCase,
    private val readNotificationCompletableUseCase: ReadNotificationCompletableUseCase,
    private val readAllNotificationCompletableUseCase: ReadAllNotificationCompletableUseCase
) : NotificationFragmentViewModel() {

    private lateinit var _notificationResponse: Flow<PagingData<NotificationPayloadModel>>
    override val notificationResponse: Flow<PagingData<NotificationPayloadModel>>
        get() = _notificationResponse

    override fun fetchNotification(source: String) {
        launchPagingAsync({
            notificationRepository.getNotification(source).cachedIn(viewModelScope)
        }, {
            _notificationResponse = it
        })
    }

    override val badgesNotificationResponse: Observable<NotificationUiModel>
        get() = getBadgesNotificationSingleUseCase.execute(Unit)
            .toObservable()

    override fun putReadNotification(notificationId: String): Completable {
        return readNotificationCompletableUseCase.execute(notificationId)
    }

    override fun putReadAllNotification(): Completable {
        return readAllNotificationCompletableUseCase.execute(Unit)
    }
}
