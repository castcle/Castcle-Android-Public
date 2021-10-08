package com.castcle.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.*
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.userprofile.User
import com.castcle.data.staticmodel.ContentType
import com.castcle.data.staticmodel.TabContentStatic.tabContent
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.profile.adapter.ContentPageAdapter
import com.castcle.ui.profile.viewholder.UserProfileAdapter
import com.google.android.material.tabs.TabLayoutMediator
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
//  Created by sklim on 10/9/2021 AD at 11:38.

class ProfileFragment : BaseFragment<ProfileFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentProfileBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private lateinit var userProfileAdapter: UserProfileAdapter

    private lateinit var contentPageAdapter: ContentPageAdapter

    private val argsBundle: ProfileFragmentArgs by navArgs()

    private val profileType: String
        get() = argsBundle.me

    private val profileId: String
        get() = argsBundle.id

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = { inflater, container, attachToRoot ->
            FragmentProfileBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentProfileBinding
        get() = viewBinding as FragmentProfileBinding

    override fun viewModel(): ProfileFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ProfileFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() = Unit

    private fun isProfileIsMe(): Boolean {
        activityViewModel.setContentTypeMe(profileType == PROFILE_TYPE_ME)
        activityViewModel.setContentTypeYouId(profileId)
        return profileType == PROFILE_TYPE_ME
    }

    override fun setupView() {
        setupToolBar()
        with(binding.vpPageContent) {
            adapter = ContentPageAdapter(this@ProfileFragment).also {
                contentPageAdapter = it
            }
        }

        TabLayoutMediator(
            binding.tabs,
            binding.vpPageContent
        ) { Tab, position ->
            Tab.text = requireContext().getString(tabContent[position].tabNameRes)
        }.attach()

        if (isProfileIsMe()) {
            binding.profileMe.clMainContent.visible()
            binding.profileYou.clMainContent.gone()
        } else {
            binding.profileMe.clMainContent.gone()
            binding.profileYou.clMainContent.visible()
        }
    }

    private fun setupToolBar() {
        with(binding.tbProfile) {
            ivToolbarLogoButton.visible()
            tvToolbarTitle.setTextColor(
                requireContext().getColorResource(R.color.white)
            )
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {

    }

    override fun bindViewModel() {
        viewModel.getFeedResponse(ContentType.SHORT)
        viewModel.errorMessage.observe(this, {
            binding.tvMessageError.run {
                visible()
                text = it
            }
        })

        if (isProfileIsMe()) {
            viewModel.userProfileRes
                .subscribe {
                    onBindProfile(it)
                }.addToDisposables()
        } else {
            viewModel.getUserViewProfileMock(profileId)
        }

        viewModel.userProfileYouRes.subscribe {
            onBindViewProfile(it)
        }.addToDisposables()
    }

    private fun onBindProfile(user: User) {
        with(binding.profileMe) {
            tvFollowingCount.text = user.followersCount.toCount()
            tvFollowersCount.text = user.followersCount.toCount()
            tvProfileName.text = user.displayName
            tvProfileCastcleId.text = user.castcleId
            with(user.overview) {
                tvProfileOverView.visibleOrInvisible(!isEmpty())
                tvProfileOverView.text = this
            }
            ivAvatarProfile.loadCircleImage(user.avatar)
        }
        binding.ivProfileCover.loadImageWithCache(user.cover)
        binding.tbProfile.tvToolbarTitle.text = user.castcleId
    }

    private fun onBindViewProfile(user: User) {
        with(binding.profileYou) {
            tvFollowingCount.text = user.followersCount.toCount()
            tvFollowersCount.text = user.followersCount.toCount()
            tvProfileName.text = user.displayName
            tvProfileCastcleId.text = user.castcleId
            with(user.overview) {
                tvProfileOverView.visibleOrInvisible(!isEmpty())
                tvProfileOverView.text = this
            }
            ivAvatarProfile.loadCircleImage(user.avatar)
        }
        binding.ivProfileCover.loadImageWithCache(user.cover)
        binding.tbProfile.tvToolbarTitle.text = user.castcleId
    }
}

private const val PROFILE_TYPE_ME = "me"
