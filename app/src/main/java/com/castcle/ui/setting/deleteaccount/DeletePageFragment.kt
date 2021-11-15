package com.castcle.ui.setting.deleteaccount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentDeletePageBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.common_model.model.userprofile.DeletePagePayload
import com.castcle.common_model.model.userprofile.DeleteUserPayload
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

class DeletePageFragment : BaseFragment<DeletePageFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentDeletePageBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val profileBundle: DeletePageFragmentArgs by navArgs()

    private val profile: ProfileBundle
        get() = profileBundle.profileBundle

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentDeletePageBinding
        get() = { inflater, container, attachToRoot ->
            FragmentDeletePageBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentDeletePageBinding
        get() = viewBinding as FragmentDeletePageBinding

    override fun viewModel(): DeletePageFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(DeletePageFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
        onBindDeletePage()
    }

    private fun onBindDeletePage() {
        val profileBundle = profile as ProfileBundle.ProfileDelete
        var messageTitle: CharSequence = ""
        var profileType = ""
        with(binding) {
            when (profileBundle.profileType) {
                ProfileType.PROFILE_TYPE_PAGE.type -> {
                    profileType = DEFAULT_TYPE_PAGE
                    messageTitle = localizedResources.getText(
                        R.string.delete_page_fragment_description_page
                    )
                }
                ProfileType.PROFILE_TYPE_ME.type -> {
                    profileType = DEFAULT_TYPE_ME
                    messageTitle = localizedResources.getText(
                        R.string.delete_page_fragment_description
                    )
                    tvDescription.text = localizedResources.getText(
                        R.string.delete_page_fragment_description_page
                    )
                }
            }
            tvWelcome.text = messageTitle
            ivAvatarProfile.loadCircleImage(profileBundle.avatar)
            tvProfileName.text = profileBundle.displayName
            tvProfileType.text = profileType
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.gone()
            setToolbarTitle(localizedResources.getString(R.string.delete_page_fragment_title))
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    private fun setToolbarTitle(message: CharSequence) {
        with(toolbarBinding) {
            tvToolbarTitle.run {
                setTextColor(requireContext().getColorResource(R.color.white))
                text = message
            }
        }
    }

    override fun bindViewEvents() {
        binding.btDelete.subscribeOnClick {
            onHandlerDeletePageState()
        }.addToDisposables()

        binding.btNext.subscribeOnClick {
            onDeletePage()
        }.addToDisposables()
    }

    private fun onHandlerDeletePageState() {
        var messageTitle: CharSequence = ""
        var messageDescription: CharSequence = ""
        var messageToolbar: CharSequence = ""

        with(binding) {
            onHandlerDeleteRequest(
                onDeletePage = {
                    messageTitle = localizedResources.getText(
                        R.string.delete_page_fragment_title_on_delete_page
                    )
                    messageDescription = localizedResources.getText(
                        R.string.delete_page_fragment_description_on_delete_page
                    )
                    messageToolbar = localizedResources.getText(
                        R.string.delete_page_fragment_title_confirm_page
                    )
                },
                onDeleteUser = {
                    messageTitle = localizedResources.getText(
                        R.string.delete_page_fragment_title_on_delete
                    )
                    messageDescription = localizedResources.getText(
                        R.string.delete_page_fragment_description_on_delete
                    )
                    messageToolbar = localizedResources.getText(
                        R.string.delete_page_fragment_title_confirm
                    )
                }
            )
            tvWelcome.text = messageTitle
            tvDescription.text = messageDescription
            setToolbarTitle(messageToolbar)
            btDelete.gone()
            itPassword.run {
                visible()
                onTextChanged = {
                    it.isNotBlank().run {
                        btNext.isActivated = this
                        btNext.isEnabled = this
                    }
                }
            }
            btDelete.text = localizedResources.getText(R.string.delete_page_fragment_continue)
            onActiveButtonContinue()
        }
    }

    private fun onHandlerDeleteRequest(onDeleteUser: () -> Unit, onDeletePage: () -> Unit) {
        val profileBundle = profile as ProfileBundle.ProfileDelete
        when (profileBundle.profileType) {
            ProfileType.PROFILE_TYPE_PAGE.type -> {
                onDeleteUser.invoke()
            }
            ProfileType.PROFILE_TYPE_ME.type -> {
                onDeletePage.invoke()
            }
        }
    }

    private fun onActiveButtonContinue(state: Boolean = false) {
        with(binding) {
            btDelete.visibleOrGone(state)
            btNext.visibleOrGone(!state)
        }
    }

    private fun onDeletePage() {
        val profileBundle = profile as ProfileBundle.ProfileDelete
        if (profileBundle.profileType == ProfileType.PROFILE_TYPE_ME.type) {
            handlerOnDeleteAccount(profileBundle)
        } else {
            handlerOnDeletePage(profileBundle)
        }
    }

    private fun handlerOnDeleteAccount(profileBundle: ProfileBundle.ProfileDelete) {
        viewModel.onDeleteAccount(
            DeleteUserPayload(
                castcleId = profileBundle.castcleId,
                channel = DEFAULT_CHANNEL,
                payload = DeletePagePayload(
                    password = binding.itPassword.primaryText
                )
            )
        ).subscribeBy(
            onComplete = {
                onNavigateCompleteFragment(ProfileType.PROFILE_TYPE_ME.type)
            }, onError = {
                onDisplayError(it)
            }
        ).addToDisposables()
    }

    private fun onDisplayError(it: Throwable) {
        with(binding.itPassword) {
            setError(it.cause?.message)
        }
    }

    private fun handlerOnDeletePage(profileBundle: ProfileBundle.ProfileDelete) {
        viewModel.onDeletePage(
            DeleteUserPayload(
                castcleId = profileBundle.castcleId,
                channel = DEFAULT_CHANNEL,
                payload = DeletePagePayload(
                    password = binding.itPassword.primaryText
                )
            )
        ).subscribeBy(
            onComplete = {
                onNavigateCompleteFragment(ProfileType.PROFILE_TYPE_PAGE.type)
            }, onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun onNavigateCompleteFragment(profileType: String) {
        when (profileType) {
            ProfileType.PROFILE_TYPE_PAGE.type -> {
                onBoardNavigator.navigateToCompleteFragment(onDeletePage = true)
            }
            ProfileType.PROFILE_TYPE_ME.type -> {
                onBoardNavigator.navigateToCompleteFragment(onAccountPage = true)
            }
        }
    }

    override fun bindViewModel() = Unit
}

private const val DEFAULT_CHANNEL = "email"
private const val DEFAULT_TYPE_PAGE = "Page"
private const val DEFAULT_TYPE_ME = "Account"
