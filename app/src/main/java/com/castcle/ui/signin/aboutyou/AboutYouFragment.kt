package com.castcle.ui.signin.aboutyou

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentAboutYouBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.ProfileBundle
import com.castcle.common_model.model.setting.CreatePageRequest
import com.castcle.common_model.model.userprofile.*
import com.castcle.extensions.*
import com.castcle.networking.api.user.PROFILE_TYPE_PAGE
import com.castcle.ui.base.*
import com.castcle.ui.common.dialog.DatePickerDialogFragment
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.signin.aboutyou.addlink.LINK_REQUEST_CODE
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

class AboutYouFragment : BaseFragment<AboutYouFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentAboutYouBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private var linksRequest = MutableLiveData<LinksRequestUiModel>()

    private lateinit var linkAdapter: LinksAdapter

    private val profileBundle: AboutYouFragmentArgs by navArgs()

    private val profile: ProfileBundle
        get() = profileBundle.profileBundle

    private val isCreatePage: Boolean
        get() = profileBundle.isCreatePage

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentAboutYouBinding
        get() = { inflater, container, attachToRoot ->
            FragmentAboutYouBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentAboutYouBinding
        get() = viewBinding as FragmentAboutYouBinding

    override fun viewModel(): AboutYouFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(AboutYouFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
        getNavigationResult<LinksRequestUiModel>(
            onBoardNavigator,
            R.id.aboutYouFragment,
            LINK_REQUEST_CODE,
            onResult = {
                linksRequest.value = it
            })
        if (isCreatePage) {
            binding.groupIsCreatePage.gone()
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.apply {
                visible()
                text = context.getString(R.string.tool_bar_skip)
            }.run {
                subscribeOnClick {
                    handlerSkip()
                }
            }
            tvToolbarTitle.gone()
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        with(binding) {
            itBirthday.onDrawableEndClickListener = {
                DatePickerDialogFragment { _, year, month, dayOfMonth ->
                    itBirthday.primaryText = "$dayOfMonth ${month.getMonthName()} $year"
                }.show(childFragmentManager, "date picker")
            }

            btDone.subscribeOnClick {
                onHandlerUpdate(
                    onPage = {
                        onRequestUpdateProfile()
                    },
                    onProfile = {
                        onUpdatePage()
                    }
                )
            }

            tvAddLinks.subscribeOnClick {
                navigateToAddLinksFragment()
            }.addToDisposables()

            itOverView.onTextChanged = {
                onActiveButton(it.isNotBlank())
            }
            linksRequest.observe(this@AboutYouFragment, {
                onBindLinksItem(it)
            })
        }
    }

    private fun onActiveButton(isActivate: Boolean) {
        binding.btDone.isActivated = isActivate
    }

    private fun onHandlerUpdate(onProfile: () -> Unit, onPage: () -> Unit) {
        if (isCreatePage) {
            onProfile.invoke()
        } else {
            onPage.invoke()
        }
    }

    private fun onUpdatePage() {
        val profileBundle = profile as ProfileBundle.ProfileWithEmail
        val requestUpdate = CreatePageRequest(
            avatar = profileBundle.imageAvatar,
            overview = binding.itOverView.primaryText,
            castcleId = profileBundle.castcleId,
            links = LinksRequest(
                facebook = linksRequest.value?.facebook ?: "",
                twitter = linksRequest.value?.twitter ?: "",
                youtube = linksRequest.value?.youtube ?: "",
                website = linksRequest.value?.website ?: "",
                medium = linksRequest.value?.medium ?: "",
            )
        )

        viewModel.requestUpdatePage(requestUpdate)
            .subscribeBy(
                onComplete = {
                    onNavigateProfile(profileBundle.castcleId)
                }, onError = {
                    displayError(it)
                }
            ).addToDisposables()
    }

    private fun onNavigateProfile(castcleId: String) {
        onBoardNavigator.navigateToProfileFragment(castcleId, PROFILE_TYPE_PAGE)
    }

    private fun onRequestUpdateProfile() {
        with(binding) {
            val requestUpdate = UserUpdateRequest(
                dob = itBirthday.primaryText,
                overview = itOverView.primaryText,
                links = LinksRequest(
                    facebook = linksRequest.value?.facebook ?: "",
                    twitter = linksRequest.value?.twitter ?: "",
                    youtube = linksRequest.value?.youtube ?: "",
                    website = linksRequest.value?.website ?: "",
                    medium = linksRequest.value?.medium ?: "",
                )
            )

            viewModel.requestUpdateProfile(requestUpdate)
                .subscribeBy(
                    onComplete = {
                        onNavigateToFeedFragment()
                    }
                ).addToDisposables()
        }
    }

    private fun navigateToAddLinksFragment() {
        onBoardNavigator.navigateToAddLinksFragment(
            linksRequest.value ?: LinksRequestUiModel()
        )
    }

    private fun onBindLinksItem(linksRequest: LinksRequestUiModel) {
        linksRequest.facebook.isNotEmpty().run {
            binding.itLinkFacebook.visibleOrGone(this)
            if (this) {
                binding.itLinkFacebook.primaryText = linksRequest.facebook
            }
        }
        linksRequest.twitter.isNotEmpty().run {
            binding.itLinkTwitter.visibleOrGone(this)
            if (this) {
                binding.itLinkTwitter.primaryText = linksRequest.twitter
            }
        }
        linksRequest.youtube.isNotEmpty().run {
            binding.itLinkYouTube.visibleOrGone(this)
            if (this) {
                binding.itLinkYouTube.primaryText = linksRequest.youtube
            }
        }
        linksRequest.medium.isNotEmpty().run {
            binding.itLinkMedium.visibleOrGone(this)
            if (this) {
                binding.itLinkMedium.primaryText = linksRequest.medium
            }
        }
        linksRequest.website.isNotEmpty().run {
            binding.itLinkWebSite.visibleOrGone(this)
            if (this) {
                binding.itLinkWebSite.primaryText = linksRequest.website
            }
        }

        with(binding) {
            if (itLinkFacebook.isVisible && itLinkWebSite.isVisible && itLinkMedium.isVisible
                && itLinkYouTube.isVisible && itLinkTwitter.isVisible
            ) {
                tvAddLinks.gone()
            } else {
                tvAddLinks.visible()
            }
        }
    }

    private fun handlerSkip() {
        if (isCreatePage) {
            onNavigateToProfile()
        } else {
            onNavigateToFeedFragment()
        }
    }

    private fun onNavigateToProfile() {
        val profileBundle = profile as ProfileBundle.ProfileWithEmail
        onBoardNavigator.navigateToProfileFragment(
            profileBundle.castcleId, PROFILE_TYPE_PAGE
        )
    }

    private fun onNavigateToFeedFragment() {
        onBoardNavigator.nvaigateToFeedFragment()
        activityViewModel.onRefreshProfile()
    }

    override fun bindViewModel() = Unit
}
