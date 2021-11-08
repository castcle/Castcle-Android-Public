package com.castcle.ui.signin.email

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.databinding.FragmentEmailBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.authen_android.data.model.AuthenticationInfo
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.AuthBundle
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel
import com.castcle.extensions.*
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

class EmailFragment : BaseFragment<EmailFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentEmailBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentEmailBinding
        get() = { inflater, container, attachToRoot ->
            FragmentEmailBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentEmailBinding
        get() = viewBinding as FragmentEmailBinding

    override fun viewModel(): EmailFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(EmailFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.gone()
            tvToolbarTitle.gone()
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

            btContinue.subscribeOnClick {
                val authBundle = AuthBundle.LoginAuthBundle.EmailAuthBundle(
                    authenticationMethod = AuthenticationInfo.Method.EMAIL,
                    email = binding.itEmail.primaryText
                )
                navigateToPasswordFragment(authBundle)
            }
        }
    }

    private fun checkEmailExsit(email: String) {
        viewModel.input.checkEmailExist(email)
        if (email.isBlank()) {
            showErrorMessage(false)
        }
    }

    private fun navigateToPasswordFragment(authBundle: AuthBundle) {
        onBoardNavigator.navigateToPassword(authBundle)
    }

    override fun bindViewModel() {
        with(viewModel) {
            showLoading
                .subscribe(::showLoadingInputText)
                .addToDisposables()

            responseCheckEmailExsit
                .subscribeBy(
                    onNext = {
                        resultCheckEmail(it)
                    }, onError = {
                        showErrorMessage(true)
                    }
                )
                .addToDisposables()

            error.subscribe {
                onErrorMessage(it)
            }.addToDisposables()
        }
    }

    private fun resultCheckEmail(emailVerifyUiModel: AuthVerifyBaseUiModel.EmailVerifyUiModel) {
        if (!emailVerifyUiModel.exist && emailVerifyUiModel.message.isNotBlank()) {
            enableContinueButton(true)
            binding.tvSubTitle.gone()
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
            requireActivity().hideSoftKeyboard()
        }
    }

    private fun showPassMessage() {
        with(binding) {
            tvErrorMessage.gone()
            tvSubTitle.gone()
            tvPassMessage.visible()
        }
    }

    private fun showErrorMessage(exist: Boolean) {
        with(binding) {
            tvErrorMessage.visibleOrGone(exist)
            tvSubTitle.visibleOrGone(!exist)
        }
    }

    private fun onErrorMessage(throwable: Throwable) {
        with(binding) {
            tvErrorMessage.visibleOrGone(true)
            tvErrorMessage.text = throwable.cause?.message ?: ""
            tvSubTitle.visibleOrGone(false)
        }
    }

    private fun statusInputText(pass: Boolean) {
        if (pass) {
            binding.itEmail.onSetupStatusVerifyEmailPass()
        }
    }
}
