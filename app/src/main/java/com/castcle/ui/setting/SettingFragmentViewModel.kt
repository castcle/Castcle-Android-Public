package com.castcle.ui.setting

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.feed.PaginationModel
import com.castcle.common_model.model.notification.NotificationUiModel
import com.castcle.common_model.model.setting.*
import com.castcle.common_model.model.signin.ViewPageUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.ui.base.BaseViewModel
import com.castcle.usecase.feed.GetNotificationCountCompleteUseCase
import com.castcle.usecase.feed.UpdateNotificationCountCompleteUseCase
import com.castcle.usecase.notification.GetBadgesNotificationSingleUseCase
import com.castcle.usecase.setting.*
import com.castcle.usecase.userprofile.GetCachedUserProfileSingleUseCase
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
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

abstract class SettingFragmentViewModel : BaseViewModel() {

    abstract val userProfile: LiveData<User>

    abstract val userPage: LiveData<List<PageUiModel>>

    abstract val notificationBadgesCounts: LiveData<NotificationUiModel>

    abstract fun onLogOut(activity: Activity): Completable

    abstract fun fetchCachedUserProfile(): Completable

    abstract fun fetchUserPage()

    abstract fun fetchNextUserPage()

    abstract fun getCacheNotificationCount()
}

class SettingFragmentViewModelImpl @Inject constructor(
    private val logoutCompletableUseCase: LogoutCompletableUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
    private val getUerPageSingleUseCase: GetUerPageSingleUseCase,
    private val getCachePageDataCompletableUseCase: GetCachePageDataCompletableUseCase,
    private val getBadgesNotificationSingleUseCase: GetBadgesNotificationSingleUseCase,
    private val updateNotificationCountCompleteUseCase: UpdateNotificationCountCompleteUseCase,
    private val getNotificationCountCompleteUseCase: GetNotificationCountCompleteUseCase
) : SettingFragmentViewModel() {

    private val _showLoading = BehaviorSubject.create<Boolean>()

    private val _error = PublishSubject.create<Throwable>()

    private var _pagination = MutableLiveData<PaginationModel>()

    private var _notificationBadgesCounts = MutableLiveData<NotificationUiModel>()
    override val notificationBadgesCounts: LiveData<NotificationUiModel>
        get() = _notificationBadgesCounts

    private val _userProfile = MutableLiveData<User>()
    override val userProfile: LiveData<User>
        get() = _userProfile

    override fun onLogOut(activity: Activity): Completable {
        return logoutCompletableUseCase.execute(activity)
    }

    private var _userPage = MutableLiveData<List<PageUiModel>>()
    override val userPage: LiveData<List<PageUiModel>>
        get() = _userPage

    override fun fetchCachedUserProfile(): Completable =
        cachedUserProfileSingleUseCase
            .execute(Unit)
            .firstOrError()
            .zipWith(
                getCachePageDataCompletableUseCase.execute(Unit)
            ).doOnSubscribe { _showLoading.onNext(true) }
            .doFinally { _showLoading.onNext(false) }
            .doOnSuccess { (user, page) ->
                onBindCacheUserPage(user, page)
            }.doOnError(_error::onNext)
            .ignoreElement()


    private fun onBindCacheUserPage(userOptional: Optional<User>, page: PageHeaderUiModel) {
        if (userOptional.isPresent) {
            setUserProfileData(userOptional)

            val userPage = userOptional.get().toPageHeaderUiModel()
            page.pageUiItem.toMutableList().apply {
                add(0, userPage.pageUiItem.first())
            }.let {
                setUserPage(it)
            }
        }
    }

    override fun fetchUserPage() {
        val defaultPaginate = if (_pagination.value != null) {
            _pagination.value
        } else {
            PaginationModel(
                limit = PAGE_SIZE_DEFAULT,
                next = PAGE_NUMBER_DEFAULT
            )
        }
        defaultPaginate?.let { it ->
            getUerPageSingleUseCase.execute(it)
                .zipWith(
                    getBadgesNotificationSingleUseCase.execute(Unit)
                ).subscribeBy(
                    onSuccess = { (pageUiMode, notificationBadge) ->
                        onBindPageModel(pageUiMode)
                        onBindNotification(notificationBadge)
                    }, onError = {
                        _error.onNext(it)
                    }
                ).addToDisposables()
        }
    }

    private fun onBindNotification(notificationBadge: NotificationUiModel) {
        val notificationCount = (notificationBadge as NotificationUiModel.NotificationBadgeModel)
        onUpdateNotificationCount(notificationCount.badges)
        _notificationBadgesCounts.value = notificationBadge
    }

    private fun onUpdateNotificationCount(badges: String) {
        updateNotificationCountCompleteUseCase.execute(badges).subscribe().addToDisposables()
    }

    override fun fetchNextUserPage() {
        if (_showLoading.value == true) return
        fetchUserPage()
    }

    private fun onBindPageModel(item: ViewPageUiModel) {
        val userPage = _userProfile.value?.toPageHeaderUiModel()
        item.pageHeaderUiModel.pageUiItem.toMutableList().apply {
            add(0, userPage?.pageUiItem?.first() ?: PageUiModel())
        }.let {
            setUserPage(it)
        }
        setPaginationData(item.paginationModel)
    }

    private fun setUserProfileData(user: Optional<User>) {
        _userProfile.value = user.get()
    }

    private fun setPaginationData(pagination: PaginationModel) {
        _pagination.value = pagination
    }

    private fun setUserPage(pageitem: List<PageUiModel>) {
        _userPage.value = pageitem
    }

    override fun getCacheNotificationCount() {
        getNotificationCountCompleteUseCase.execute(Unit).subscribeBy(
            onSuccess = {
                onBindNotification(
                    NotificationUiModel.NotificationBadgeModel(
                        badges = it
                    )
                )
            }, onError = {
                _error.onNext(it)
            }
        ).addToDisposables()
    }
}

private const val PAGE_NUMBER_DEFAULT = 1
private const val PAGE_SIZE_DEFAULT = 25
