package com.castcle.ui.setting.changepassword.createnewpassword

import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.*
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.*
import com.castcle.common_model.model.setting.domain.ChangePassRequest
import com.castcle.common_model.model.setting.VerificationUiModel
import com.castcle.data.error.userReadableMessage
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.signin.password.VerifyPassState
import com.castcle.ui.signin.password.VerifyPassState.*
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

class CreatePasswordFragment : BaseFragment<CreatePasswordFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentCreatePasswordBinding>,
    ToolbarBindingInflater<ToolbarCastcleLanguageBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val createPasswordFragmentArgs: CreatePasswordFragmentArgs by navArgs()

    private val verificationUiModel: VerificationUiModel
        get() = createPasswordFragmentArgs.veificationUiModel

    private val onForGotPassword: Boolean
        get() = verificationUiModel.forgotPassword

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleLanguageBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleLanguageBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleLanguageBinding
        get() = toolbarViewBinding as ToolbarCastcleLanguageBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreatePasswordBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCreatePasswordBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCreatePasswordBinding
        get() = viewBinding as FragmentCreatePasswordBinding

    override fun viewModel(): CreatePasswordFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(CreatePasswordFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.gone()
            tvToolbarTitle.text = localizedResources.getString(
                R.string.setting_create_password_tool_bar
            )
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        with(binding) {
            itPassword.onDrawableEndClickListener = {
                itPassword.setTransformationTextPassword()
            }
            itPassword.onTextChanged = {
                handleCheckLawOne()
                handlePasswordChecged()
                viewModel.input.password(it)
            }
            itReTypePassword.onDrawableEndClickListener = {
                itReTypePassword.setTransformationTextPassword()
            }
            itReTypePassword.onTextChanged = {
                handleCheckLawOne()
                handleReTypeChecged()
                viewModel.input.retypePassword(it)
            }
            btNext.subscribeOnClick {
                handlerSubmitChangePassword()
            }
        }
    }

    private fun handlerSubmitChangePassword() {
        val changePasswordRequest = ChangePassRequest(
            refCode = verificationUiModel.refCode,
            newPassword = binding.itPassword.primaryText
        )
        viewModel.submitChangePassword(changePasswordRequest).subscribeBy(
            onComplete = {
                onBindNavigate()
            },
            onError = {
                handleErrorState(it)
            }
        ).addToDisposables()
    }

    private fun onBindNavigate() {
        navigateToCompleteFragment()
    }

    private fun navigateToCompleteFragment() {
        requireActivity().hideSoftKeyboard()
        onBoardNavigator.navigateToCompleteFragment(onForGotPass = onForGotPassword)
    }

    private fun handleCheckLawOne() {
        with(binding) {
            if (itPassword.primaryText.length >= LIMIT_PASS &&
                itReTypePassword.primaryText.length >= LIMIT_PASS
            ) {
                tvLaw1.setTextColor(requireContext().getColorResource(R.color.white))
            } else {
                tvLaw1.setTextColor(requireContext().getColorResource(R.color.gray_light))
            }
        }
    }

    private fun handlePasswordChecged() {
        with(binding) {
            if (itReTypePassword.primaryText.isPasswordPatten()) {
                itReTypePassword.onSetupStatusDrawableEnd()
            }
            itPassword.onSetupStatusDrawableEnd()
        }
    }

    private fun handleReTypeChecged() {
        with(binding) {
            if (itPassword.primaryText.isPasswordPatten()) {
                itPassword.onSetupStatusDrawableEnd()
            }
            itReTypePassword.onSetupStatusDrawableEnd()
        }
    }

    override fun bindViewModel() {

        with(viewModel) {
            passwordError.observe(this@CreatePasswordFragment, {
                handlerPasswordError(it)
            })

            enableContinue.observe(this@CreatePasswordFragment, {
                enableContinueButton(it)
            })

            verifyPassword.subscribe {
                binding.itPassword.onSetupStatusDrawableEnd()
            }.addToDisposables()

            verifyReTypePassword.subscribe {
                binding.itReTypePassword.onSetupStatusDrawableEnd()
            }.addToDisposables()

            showLoading.subscribe {
                handleShowLoading(it)
            }.addToDisposables()

            onError.subscribe {
                handleErrorState(it)
            }.addToDisposables()
        }
    }

    private fun handleErrorState(throwable: Throwable) {
        with(binding) {
            tvErrorMessage.visible()
            tvErrorMessage.text = throwable.userReadableMessage(requireContext())
            tvSubTitle.invisible()
        }
    }

    private fun handleShowLoading(showLoading: Boolean) {
        binding.progressBar.visibleOrGone(showLoading)
        binding.btNext.visibleOrGone(!showLoading)
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
                handleCheckLawTwo()
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
                    handleCheckLawTwo(true)
                    itReTypePassword.onSetupEndIconVerifyPass()
                    itPassword.onSetupEndIconVerifyPass()
                    tvSubTitle.visible()
                    tvErrorMessage.gone()
                }
            }
        }
    }

    private fun handleCheckLawTwo(pass: Boolean = false) {
        with(binding) {
            if (pass) {
                tvLaw2.setTextColor(requireContext().getColorResource(R.color.white))
            } else {
                tvLaw2.setTextColor(requireContext().getColorResource(R.color.light))
            }
        }
    }

    private fun showMessageError() {
        with(binding) {
            tvSubTitle.invisible()
            tvErrorMessage.visible()
        }
    }

    private fun enableContinueButton(enable: Boolean) {
        with(binding) {
            btNext.isActivated = enable
            btNext.isEnabled = enable
        }
    }
}

private const val LIMIT_PASS = 8
