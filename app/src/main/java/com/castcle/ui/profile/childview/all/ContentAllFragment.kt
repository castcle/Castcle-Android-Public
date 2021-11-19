package com.castcle.ui.profile.childview.all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.castcle.android.R
import com.castcle.android.databinding.FragmentContentAllBinding
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonAdapter
import com.castcle.ui.common.dialog.recast.KEY_REQUEST
import com.castcle.ui.common.dialog.recast.KEY_REQUEST_UNRECAST
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.profile.ProfileFragmentViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
//  Created by sklim on 16/9/2021 AD at 19:01.

class ContentAllFragment : BaseFragment<ProfileFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentContentAllBinding> {

    private lateinit var adapterPagingCommon: CommonAdapter

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContentAllBinding
        get() = { inflater, container, attachToRoot ->
            FragmentContentAllBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentContentAllBinding
        get() = viewBinding as FragmentContentAllBinding

    override fun viewModel(): ProfileFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ProfileFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        isProfileType(
            onPage = {
                viewModel.fetachUserProfileContent(
                    FeedRequestHeader(
                        castcleId = activityViewModel.isContentTypeYouId.value ?: "",
                        viewType = ProfileType.PROFILE_TYPE_PAGE.type
                    )
                )
            },
            onProfileMe = {
                viewModel.fetachUserProfileContent(
                    FeedRequestHeader(
                        viewType = ProfileType.PROFILE_TYPE_ME.type
                    )
                )
            },
            onProfileYou = {
                viewModel.fetachUserProfileContent(
                    FeedRequestHeader(
                        castcleId = activityViewModel.isContentTypeYouId.value ?: "",
                        viewType = ProfileType.PROFILE_TYPE_PEOPLE.type
                    )
                )
            })
    }

    private fun isProfileType(
        onProfileMe: () -> Unit,
        onProfileYou: () -> Unit,
        onPage: () -> Unit
    ) {
        when (activityViewModel.isContentProfileType.value) {
            ProfileType.PROFILE_TYPE_ME -> {
                onProfileMe.invoke()
            }
            ProfileType.PROFILE_TYPE_PEOPLE -> {
                onProfileYou.invoke()
            }
            ProfileType.PROFILE_TYPE_PAGE -> {
                onPage.invoke()
            }
        }
    }

    override fun setupView() {
        with(binding) {
            rvContent.adapter = CommonAdapter().also {
                adapterPagingCommon = it
            }
        }

        lifecycleScope.launchWhenCreated {
            adapterPagingCommon.loadStateFlow.collectLatest { loadStates ->
                val refresher = loadStates.refresh
                val displayEmpty = (refresher is LoadState.NotLoading &&
                    !refresher.endOfPaginationReached && adapterPagingCommon.itemCount == 0)
                val isError = loadStates.refresh is LoadState.Error
                val isLoading = loadStates.refresh is LoadState.Loading
                if (isError) {
                    handleEmptyState(isError)
                }
                if (!isLoading) {
//                    stopLoadingShimmer()
                    activityViewModel.onProfileLoading(false)
                }
            }
        }
    }

//    private fun startLoadingShimmer() {
//        with(binding) {
//            skeletonLoading.shimmerLayoutLoading.run {
//                startShimmer()
//                visible()
//            }
//        }
//    }
//
//    private fun stopLoadingShimmer() {
//        with(binding) {
//            skeletonLoading.shimmerLayoutLoading.run {
//                stopShimmer()
//                setShimmer(null)
//                gone()
//            }
//        }
//    }

    private fun handleContentClick(click: Click) {
        when (click) {
            is FeedItemClick.FeedAvatarClick -> {
                handleNavigateAvatarClick(click.contentUiModel)
            }
            is FeedItemClick.FeedLikeClick -> {
                handleLikeClick(click.contentUiModel)
            }
            is FeedItemClick.FeedRecasteClick -> {
                handleRecastClick(click.contentUiModel)
            }
            is FeedItemClick.FeedCommentClick -> {
                handleCommentClick(click.contentUiModel)
            }
        }
    }

    private fun handleCommentClick(contentUiModel: ContentUiModel) {
        navigateToFeedDetailFragment(contentUiModel)
    }

    private fun navigateToFeedDetailFragment(contentUiModel: ContentUiModel) {
        onBoardNavigator.navigateToFeedDetailFragment(contentUiModel)
    }

    private fun handleNavigateAvatarClick(contentUiModel: ContentUiModel) {
        val profileType = if (activityViewModel.isContentProfileType.value
            == ProfileType.PROFILE_TYPE_ME
        ) {
            ProfileType.PROFILE_TYPE_ME.type
        } else {
            contentUiModel.payLoadUiModel.author.type
        }

        navigateToProfile(contentUiModel.payLoadUiModel.author.castcleId, profileType)
    }

    private fun navigateToProfile(castcleId: String, type: String) {
        onBoardNavigator.navigateToProfileFragment(castcleId, type)
    }

    private fun handleLikeClick(contentUiModel: ContentUiModel) {
        val likeContentRequest = LikeContentRequest(
            contentId = contentUiModel.payLoadUiModel.contentId,
            feedItemId = contentUiModel.payLoadUiModel.contentId,
            authorId = viewModel.castcleId,
            likeStatus = contentUiModel.payLoadUiModel.likedUiModel.liked
        )
        adapterPagingCommon.updateStateItemLike(contentUiModel)

        viewModel.likedContent(
            likeContentRequest
        ).subscribeBy(onError = {
            adapterPagingCommon.updateStateItemUnLike(contentUiModel)
            displayError(it)
        }).addToDisposables()
    }

    private fun handleRecastClick(contentUiModel: ContentUiModel) {
        navigateToRecastDialogFragment(contentUiModel)
    }

    private fun navigateToRecastDialogFragment(contentUiModel: ContentUiModel) {
        onBoardNavigator.navigateToRecastDialogFragment(contentUiModel)

        getNavigationResult<ContentUiModel>(
            onBoardNavigator,
            R.id.profileFragment,
            KEY_REQUEST,
            onResult = {
                adapterPagingCommon.updateStateItemRecast(it)
            })

        getNavigationResult<ContentUiModel>(
            onBoardNavigator,
            R.id.profileFragment,
            KEY_REQUEST_UNRECAST,
            onResult = {
                adapterPagingCommon.updateStateItemUnRecast(it)
            })
    }

    private fun handleEmptyState(show: Boolean) {
        with(binding) {
            rvContent.visibleOrGone(!show)
        }
        with(binding.empState) {
            visibleOrGone(show)
            bindUiState(com.castcle.common_model.model.empty.EmptyState.FEED_EMPTY)
        }
    }

    override fun bindViewEvents() {
        with(adapterPagingCommon) {
            itemClick.subscribe {
                handleContentClick(it)
            }.addToDisposables()
        }
    }

    override fun bindViewModel() {
        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                userProfileContentRes.collectLatest {
                    adapterPagingCommon.submitData(it)
                }
            }
        }
    }
}
