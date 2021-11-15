package com.castcle.ui.signin.password

import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.databinding.*
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.*
import com.castcle.common_model.model.login.domain.*
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.signin.password.VerifyPassState.*
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

class PasswordFragment : BaseFragment<PasswordFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentPasswordBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private val authBundle: PasswordFragmentArgs by navArgs()

    private val emailBundle: AuthBundle
        get() = authBundle.emailBundle

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentPasswordBinding
        get() = { inflater, container, attachToRoot ->
            FragmentPasswordBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentPasswordBinding
        get() = viewBinding as FragmentPasswordBinding

    override fun viewModel(): PasswordFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(PasswordFragmentViewModel::class.java)

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
            with(itPassword) {
                onDrawableEndClickListener = {
                    itPassword.setTransformationTextPassword()
                }
                onTextChanged = {
                    handlePasswordChecked()
                    viewModel.input.password(it)
                }
                onEditorActionNext = {
                    itReTypePassword.setRequestFocus()
                }
            }
            with(itReTypePassword) {
                onDrawableEndClickListener = {
                    itReTypePassword.setTransformationTextPassword()
                }
                onTextChanged = {
                    handleReTypeChecked()
                    viewModel.input.retypePassword(it)
                }
                onEditorActionListener = { actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btNext.callOnClick()
                        true
                    }
                    false
                }
            }
            btNext.subscribeOnClick {
                onNavigateToDisplayNameFragment()
            }.addToDisposables()
        }
    }

    private fun onNavigateToDisplayNameFragment() {
        val emailEdit = emailBundle.toEmailAuthBundle()
        if (emailEdit is RegisterBundle.RegisterWithEmail) {
            emailEdit.apply {
                password = binding.itPassword.primaryText
            }.run {
                onBoardNavigator.navigetToDisplayNameFragment(this)
            }
        }
    }

    private fun handlePasswordChecked() {
        with(binding) {
            if (itReTypePassword.primaryText.isPasswordPatten()) {
                itReTypePassword.onSetupStatusDrawableEnd()
            }
            itPassword.onSetupStatusDrawableEnd()
        }
    }

    private fun handleReTypeChecked() {
        with(binding) {
            if (itPassword.primaryText.isPasswordPatten()) {
                itPassword.onSetupStatusDrawableEnd()
            }
            itReTypePassword.onSetupStatusDrawableEnd()
        }
    }

    override fun bindViewModel() {

        with(viewModel) {
            passwordError.observe(this@PasswordFragment, {
                handlerPasswordError(it)
            })

            enableContinue.observe(this@PasswordFragment, {
                enableContinueButton(it)
            })

            verifyPassword.subscribe {
                binding.itPassword.onSetupStatusDrawableEnd()
            }.addToDisposables()

            verifyReTypePassword.subscribe {
                binding.itReTypePassword.onSetupStatusDrawableEnd()
            }.addToDisposables()
        }
    }

    private fun handlerPasswordError(verifyPassState: VerifyPassState) {
        when (verifyPassState) {
            PASSWORD_NOT_MATCH -> {
                showMessageError()
                with(binding) {
                    itReTypePassword.onSetupStatusFaildDrawableEnd()
                    itPassword.onSetupStatusFaildDrawableEnd()
                }
            }
            PASSWORD_LENGTH, RETYPE_PASSWORD_LENGTH -> {
            }
            PASSWORD_PATTRN -> {
                binding.itPassword.onSetupStatusFaildDrawableEnd()
                showMessageError()
            }
            RETYPE_PASSWORD_PATTRN -> {
                binding.itReTypePassword.onSetupStatusFaildDrawableEnd()
                showMessageError()
            }
            PASSWORD_PASS -> {
                binding.itPassword.onSetupStatusDrawableEnd()
            }
            RETYPE_PASSWORD_PASS -> {
                binding.itReTypePassword.onSetupStatusDrawableEnd()
            }
            PASSWORD_MATCH_PASS -> {
                with(binding) {
                    itReTypePassword.onSetupEndIconVerifyPass()
                    itPassword.onSetupEndIconVerifyPass()
                    tvSubTitle.gone()
                    tvErrorMessage.gone()
                }
            }
        }
    }

    private fun showMessageError() {
        with(binding) {
            tvSubTitle.gone()
            tvErrorMessage.visible()
        }
    }

    private fun clearStatusEndIcon() {
        binding.itPassword.onSetupStatusDrawableEnd()
    }

    private fun enableContinueButton(enable: Boolean) {
        with(binding) {
            btNext.isActivated = enable
            btNext.isEnabled = enable
        }
    }
}
