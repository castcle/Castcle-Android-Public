package com.castcle.ui.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentSettingBinding
import com.castcle.android.databinding.ToolbarCastcleCommonBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.setting.SettingMenuType
import com.castcle.common_model.model.setting.toPageHeaderUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.data.staticmodel.StaticSeetingMenu
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.splashscreen.SplashScreenActivity
import io.reactivex.rxkotlin.subscribeBy
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

class SettingFragment : BaseFragment<SettingFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentSettingBinding>,
    ToolbarBindingInflater<ToolbarCastcleCommonBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleCommonBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleCommonBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleCommonBinding
        get() = toolbarViewBinding as ToolbarCastcleCommonBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingBinding
        get() = { inflater, container, attachToRoot ->
            FragmentSettingBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentSettingBinding
        get() = viewBinding as FragmentSettingBinding

    override fun viewModel(): SettingFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(SettingFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        viewModel.fetchCachedUserProfile()
            .subscribe()
            .addToDisposables()
    }

    override fun setupView() {
        setupToolBar()
        bindingSettingContent()
    }

    private fun bindingSettingContent() {
        with(binding) {
            val settingUiModel = StaticSeetingMenu.staticMenuSetting
            with(layoutNotification) {
                tvNotiCount.text =
                    context?.getString(R.string.setting_noti_count)?.format("3")
                tvCount.text = "3"
                clNotiication.subscribeOnClick {

                }
            }
            mtMenuSetting.bindingMenu(settingUiModel)
            mtMenuSetting.clickStateMenu.subscribe {
                handleMenuClick(it)
            }.addToDisposables()
        }
    }

    private fun handleMenuClick(menuClick: TemplateClicks?) {
        if (menuClick is TemplateClicks.MenuClick) {
            when (menuClick.menuType) {
                SettingMenuType.LANGUAGE -> {
                    navigateToLanguageFragment()
                }
                SettingMenuType.PROFILE -> {
                    navigateToProfileSettingFragment()
                }
                SettingMenuType.PRIVACY -> {

                }
                else -> {

                }
            }
        }
    }

    private fun navigateToProfileSettingFragment() {
        onBoardNavigator.navigateToSettingProfileFragment()
    }

    private fun navigateToLanguageFragment() {
        onBoardNavigator.navigateToLanguageFragment()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.setImageDrawable(
                context?.getDrawableRes(
                    R.drawable.ic_hamburger_selected
                )
            )
            tvToolbarTitle.text = localizedResources.getString(R.string.setting_title)
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        binding.btContinue.subscribeOnClick {
            viewModel.onLogOut().subscribeBy(
                onComplete = {
                    logoutToSplashScreen()
                }
            ).addToDisposables()
        }

        binding.ptPageContentList.clickPage.subscribe {
            handleNavigateAvatarClick()
        }.addToDisposables()
    }

    private fun handleNavigateAvatarClick() {
        val deepLink = makeDeepLinkUrl(
            requireContext(), Input(
                type = DeepLinkTarget.USER_PROFILE_ME,
                contentData = ""
            )
        ).toString()
        navigateByDeepLink(deepLink)
    }

    private fun navigateByDeepLink(url: String) {
        onBoardNavigator.navigateByDeepLink(url.toUri())
    }

    private fun logoutToSplashScreen() {
        SplashScreenActivity.start(requireContext())
        (context as OnBoardActivity).finish()
        findNavController().navigateUp()
    }

    override fun bindViewModel() {
        viewModel.userProfile.observe(this, {
            onBindUserData(it)
        })
    }

    private fun onBindUserData(user: User) {
        with(binding) {
            val pageHeaderList = user.toPageHeaderUiModel()
            ptPageContentList.bindPage(pageHeaderList)

            with(layoutNotificationWarning) {
                clWarningNoti.visibleOrGone(!user.verified)
                if (clWarningNoti.isVisible) {
                    clWarningNoti.subscribeOnClick {
                        val email = viewModel.userProfile.value?.email ?: ""
                        navigateToResentVerifyFragment(email)
                    }
                }
            }
        }
    }

    private fun navigateToResentVerifyFragment(email: String) {
        onBoardNavigator.navigateToResentVerifyEmail(email)
    }
}
