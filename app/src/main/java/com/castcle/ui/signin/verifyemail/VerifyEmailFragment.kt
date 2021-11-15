package com.castcle.ui.signin.verifyemail

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.castcle.android.databinding.FragmentVerifyEmailBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardViewModel
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
//  Created by sklim on 1/9/2021 AD at 18:12.

class VerifyEmailFragment : BaseFragment<VerifyEmailFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentVerifyEmailBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private val authBundle: VerifyEmailFragmentArgs by navArgs()

    private val profileBundle: ProfileBundle
        get() = authBundle.profileBundle

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentVerifyEmailBinding
        get() = { inflater, container, attachToRoot ->
            FragmentVerifyEmailBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentVerifyEmailBinding
        get() = viewBinding as FragmentVerifyEmailBinding

    override fun viewModel(): VerifyEmailFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(VerifyEmailFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.gone()
            tvToolbarTitle.gone()
            ivToolbarLogoButton.gone()
        }
    }

    override fun bindViewEvents() {
        binding.btGoToFeed.subscribeOnClick {
            naivgateToFeed()
        }.addToDisposables()

        binding.tvSentVerifyEmail.subscribeOnClick {
            navigateToResentVerifyEmail()
        }

        binding.btProfileSetting.subscribeOnClick {
            navigateToProfileSetting()
        }
    }

    private fun naivgateToFeed() {
        onBoardNavigator.nvaigateToFeedFragment()
        activityViewModel.onRefreshProfile()
    }

    private fun navigateToProfileSetting() {
        onBoardNavigator.navigateToAboutYouFragment(profileBundle)
    }

    private fun navigateToResentVerifyEmail() {
        val bundle = profileBundle as ProfileBundle.ProfileWithEmail
        val input = Input(
            contentData = bundle.email,
            type = DeepLinkTarget.VERIFY_EMAIL
        )
        makeDeepLinkUrl(requireContext(), input).run(::navigateByDeepLink)
    }

    private fun navigateByDeepLink(uri: Uri) {
        onBoardNavigator.navigateByDeepLink(uri, false)
    }

    override fun bindViewModel() = Unit
}