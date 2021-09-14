package com.castcle.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.castcle.android.R
import com.castcle.android.databinding.FragmentProfileBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.userprofile.User
import com.castcle.data.staticmodel.TabContentStatic
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.profile.viewholder.UserProfileAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
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
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding>,
    ViewBindingInflater<FragmentProfileBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private lateinit var userProfileAdapter: UserProfileAdapter

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

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

    override fun initViewModel() {
        viewModel.fetachUserProfileContent()
    }

    override fun setupView() {
        setupToolBar()

        with(binding.vpPageContent) {
            adapter = UserProfileAdapter().also {
                userProfileAdapter = it
            }
        }
        toolbarBinding.tvToolbarTitle.setTextColor(
            requireContext().getColorResource(R.color.white)
        )

        val tabStatic = TabContentStatic.tabContent
        with(binding.tabs) {
            addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    
                }

            })
        }
    }

    private fun setupToolBar() {
        with(binding) {
            appBarLayoutProfile.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { _, _ ->
                }
            )
        }

        with(toolbarBinding) {
            tvToolbarTitleAction.gone()
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        viewModel.errorMessage.observe(this, {
            binding.tvMessageError.run {
                visible()
                text = it
            }
        })

        viewModel.userProfileRes
            .subscribe {
                onBindProfile(it)
            }.addToDisposables()

        with(viewModel) {
            launchOnLifecycleScope {
                userProfileContentRes.collectLatest {
                    userProfileAdapter.submitData(it)
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            userProfileAdapter.loadStateFlow.collectLatest { loadStates ->
                val refresher = loadStates.refresh
                val displayEmptyMessage = (refresher is LoadState.NotLoading &&
                    refresher.endOfPaginationReached && userProfileAdapter.itemCount == 0)
//                binding.tvMessageError.visibleOrGone(displayEmptyMessage)
            }
        }
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
        binding.ivProfileCover.loadCircleImageCache(user.cover)
        toolbarBinding.tvToolbarTitle.text = user.castcleId
    }

    override fun bindViewModel() {
    }
}
