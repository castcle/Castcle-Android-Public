package com.castcle.ui.signin.createaccount.accountcomplete

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.castcle.android.databinding.FragmentAccountCompletedBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.RegisterBundle
import com.castcle.extensions.loadCircleImage
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
//  Created by sklim on 1/10/2021 AD at 13:19.

class AccountCompleteFragment : BaseFragment<AccountCompleteViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentAccountCompletedBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val completeBundle: AccountCompleteFragmentArgs by navArgs()

    private val registerBundle: RegisterBundle
        get() = completeBundle.registerBundle

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentAccountCompletedBinding
        get() = { inflater, container, attachToRoot ->
            FragmentAccountCompletedBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentAccountCompletedBinding
        get() = viewBinding as FragmentAccountCompletedBinding

    override fun viewModel(): AccountCompleteViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(AccountCompleteViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        val registerWithSocial = registerBundle as RegisterBundle.RegisterWithSocial
        with(binding) {
            ivAvatar.loadCircleImage(registerWithSocial.userAvatar)
            tvUserName.text = registerWithSocial.userName
            tvCastcleId.text = registerWithSocial.userId
        }
    }

    override fun bindViewEvents() {
        binding.btGoToFeed.subscribeOnClick {

        }.addToDisposables()

        binding.btProfileSetting.isActivated = true
        binding.btProfileSetting.subscribeOnClick {

        }
    }

    override fun bindViewModel() = Unit

}
