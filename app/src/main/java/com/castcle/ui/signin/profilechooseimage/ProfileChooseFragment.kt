package com.castcle.ui.signin.profilechooseimage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCreateProfileChooseBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.ProfileBundle
import com.castcle.extensions.gone
import com.castcle.extensions.visible
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
//  Created by sklim on 1/9/2021 AD at 18:12.

class ProfileChooseFragment : BaseFragment<ProfileChooseFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentCreateProfileChooseBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private val authBundle: ProfileChooseFragmentArgs by navArgs()

    private val emailBundle: ProfileBundle
        get() = authBundle.profileBundle

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateProfileChooseBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCreateProfileChooseBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCreateProfileChooseBinding
        get() = viewBinding as FragmentCreateProfileChooseBinding

    override fun viewModel(): ProfileChooseFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ProfileChooseFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.run {
                visible()
                text = context.getString(R.string.tool_bar_skip)
                subscribeOnClick {
                    navigateToVerifyEmail()
                }.addToDisposables()
            }
            tvToolbarTitle.gone()
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    private fun navigateToVerifyEmail() {
    }

    override fun bindViewEvents() {

    }

    override fun bindViewModel() = Unit
}