package com.castcle.ui.setting.notification.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.castcle.android.databinding.FragmentNotificationContentBinding
import com.castcle.common_model.model.empty.EmptyState.NOTIFICATION_EMPTY
import com.castcle.common_model.model.notification.NotificationPayloadModel
import com.castcle.extensions.displayError
import com.castcle.extensions.visibleOrGone
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.events.NotificationItemClick
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.setting.notification.NotificationFragmentViewModel
import com.castcle.ui.setting.notification.NotificationPagingAdapter
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.flow.collectLatest
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
//  Created by sklim on 1/9/2021 AD at 18:12.

class ProfileNotificationFragment : BaseFragment<NotificationFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentNotificationContentBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentNotificationContentBinding
        get() = { inflater, container, attachToRoot ->
            FragmentNotificationContentBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentNotificationContentBinding
        get() = viewBinding as FragmentNotificationContentBinding

    override fun viewModel(): NotificationFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(NotificationFragmentViewModel::class.java)

    override fun initViewModel() {
        viewModel.fetchNotification("profile")
    }

    private lateinit var adapterNotificationPaging: NotificationPagingAdapter

    override fun setupView() {
        with(binding) {
            rvNotiContent.adapter = NotificationPagingAdapter().also {
                adapterNotificationPaging = it
            }
        }

        lifecycleScope.launchWhenCreated {
            adapterNotificationPaging.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading
                val isError = loadStates.refresh is LoadState.Error
                val isNotItem = loadStates.refresh is LoadState.NotLoading &&
                    adapterNotificationPaging.itemCount == 0
                handleEmptyState(isNotItem)
                if (isError) {
                    handleEmptyState(true)
                }
            }
        }
    }

    private fun handleEmptyState(show: Boolean) {
        with(binding.empState) {
            visibleOrGone(show)
            bindUiState(NOTIFICATION_EMPTY)
        }
    }

    override fun bindViewModel() {
        with(viewModel) {
            launchOnLifecycleScope {
                notificationResponse.collectLatest {
                    adapterNotificationPaging.submitData(it)
                }
            }
        }
    }

    override fun bindViewEvents() {
        adapterNotificationPaging.itemClick.subscribe {
            when (it) {
                is NotificationItemClick.ReadNotificationClick -> {
                    handlerReadNotification(it.itemImage)
                }
            }
        }.addToDisposables()
    }

    private fun handlerReadNotification(itemImage: NotificationPayloadModel) {
        viewModel.putReadNotification(itemImage.id).subscribeBy(
            onComplete = {
                adapterNotificationPaging.updateStateRead(itemImage)
            }, onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

}