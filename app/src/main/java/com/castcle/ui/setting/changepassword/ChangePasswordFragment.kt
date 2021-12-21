package com.castcle.ui.setting.changepassword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentChangePasswordBinding
import com.castcle.android.databinding.ToolbarCastcleLanguageBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.setting.VerificationUiModel
import com.castcle.data.error.userReadableMessage
import com.castcle.extensions.*
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
//  Created by sklim on 1/9/2021 AD at 18:12.

class ChangePasswordFragment : BaseFragment<ChangePasswordViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentChangePasswordBinding>,
    ToolbarBindingInflater<ToolbarCastcleLanguageBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleLanguageBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleLanguageBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleLanguageBinding
        get() = toolbarViewBinding as ToolbarCastcleLanguageBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentChangePasswordBinding
        get() = { inflater, container, attachToRoot ->
            FragmentChangePasswordBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentChangePasswordBinding
        get() = viewBinding as FragmentChangePasswordBinding

    override fun viewModel(): ChangePasswordViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ChangePasswordViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.invisible()
            tvToolbarTitle.text = localizedResources.getString(
                R.string.setting_change_password_tool_bar
            )
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        binding.itPassword.setIconWithTransformation()
        binding.itPassword.onTextChanged = {
            handleInputPassword(it)
        }

        binding.btNext.subscribeOnClick {
            binding.tvSubTitleError.gone()
            binding.tvSubTitle.visible()
            viewModel.input.verificationPassword(binding.itPassword.primaryText)
        }
    }

    private fun handleInputPassword(pass: String) {
        if (pass.isNotBlank()) {
            activatedButtonPass(true)
        } else {
            activatedButtonPass(false)
        }
    }

    override fun bindViewModel() {
        viewModel.onVerificationResponse.observe(this, {
            navigateToChangePassword(it)
        })

        viewModel.onError.subscribe {
            handleErrorState(it)
        }.addToDisposables()

        viewModel.showLoading.subscribe {
            handleShowLoading(it)
        }.addToDisposables()
    }

    private fun handleShowLoading(showLoading: Boolean) {
        binding.progressBar.visibleOrGone(showLoading)
    }

    private fun handleErrorState(throwable: Throwable) {
        with(binding) {
            tvSubTitle.gone()
            tvSubTitleError.visible()
            tvSubTitleError.text = throwable.userReadableMessage(requireContext())
        }
    }

    private fun activatedButtonPass(active: Boolean) {
        with(binding) {
            btNext.run {
                isActivated = active
                isEnabled = active
            }
        }
    }

    private fun navigateToChangePassword(verificationUiModel: VerificationUiModel) {
        requireActivity().hideSoftKeyboard()
        onBoardNavigator.navigateToCreatePasswordFragment(verificationUiModel)
    }
}
