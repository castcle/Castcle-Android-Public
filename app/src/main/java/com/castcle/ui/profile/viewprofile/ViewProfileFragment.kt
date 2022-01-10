package com.castcle.ui.profile.viewprofile

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.databinding.FragmentViewProfileBinding
import com.castcle.android.databinding.ToolbarCastcleLanguageBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardViewModel
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
//  Created by sklim on 7/1/2022 AD at 09:46.

class ViewProfileFragment : BaseFragment<ViewProfileFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentViewProfileBinding>,
    ToolbarBindingInflater<ToolbarCastcleLanguageBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val profileBundle: ViewProfileFragmentArgs by navArgs()

    private val profile: ProfileBundle
        get() = profileBundle.profileBundle

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentViewProfileBinding
        get() = { inflater, container, attachToRoot ->
            FragmentViewProfileBinding.inflate(inflater, container, attachToRoot)
        }

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleLanguageBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleLanguageBinding.inflate(inflater, container, attachToRoot)
        }

    override val toolbarBinding: ToolbarCastcleLanguageBinding
        get() = toolbarViewBinding as ToolbarCastcleLanguageBinding

    override val binding: FragmentViewProfileBinding
        get() = viewBinding as FragmentViewProfileBinding

    override fun viewModel(): ViewProfileFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ViewProfileFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() = Unit

    @SuppressLint("SetTextI18n")
    override fun setupView() {
        val profileBundle = (profile as ProfileBundle.ViewProfile)
        if (profile is ProfileBundle.ViewProfile) {
            onBindLinks(profileBundle)
            setupToolbar(profileBundle.castcleName)
            with(binding) {
                ivImageCover.loadImageWithoutTransformation(profileBundle.imageCover)
                ivAvatarProfile.loadCircleImage(profileBundle.avatar)
                tvCastcleName.text = profileBundle.castcleName
                tvCastcleId.text = "@ ${profileBundle.castcleId}"
                tvOverViewDescription.text = profileBundle.overview
                tvBirthdayDescription.text = profileBundle.dob?.toFormatDate()
            }
        }
    }

    private fun setupToolbar(titleName: String) {
        with(toolbarBinding) {
            tvToolbarTitle.text = titleName
            ivToolbarProfileButton.invisible()
            ivToolbarLogoButton.subscribeOnClick {
                findNavController().navigateUp()
            }.addToDisposables()
        }
    }

    override fun bindViewEvents() = Unit

    override fun bindViewModel() = Unit

    private fun onBindLinks(profileBundle: ProfileBundle.ViewProfile) {
        with(binding) {
            profileBundle.facebookLinks.isNotEmpty().run {
                itLinkFacebook.visibleOrGone(true)
                if (this) {
                    itLinkFacebook.text = profileBundle.facebookLinks
                }
                itLinkFacebook.subscribeOnClick {
                    openWebView(profileBundle.facebookLinks)
                }.addToDisposables()
            }
            profileBundle.twitterLinks.isNotEmpty().run {
                itLinkTwitter.visibleOrGone(true)
                if (this) {
                    itLinkTwitter.text = profileBundle.twitterLinks
                }
            }
            profileBundle.youtubeLinks.isNotEmpty().run {
                itLinkYouTube.visibleOrGone(true)
                if (this) {
                    itLinkYouTube.text = profileBundle.youtubeLinks
                }
            }
            profileBundle.mediumLinks.isNotEmpty().run {
                itLinkMedium.visibleOrGone(this)
                if (this) {
                    itLinkMedium.text = profileBundle.mediumLinks
                }
            }
            profileBundle.websiteLinks.isNotEmpty().run {
                itLinkWebSite.visibleOrGone(true)
                if (this) {
                    itLinkWebSite.text = profileBundle.websiteLinks
                }
            }
        }
    }

    private fun openWebView(url: String) {
        (context as Activity).openUri(url)
    }
}
