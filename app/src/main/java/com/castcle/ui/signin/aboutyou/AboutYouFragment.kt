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
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.common_model.model.setting.domain.CreatePageRequest
import com.castcle.common_model.model.userprofile.*
import com.castcle.common_model.model.userprofile.domain.LinksRequest
import com.castcle.common_model.model.userprofile.domain.UserUpdateRequest
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
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

    @Inject lateinit var localizedResources: LocalizedResources

    private var linksRequest = MutableLiveData<LinksRequestUiModel>()

    private lateinit var linkAdapter: LinksAdapter

    private val profileBundle: AboutYouFragmentArgs by navArgs()

    private var profileType = ProfileType.NON

    private val profile: ProfileBundle
        get() = profileBundle.profileBundle

    private val isCreatePage: Boolean
        get() = profileBundle.isCreatePage

    private var castcleId: String = ""

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

        when (profile) {
            is ProfileBundle.ProfileEdit -> {
                profileType = ProfileType.PROFILE_TYPE_ME
                castcleId = (profile as ProfileBundle.ProfileEdit).castcleId
                onBindEditProfile()
            }
            is ProfileBundle.ViewProfile -> {
                profileType = ProfileType.PROFILE_TYPE_PEOPLE
                castcleId = (profile as ProfileBundle.ViewProfile).castcleId
                onBindViewProfile()
            }
            is ProfileBundle.PageEdit -> {
                profileType = ProfileType.PROFILE_TYPE_PAGE
                castcleId = (profile as ProfileBundle.PageEdit).castcleId
                onBindEditPage()
            }
            is ProfileBundle.ProfileWithEmail -> {
                profileType = ProfileType.PROFILE_REGISTER
                castcleId = (profile as ProfileBundle.ProfileWithEmail).castcleId
                setupToolBar()
            }
            is ProfileBundle.CreatePage -> {
                profileType = ProfileType.PROFILE_TYPE_PAGE_CREATE
                castcleId = (profile as ProfileBundle.CreatePage).castcleId
                setupToolBar()
            }
            else -> {
            }
        }

        onBindUi()
    }

    private fun onBindUi() {
        with(binding) {
            itOverView.onTextChanged = {
                onActiveButton(true)
            }
            itBirthday.onTextChanged = {
                onActiveButton(true)
            }
            btDone.text = localizedResources.getString(R.string.edite_profile_save)
        }
    }

    private fun onBindViewProfile() {
        setupEditProfileToolBar()
        with(binding) {
            val profileEdit = profile as ProfileBundle.ViewProfile
            groupAddLinks.gone()
            groupIsCreatePage.visible()
            itOverView.primaryText = profileEdit.overview
            itOverView.onDisableDrawableClick()
            itBirthday.primaryText = profileEdit.dob ?: ""
            itBirthday.onDisableDrawableClick()
            btDone.gone()
            onBindLinksViewProfile(profileEdit)
        }
    }

    private fun onBindEditProfile() {
        setupEditProfileToolBar()
        with(binding) {
            val profileEdit = profile as ProfileBundle.ProfileEdit
            groupAddLinks.gone()
            groupIsCreatePage.visible()
            itOverView.primaryText = profileEdit.overview
            itBirthday.primaryText = profileEdit.dob ?: ""
            onBindLinksEditProfile(profileEdit)
        }
    }

    private fun onBindEditPage() {
        setupEditProfileToolBar()
        with(binding) {
            val profileEdit = profile as ProfileBundle.PageEdit
            itOverView.primaryText = profileEdit.overview
            itBirthday.primaryText = profileEdit.dob ?: ""
            onBindLinksEditPage(profileEdit)
            groupAddLinks.gone()
            groupIsCreatePage.gone()
        }
    }

    private fun setupEditProfileToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.gone()
            tvToolbarTitle.text = localizedResources.getString(R.string.profile_edit_profile)
            tvToolbarTitle.setTextColor(requireContext().getColorResource(R.color.white))
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.apply {
                visible()
                text = localizedResources.getString(R.string.tool_bar_skip)
            }.run {
                subscribeOnClick {
                    handlerSkip()
                }.addToDisposables()
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
                        onUpdatePage()
                    },
                    onProfile = {
                        onRequestUpdateProfile()
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
        when (profileType) {
            ProfileType.PROFILE_TYPE_ME -> {
                onProfile.invoke()
            }
            ProfileType.PROFILE_TYPE_PAGE -> {
                onPage.invoke()
            }
            ProfileType.PROFILE_TYPE_PAGE_CREATE -> {
                onPage.invoke()
            }
            else -> {
                onProfile.invoke()
            }
        }
    }

    private fun onUpdatePage() {
        val profileCastcleId = when (profile) {
            is ProfileBundle.PageEdit -> {
                (profile as ProfileBundle.PageEdit).castcleId
            }
            else -> {
                (profile as ProfileBundle.CreatePage).castcleId
            }
        }
        val requestUpdate = CreatePageRequest(
            overview = binding.itOverView.primaryText,
            castcleId = profileCastcleId,
            links = getWebLinkRequest()
        )

        viewModel.requestUpdatePage(requestUpdate)
            .subscribeBy(
                onComplete = {
                    if (profileType == ProfileType.PROFILE_TYPE_PAGE_CREATE) {
                        onNavigateToProfile()
                    } else {
                        displayErrorMessage(
                            localizedResources.getString(R.string.edite_page_success_message)
                        )
                        onActiveButton(false)
                    }
                }, onError = {
                    displayError(it)
                }
            ).addToDisposables()
    }

    private fun onRequestUpdateProfile() {
        with(binding) {
            val requestUpdate = UserUpdateRequest(
                dob = itBirthday.primaryText,
                overview = itOverView.primaryText,
                links = getWebLinkRequest()
            )

            viewModel.requestUpdateProfile(requestUpdate)
                .subscribeBy(
                    onComplete = {
                        handleOnEditeProfile()
                    }
                ).addToDisposables()
        }
    }

    private fun getWebLinkRequest(): LinksRequest {
        with(binding) {
            return LinksRequest(
                facebook = linksRequest.value?.facebook ?: itLinkFacebook.primaryText,
                twitter = linksRequest.value?.twitter ?: itLinkTwitter.primaryText,
                youtube = linksRequest.value?.youtube ?: itLinkYouTube.primaryText,
                website = linksRequest.value?.website ?: itLinkWebSite.primaryText,
                medium = linksRequest.value?.medium ?: itLinkMedium.primaryText,
            )
        }
    }

    private fun handleOnEditeProfile() {
        when (profileType) {
            ProfileType.PROFILE_TYPE_PAGE,
            ProfileType.PROFILE_REGISTER -> {
                onNavigateToProfile()
            }
            ProfileType.PROFILE_TYPE_ME -> {
                displayErrorMessage(
                    localizedResources.getString(R.string.edite_profile_success_message)
                )
                onActiveButton(false)
            }
            else -> {
                onNavigateToFeedFragment()
            }
        }
    }

    private fun navigateToAddLinksFragment() {
        onBoardNavigator.navigateToAddLinksFragment(
            linksRequest.value ?: LinksRequestUiModel()
        )
    }

    private fun onBindLinksViewProfile(profileBundle: ProfileBundle) {
        if (profileBundle is ProfileBundle.ViewProfile) {
            with(binding) {
                profileBundle.facebookLinks.isNotEmpty().run {
                    itLinkFacebook.visibleOrGone(true)
                    itLinkFacebook.isEditable = false
                    if (this) {
                        itLinkFacebook.primaryText = profileBundle.facebookLinks
                    }
                }
                profileBundle.twitterLinks.isNotEmpty().run {
                    itLinkTwitter.visibleOrGone(true)
                    itLinkTwitter.isEditable = false
                    if (this) {
                        itLinkTwitter.primaryText = profileBundle.twitterLinks
                    }

                }
                profileBundle.youtubeLinks.isNotEmpty().run {
                    itLinkYouTube.visibleOrGone(true)
                    itLinkYouTube.isEditable = false
                    if (this) {
                        itLinkYouTube.primaryText = profileBundle.youtubeLinks
                    }
                }
                profileBundle.mediumLinks.isNotEmpty().run {
                    itLinkMedium.visibleOrGone(true)
                    itLinkMedium.isEditable = false
                    if (this) {
                        itLinkMedium.primaryText = profileBundle.mediumLinks
                    }
                }
                profileBundle.websiteLinks.isNotEmpty().run {
                    itLinkWebSite.visibleOrGone(true)
                    itLinkWebSite.isEditable = false
                    if (this) {
                        itLinkWebSite.primaryText = profileBundle.websiteLinks
                    }
                }
            }
        }
    }

    private fun onBindLinksEditProfile(profileBundle: ProfileBundle) {
        if (profileBundle is ProfileBundle.ProfileEdit) {
            with(binding) {
                profileBundle.facebookLinks.isNotEmpty().run {
                    itLinkFacebook.visibleOrGone(true)
                    if (this) {
                        itLinkFacebook.primaryText = profileBundle.facebookLinks
                    }
                    itLinkFacebook.onTextChanged = {
                        onActiveButton(true)
                    }
                }
                profileBundle.twitterLinks.isNotEmpty().run {
                    itLinkTwitter.visibleOrGone(true)
                    if (this) {
                        itLinkTwitter.primaryText = profileBundle.twitterLinks
                    }
                    itLinkTwitter.onTextChanged = {
                        onActiveButton(true)
                    }
                }
                profileBundle.youtubeLinks.isNotEmpty().run {
                    itLinkYouTube.visibleOrGone(true)
                    if (this) {
                        itLinkYouTube.primaryText = profileBundle.youtubeLinks
                    }
                    itLinkYouTube.onTextChanged = {
                        onActiveButton(true)
                    }
                }
                profileBundle.mediumLinks.isNotEmpty().run {
                    itLinkMedium.visibleOrGone(true)
                    if (this) {
                        itLinkMedium.primaryText = profileBundle.mediumLinks
                    }
                    itLinkWebSite.onTextChanged = {
                        onActiveButton(true)
                    }
                }
                profileBundle.websiteLinks.isNotEmpty().run {
                    itLinkWebSite.visibleOrGone(true)
                    if (this) {
                        itLinkWebSite.primaryText = profileBundle.websiteLinks
                    }
                    itLinkWebSite.onTextChanged = {
                        onActiveButton(true)
                    }
                }
            }
        }
    }

    private fun onBindLinksEditPage(profileBundle: ProfileBundle) {
        when (profileBundle) {
            is ProfileBundle.PageEdit -> {
                with(binding) {
                    profileBundle.facebookLinks.isNotEmpty().run {
                        itLinkFacebook.visibleOrGone(true)
                        if (this) {
                            itLinkFacebook.primaryText = profileBundle.facebookLinks
                        }
                        itLinkFacebook.onTextChanged = {
                            onActiveButton(true)
                        }
                    }
                    profileBundle.twitterLinks.isNotEmpty().run {
                        itLinkTwitter.visibleOrGone(true)
                        if (this) {
                            itLinkTwitter.primaryText = profileBundle.twitterLinks
                        }
                        itLinkTwitter.onTextChanged = {
                            onActiveButton(true)
                        }
                    }
                    profileBundle.youtubeLinks.isNotEmpty().run {
                        itLinkYouTube.visibleOrGone(true)
                        if (this) {
                            itLinkYouTube.primaryText = profileBundle.youtubeLinks
                        }
                        itLinkYouTube.onTextChanged = {
                            onActiveButton(true)
                        }
                    }
                    profileBundle.mediumLinks.isNotEmpty().run {
                        itLinkMedium.visibleOrGone(true)
                        if (this) {
                            itLinkMedium.primaryText = profileBundle.mediumLinks
                        }
                        itLinkWebSite.onTextChanged = {
                            onActiveButton(true)
                        }
                    }
                    profileBundle.websiteLinks.isNotEmpty().run {
                        itLinkWebSite.visibleOrGone(true)
                        if (this) {
                            itLinkWebSite.primaryText = profileBundle.websiteLinks
                        }
                        itLinkWebSite.onTextChanged = {
                            onActiveButton(true)
                        }
                    }
                }
            }
            else -> {
            }
        }
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
        onBoardNavigator.navigateToProfileFragment(
            castcleId, PROFILE_TYPE_PAGE
        )
    }

    private fun onNavigateToFeedFragment() {
        onBoardNavigator.nvaigateToFeedFragment()
        activityViewModel.onRefreshProfile()
    }

    override fun bindViewModel() = Unit
}
