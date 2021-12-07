package com.castcle.ui.forgotpassword.searchaccount

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentSearchAccountBinding
import com.castcle.android.databinding.ToolbarCastcleProfileBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.setting.VerificationUiModel
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
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

class SearchAccountFragment : BaseFragment<SearchAccountFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentSearchAccountBinding>,
    ToolbarBindingInflater<ToolbarCastcleProfileBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleProfileBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleProfileBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleProfileBinding
        get() = toolbarViewBinding as ToolbarCastcleProfileBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchAccountBinding
        get() = { inflater, container, attachToRoot ->
            FragmentSearchAccountBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentSearchAccountBinding
        get() = viewBinding as FragmentSearchAccountBinding

    override fun viewModel(): SearchAccountFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(SearchAccountFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.invisible()
            tvToolbarTitle.text = localizedResources.getString(R.string.search_account_title)
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        with(binding) {
            itEmail.onTextChanged = {
                checkEmailExsit(it)
                itEmail.clearDrawableEnd()
            }

            itEmail.onEditorActionListener = { actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    requireActivity().hideSoftKeyboard()
                    btContinue.callOnClick()
                    true
                } else {
                    false
                }
            }

            btContinue.subscribeOnClick {
                onRequestOtp()
            }
        }
    }

    private fun onRequestOtp() {
        viewModel.input.requestOtpWithEmail(
            binding.itEmail.primaryText
        ).subscribeBy(
            onNext = {
                navigateToVerifyOtpFragment(it)
            },
            onError = {
                onErrorMessage(it)
            }
        ).addToDisposables()
    }

    private fun checkEmailExsit(email: String) {
        if (email.isNotBlank()) {
            enableContinueButton(true)
        }
        if (email.isBlank()) {
            enableContinueButton(false)
            showErrorMessage(false)
        }
    }

    private fun navigateToVerifyOtpFragment(verificationUiModel: VerificationUiModel) {
        val otpBundle = ProfileBundle.ProfileOtp(
            email = binding.itEmail.primaryText,
            refCode = verificationUiModel.refCode,
            expiresTime = verificationUiModel.expiresTime
        )
        onBoardNavigator.navigateToVerifyOtpFragment(otpBundle)
    }

    override fun bindViewModel() {
        with(viewModel) {
            showLoading
                .subscribe(::showLoadingInputText)
                .addToDisposables()

            error.subscribe {
                onErrorMessage(it)
            }.addToDisposables()
        }
    }

    private fun showLoadingInputText(showLoad: Boolean) {
        binding.itEmail.showLoading(showLoad)
    }

    private fun enableContinueButton(enable: Boolean) {
        with(binding) {
            showPassMessage()
            statusInputText(enable)
            btContinue.isEnabled = enable
            btContinue.isActivated = enable
        }
    }

    private fun showPassMessage() {
        with(binding) {
            tvErrorMessage.gone()
            tvSubTitle.gone()
        }
    }

    private fun showErrorMessage(exist: Boolean) {
        with(binding) {
            tvErrorMessage.text = localizedResources.getString(R.string.email_error_message)
            tvErrorMessage.visibleOrGone(exist)
            tvSubTitle.visibleOrGone(false)
        }
    }

    private fun onErrorMessage(throwable: Throwable) {
        with(binding) {
            tvErrorMessage.visibleOrGone(true)
            tvErrorMessage.text = throwable.cause?.message ?: ""
            tvSubTitle.visibleOrGone(false)
            tvPassMessage.gone()
        }
    }

    private fun statusInputText(pass: Boolean) {
        if (pass) {
            binding.itEmail.onSetupStatusVerifyEmailPass()
        }
    }
}
