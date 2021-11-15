package com.castcle.ui.signin.createdisplayname

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCreateProfileBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.login.domain.RegisterBundle
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel.DisplayNameVerifyUiModel
import com.castcle.data.error.RegisterErrorError
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

class CreateDisplayNameFragment : BaseFragment<CreateDisplayNameFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentCreateProfileBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val authBundle: CreateDisplayNameFragmentArgs by navArgs()

    private val resgisterBundle: RegisterBundle
        get() = authBundle.registerBundle

    private val isCreatePage: Boolean
        get() = authBundle.isCreatePage

    private val isRegisterPass = MutableLiveData<Boolean>()

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateProfileBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCreateProfileBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCreateProfileBinding
        get() = viewBinding as FragmentCreateProfileBinding

    override fun viewModel(): CreateDisplayNameFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(CreateDisplayNameFragmentViewModel::class.java)

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
            with(itDisplatName) {
                onTextChanged = {
                    viewModel.input.displayName(it)
                }
                onEditorActionNext = {
                    itCastcleId.setRequestFocus()
                }
            }
            with(itCastcleId) {
                onTextChanged = {
                    viewModel.input.checkCastcleId(it)
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
                if (isRegisterPass.value == true) {
                    navigateToChooseProfile()
                } else {
                    handleActionNext()
                }
            }.addToDisposables()
        }
    }

    private fun handleActionNext() {
        if (isCreatePage) {
            onCreatePage()
        } else {
            onRegister()
        }
    }

    private fun onCreatePage() {
        with(binding) {
            viewModel.input.createPage(
                itDisplatName.primaryText,
                itCastcleId.primaryText
            ).subscribeBy(
                onSuccess = {
                    isRegisterPass.value = true
                    navigateToChooseProfile(true)
                }, onError = {
                    handlerError(it)
                }
            ).addToDisposables()
        }
    }

    private fun onRegister() {
        with(binding) {
            viewModel.input.authRegisterWithEmail(
                resgisterBundle as RegisterBundle.RegisterWithEmail,
                itDisplatName.primaryText,
                itCastcleId.primaryText
            ).subscribeBy(
                onComplete = {
                    isRegisterPass.value = true
                    navigateToChooseProfile()
                }, onError = {
                    handlerError(it)
                }
            ).addToDisposables()
        }
    }

    private fun handlerError(error: Throwable) {
        if (error is RegisterErrorError && error.hasAuthenticationTokenExprierd()) {
            binding.itCastcleId.setError(
                error = error.readableMessage,
                isShowErrorWithBackground = true
            )
        }
        if (error is RegisterErrorError && error.hasAuthenticationCastcleIdInSystem()) {
            binding.itCastcleId.setError(
                error = error.readableMessage,
                isShowErrorWithBackground = true
            )
        }
        if (error is RegisterErrorError && error.hasAuthenticationUserInSystem()) {
            binding.itCastcleId.setError(
                error = error.readableMessage,
                isShowErrorWithBackground = true
            )
        }
        if (error is RegisterErrorError && error.hasAuthenticationEmailNotFound()) {
            binding.itCastcleId.setError(
                error = error.readableMessage,
                isShowErrorWithBackground = true
            )
        }
    }

    private fun navigateToChooseProfile(isCreatePage: Boolean = false) {
        with(binding) {
            isRegisterPass.value = true
            val registerBundle = resgisterBundle as RegisterBundle.RegisterWithEmail
            onBoardNavigator.naivgetToProfileChooseImageFragment(
                ProfileBundle.ProfileWithEmail(
                    email = registerBundle.email,
                    displayName = itDisplatName.primaryText,
                    castcleId = itCastcleId.primaryText,
                ), isCreatePage
            )
        }
    }

    override fun bindViewModel() {
        viewModel.verifyProfileState
            .subscribe(::handlerUiState)
            .addToDisposables()

        viewModel.showLoading
            .subscribe {
                showLoading(it)
            }.addToDisposables()

        viewModel.error
            .subscribe {
                displayError(it)
            }.addToDisposables()

        viewModel.responseCastcleIdSuggest
            .subscribe(::fieldInCastcleSuggest)
            .addToDisposables()

        viewModel.responseCastcleIdExsit.subscribe().addToDisposables()
    }

    private fun showLoading(isLoad: Boolean) {
        with(binding) {
            btNext.visibleOrInvisible(!isLoad)
            pbLoading.visibleOrGone(isLoad)
        }
    }

    private fun fieldInCastcleSuggest(displayNameVerifyUiModel: DisplayNameVerifyUiModel) {
        binding.itCastcleId.primaryText = displayNameVerifyUiModel.castcleIdSuggestions
    }

    private fun handlerUiState(verifyProfileState: VerifyProfileState) {
        when (verifyProfileState) {
            VerifyProfileState.CASTCLE_ID_ERROR -> {
                endableButtomNext(false)
                binding.itCastcleId.setError(
                    localizedResources.getString(R.string.register_warning_message)
                )
            }
            VerifyProfileState.CASTCLE_ID_PASS -> {
                endableButtomNext(true)
            }
            else -> {
                endableButtomNext(false)
            }
        }
    }

    private fun endableButtomNext(enable: Boolean) {
        with(binding.btNext) {
            isActivated = enable
            isEnabled = enable
        }
    }
}
