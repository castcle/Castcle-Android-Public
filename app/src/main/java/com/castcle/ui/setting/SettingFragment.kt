package com.castcle.ui.setting

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentSettingBinding
import com.castcle.android.databinding.ToolbarCastcleCommonBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.notification.NotificationUiModel
import com.castcle.common_model.model.setting.PageUiModel
import com.castcle.common_model.model.setting.SettingMenuType
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.data.staticmodel.StaticSeetingMenu
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.networking.api.user.PROFILE_TYPE_ME
import com.castcle.ui.base.*
import com.castcle.ui.common.dialog.*
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
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
            .subscribeBy(
                onComplete = {
                    viewModel.fetchUserPage()
                }, onError = {
                    viewModel.fetchUserPage()
                    displayError(it)
                }
            ).addToDisposables()
    }

    override fun setupView() {
        setupToolBar()
        bindingSettingContent()
    }

    private fun bindingSettingContent() {
        with(binding) {
            val settingUiModel = StaticSeetingMenu.staticMenuSetting
            with(layoutNotification) {
                clNotiication.subscribeOnClick {
                    navigateToNotificationFragment()
                }
            }
            mtMenuSetting.bindingMenu(settingUiModel)
            mtMenuSetting.clickStateMenu.subscribe {
                handleMenuClick(it)
            }.addToDisposables()
        }
    }

    private fun onBindNotification(badgesCount: String) {
        with(binding) {
            with(layoutNotification) {
                tvNotiCount.text =
                    context?.getString(R.string.setting_noti_count)?.format(badgesCount)
                tvCount.visibleOrGone(badgesCount.isNotBlank())
                tvCount.text = badgesCount
            }
        }
    }

    private fun navigateToNotificationFragment() {
        onBoardNavigator.navigateToNotificationFragment()
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
                    openWebView(STATIC_LINK_PRIVACY_POLICY)
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
            ivToolbarProfileButton.invisible()
            tvToolbarTitle.text = localizedResources.getString(R.string.setting_title)
            ivToolbarLogoButton.setImageDrawable(
                requireContext().getDrawableRes(R.drawable.ic_arrow_left)
            )
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        binding.btContinue.subscribeOnClick {
            viewModel.onLogOut(requireActivity()).subscribeBy(
                onError = {
                    handleError(it)
                }
            ).addToDisposables()
        }

        binding.ptPageContentList.clickPage.subscribe {
            handlerPageClick(it)
        }.addToDisposables()

        binding.ptPageContentList.onNextPage.subscribe {
            viewModel.fetchNextUserPage()
        }.addToDisposables()

        binding.tvjoinUs.subscribeOnClick {
            openWebView(STATIC_LINK_JOIN_US)
        }.addToDisposables()

        binding.tvManifesto.subscribeOnClick {
            openWebView(STATIC_LINK_MENIFESTO)
        }.addToDisposables()

        binding.tvWhitepaper.subscribeOnClick {
            openWebView(STATIC_LINK_WHITEPAPER)
        }.addToDisposables()
    }

    private fun openWebView(url: String) {
        (context as Activity).openUri(url)
    }

    private fun handleError(it: Throwable) {
        displayError(it)
    }

    private fun handlerPageClick(templateClicks: TemplateClicks) {
        when (templateClicks) {
            is TemplateClicks.AvatarClick -> {
                handleNavigateAvatarClick()
            }
            is TemplateClicks.AddPageClick -> {
                navigateToGreetingPageFragment()
            }
            is TemplateClicks.LikeClick -> {
            }
            is TemplateClicks.MenuClick -> {
            }
            is TemplateClicks.PageClick -> {
                handlerPageItemClick(templateClicks)
            }
        }
    }

    private fun handlerPageItemClick(templateClicks: TemplateClicks.PageClick) {
        val castcleId = templateClicks.pageUiModel.castcleId
        val pageType = templateClicks.pageUiModel.pageType
        navigateToProfile(castcleId, pageType)
    }

    private fun handleNavigateAvatarClick() {
        navigateToProfile(type = PROFILE_TYPE_ME)
    }

    private fun navigateToGreetingPageFragment() {
        onBoardNavigator.navigateToGreetingPageFragment()
    }

    private fun navigateToProfile(castcleId: String = "", type: String) {
        onBoardNavigator.navigateToProfileFragment(castcleId, type)
    }

    override fun bindViewModel() {
        viewModel.userProfile.observe(this, {
            onBindUserData(it)
        })

        viewModel.userPage.observe(this, {
            onBindPageContent(it)
        })

        viewModel.notificationBadgesCounts.observe(viewLifecycleOwner, {
            if (it is NotificationUiModel.NotificationBadgeModel) {
                onBindNotification(it.badges)
            }
        })
    }

    private fun onBindPageContent(list: List<PageUiModel>) {
        list.toMutableList().apply {
            this.add(PageUiModel(addPage = true))
        }.run(binding.ptPageContentList::bindPage)
    }

    private fun onBindUserData(user: User) {
        with(binding) {
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
