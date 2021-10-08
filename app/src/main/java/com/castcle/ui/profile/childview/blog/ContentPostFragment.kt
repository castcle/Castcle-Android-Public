package com.castcle.ui.profile.childview.blog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.castcle.android.R
import com.castcle.android.databinding.FragmentContentPostBinding
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.data.staticmodel.ContentType
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonAdapter
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.common.dialog.recast.KEY_REQUEST
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.profile.ProfileFragmentViewModel
import com.castcle.ui.profile.adapter.VerticalDecoration
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
//  Created by sklim on 16/9/2021 AD at 19:01.

class ContentPostFragment : BaseFragment<ProfileFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentContentPostBinding> {

    private lateinit var adapterPagingCommon: CommonAdapter

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) ->
    FragmentContentPostBinding
        get() = { inflater, container, attachToRoot ->
            FragmentContentPostBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentContentPostBinding
        get() = viewBinding as FragmentContentPostBinding

    override fun viewModel(): ProfileFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ProfileFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        if (activityViewModel.isContentTypeMe.value == true) {
            val feedRequestHeader = FeedRequestHeader(
                type = ContentType.BLOG.type,
                isMe = true
            )
            viewModel.fetachUserProfileContent(feedRequestHeader)
        } else {
            val feedRequestHeader = FeedRequestHeader(
                castcleId = activityViewModel.isContentTypeYouId.value ?: ""
            )
            viewModel.fetachUserViewProfileContent(feedRequestHeader)
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
                handleEmptyState(displayEmpty)
            }
        }
    }

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
        }
    }

    private fun handleNavigateAvatarClick(contentUiModel: ContentUiModel) {
        val deepLink = makeDeepLinkUrl(
            requireContext(), Input(
                type = DeepLinkTarget.USER_PROFILE_YOU,
                contentData = contentUiModel.payLoadUiModel.author.displayName
            )
        ).toString()
        navigateByDeepLink(deepLink)
    }

    private fun navigateByDeepLink(url: String) {
        onBoardNavigator.navigateByDeepLink(url.toUri())
    }

    private fun handleLikeClick(contentUiModel: ContentUiModel) {

    }

    private fun handleRecastClick(contentUiModel: ContentUiModel) {
        navigateToRecastDialogFragment(contentUiModel)
    }

    private fun navigateToRecastDialogFragment(contentUiModel: ContentUiModel) {
        onBoardNavigator.navigateToRecastDialogFragment(contentUiModel)

        getNavigationResult<ContentUiModel>(
            onBoardNavigator,
            R.id.dialogRecastFragment,
            KEY_REQUEST,
            onResult = {
                adapterPagingCommon.updateStateItemRecast(it)
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
            launchOnLifecycleScope {
                userProfileContentRes.collectLatest {
                    adapterPagingCommon.submitData(lifecycle, it)
                }
            }
        }
    }
}
