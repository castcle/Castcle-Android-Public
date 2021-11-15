package com.castcle.ui.setting.changepassword.complete

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCompletedBinding
import com.castcle.common.lib.extension.subscribeOnClick
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

class CompleteFragment : BaseFragment<CompleteViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentCompletedBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val completeBundle: CompleteFragmentArgs by navArgs()

    private val onDeletePage: Boolean
        get() = completeBundle.onDeletePage

    private val onDeleteAccount: Boolean
        get() = completeBundle.onDeleteAccount

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCompletedBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCompletedBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCompletedBinding
        get() = viewBinding as FragmentCompletedBinding

    override fun viewModel(): CompleteViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(CompleteViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        with(binding) {
            when {
                onDeletePage -> {
                    tvChangePasswrdTitle.text = localizedResources.getString(
                        R.string.complete_message_delete_page
                    )
                    btNext.text = localizedResources.getString(
                        R.string.complete_message_delete_button_setting
                    )
                }
                onDeleteAccount -> {
                    tvChangePasswrdTitle.text = localizedResources.getString(
                        R.string.complete_message_delete_account
                    )
                    btNext.text = localizedResources.getString(
                        R.string.complete_message_delete_button
                    )
                }
                else -> {
                    tvChangePasswrdTitle.text = localizedResources.getString(
                        R.string.setting_complete_change_title
                    )
                }
            }
        }
    }

    override fun bindViewEvents() {
        binding.btNext.isActivated = true
        binding.btNext.subscribeOnClick {
            handlerOnCompleteNavigate()
        }
    }

    private fun handlerOnCompleteNavigate() {
        when {
            onDeleteAccount -> {
                viewModel.onLogout(requireActivity())
                    .subscribe()
                    .addToDisposables()
            }
            else -> {
                onBoardNavigator.navigateToSettingFragment()
            }
        }
    }

    override fun bindViewModel() = Unit

}
