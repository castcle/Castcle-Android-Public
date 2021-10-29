package com.castcle.ui.setting.notification

import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.*
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.data.staticmodel.TabContentStatic
import com.castcle.extensions.getDrawableRes
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

class NotificationFragment : BaseFragment<NotificationFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentNotificationBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var notificationPageAdapter: NotificationPageAdapter

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentNotificationBinding
        get() = { inflater, container, attachToRoot ->
            FragmentNotificationBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentNotificationBinding
        get() = viewBinding as FragmentNotificationBinding

    override fun viewModel(): NotificationFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(NotificationFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
        with(binding.vpNotificationContent) {
            adapter = NotificationPageAdapter(this@NotificationFragment).also {
                notificationPageAdapter = it
            }
        }
        TabLayoutMediator(
            binding.tlTabMenu,
            binding.vpNotificationContent
        ) { Tab, position ->
            Tab.customView = onBindTabCustom(
                position,
                Tab,
                TabContentStatic.tabNotification[position].tabNameRes
            )
        }.attach()
    }

    private fun onBindTabCustom(
        position: Int,
        tab: TabLayout.Tab,
        tabNameRes: Int
    ): View {
        val inflater = LayoutInflater.from(context)
        val headerTab = ItemTabNotificationBinding.inflate(inflater)
        val backGroundTab = when (position) {
            0 -> requireContext().getDrawableRes(R.drawable.bg_tab_menu_start)
            1 -> requireContext().getDrawableRes(R.drawable.bg_tab_menu_center)
            2 -> requireContext().getDrawableRes(R.drawable.bg_tab_menu_end)
            else -> requireContext().getDrawableRes(R.drawable.bg_tab_menu_end)
        }
        with(headerTab) {
            clNotiication.background = backGroundTab
            tvTabTitle.text = localizedResources.getString(tabNameRes)
        }
        return headerTab.root
    }

    private fun setupToolBar() {
        with(binding.inToolbar) {
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() = Unit

    override fun bindViewModel() = Unit
}