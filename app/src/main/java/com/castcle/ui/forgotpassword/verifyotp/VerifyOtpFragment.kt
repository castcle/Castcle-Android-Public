package com.castcle.ui.forgotpassword.verifyotp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentForGotPassBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.engagement.domain.*
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

class VerifyOtpFragment : BaseFragment<VerifyOtpFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentForGotPassBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val verifyOtpArgs: VerifyOtpFragmentArgs by navArgs()

    private val profileBundle: ProfileBundle
        get() = verifyOtpArgs.profileBundle

    private var verifyRefCode: String? = null

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentForGotPassBinding
        get() = { inflater, container, attachToRoot ->
            FragmentForGotPassBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentForGotPassBinding
        get() = viewBinding as FragmentForGotPassBinding

    override fun viewModel(): VerifyOtpFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(VerifyOtpFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
        enableResentNewOtp()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.invisible()
            tvToolbarTitle.gone()
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        startCountDown()

        with(binding) {
            with(ptOtpInput) {
                onPinEnteredListener = {
                    onVerifyOtpInput(it)
                }
                onPinChangedListener = {
                    onSetPinError()
                }
            }

            tvWarningAccount.subscribeOnClick {
                resentRequestOtp()
            }.addToDisposables()
        }
    }

    private fun resentRequestOtp() {
        startCountDown()
        val otpBundle = profileBundle as ProfileBundle.ProfileOtp
        viewModel.input.requestOtpWithEmail(otpBundle.email)
    }

    private fun getVerifyOtpRequest(otpCode: String): VerifyOtpRequest {
        val otpBundle = profileBundle as ProfileBundle.ProfileOtp

        return VerifyOtpRequest(
            channel = CHANNEL_EMAIL,
            otp = otpCode,
            refCode = verifyRefCode ?: otpBundle.refCode,
            payload = OtpPayloadRequest(
                email = otpBundle.email
            )
        )
    }

    private fun onVerifyOtpInput(otpCode: String) {
        viewModel.verifyOtpRequest(getVerifyOtpRequest(otpCode)).subscribeBy(
            onSuccess = {
                onNavigateToCreateNewPassword(it)
            },
            onError = {
                onSetPinError(true)
            }
        ).addToDisposables()
    }

    private fun onSetPinError(onError: Boolean = false) {
        with(binding) {
            tvErrorMessage.visibleOrGone(onError)
            ptOtpInput.setError(onError)
        }
    }

    override fun bindViewModel() {
        viewModel.countDownTimerLiveData.observe(viewLifecycleOwner, {
            onBindTimer(it)
        })

        viewModel.onVerifyOtpResponse.subscribe {
            onBindRefCode(it)
        }.addToDisposables()
    }

    private fun onBindRefCode(response: VerificationUiModel?) {
        if (response?.refCode?.isNotBlank() == true) {
            verifyRefCode = response.refCode
        }
    }

    private fun onNavigateToCreateNewPassword(verificationUiModel: VerificationUiModel) {
        verificationUiModel.forgotPassword = true
        onBoardNavigator.navigateToCreatePasswordFragment(verificationUiModel)
    }

    private fun startCountDown() {
        viewModel.countDownTimerLiveData.start()
    }

    private fun onBindTimer(timeCount: Long) {
        with(binding) {
            tvCountDown.text = localizedResources.getString(
                R.string.verify_otp_warning_re_sent
            ).format(timeCount.toInt())
            if (timeCount.toInt() == 0) {
                enableResentNewOtp(true)
            }
        }
    }

    private fun enableResentNewOtp(enableClick: Boolean = false) {
        with(binding) {
            tvWarningAccount.isEnabled = enableClick
        }
    }
}
