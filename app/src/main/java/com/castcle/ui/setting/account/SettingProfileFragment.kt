package com.castcle.ui.setting.account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentProfileSettingBinding
import com.castcle.android.databinding.ToolbarCastcleLanguageBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.common_model.model.setting.SettingMenuType.*
import com.castcle.common_model.model.setting.SettingMenuUiModel
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.data.storage.AppPreferences
import com.castcle.extensions.gone
import com.castcle.extensions.invisible
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
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
//  Created by sklim on 30/9/2021 AD at 12:30.

class SettingProfileFragment : BaseFragment<SettingProfileViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentProfileSettingBinding>,
    ToolbarBindingInflater<ToolbarCastcleLanguageBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    @Inject lateinit var appPreferences: AppPreferences

    override val toolbarBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleLanguageBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleLanguageBinding.inflate(inflater, container, attachToRoot)
        }

    override val toolbarBinding: ToolbarCastcleLanguageBinding
        get() = toolbarViewBinding as ToolbarCastcleLanguageBinding

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileSettingBinding
        get() = { inflater, container, attachToRoot ->
            FragmentProfileSettingBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentProfileSettingBinding
        get() = viewBinding as FragmentProfileSettingBinding

    override fun viewModel(): SettingProfileViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(SettingProfileViewModel::class.java)

    override fun initViewModel() {
        viewModel.fetchUserProfile().subscribe().addToDisposables()
    }

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.invisible()
            tvToolbarTitle.text = localizedResources.getString(R.string.setting_account_title)
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        viewModel.getProfileSettingMenu().subscribe {
            onBindSettingMenu(it)
        }.addToDisposables()
    }

    override fun bindViewModel() {
        with(binding.mtMenuSetting) {
            clickStateMenu.subscribe {
                handlerSettingMenuClick(it)
            }.addToDisposables()
        }
    }

    private fun onBindSettingMenu(listMenu: List<SettingMenuUiModel>) {
        with(binding.mtMenuSetting) {
            bindingMenu(listMenu)
        }
    }

    private fun handlerSettingMenuClick(itemClick: TemplateClicks?) {
        if (itemClick is TemplateClicks.MenuClick) {
            when (itemClick.menuType) {
                SETTING_PASSWORD -> {
                    navigateToChangePasswordFragment()
                }
                SETTING_EMAIL -> {

                }
                SETTING_DELECT_ACCOUNT -> {
                    navigateToDeleteAccount()
                }
                else -> {

                }
            }
        }
    }

    private fun navigateToDeleteAccount() {
        val userProfile = viewModel.userCachePage.value
        val profileBundle = ProfileBundle.ProfileDelete(
            castcleId = userProfile?.castcleId ?: "",
            avatar = userProfile?.avatar ?: "",
            profileType = ProfileType.PROFILE_TYPE_ME.type,
            displayName = userProfile?.displayName
        )
        onBoardNavigator.navigateToProfileDeletePageFragment(profileBundle)
    }

    private fun navigateToChangePasswordFragment() {
        onBoardNavigator.navigateToChangePasswordFragment()
    }
}
