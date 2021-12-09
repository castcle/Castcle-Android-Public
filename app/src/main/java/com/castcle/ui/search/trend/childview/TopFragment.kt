package com.castcle.ui.search.trend.childview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.castcle.android.R
import com.castcle.android.databinding.FragmentContentAllBinding
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.data.staticmodel.FeedContentType
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonAdapter
import com.castcle.ui.common.dialog.recast.KEY_REQUEST
import com.castcle.ui.common.dialog.recast.KEY_REQUEST_UNRECAST
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.search.trend.TrendFragmentViewModel
import com.stfalcon.imageviewer.StfalconImageViewer
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

class TopFragment : BaseFragment<TrendFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentContentAllBinding> {

    private lateinit var adapterPagingCommon: CommonAdapter

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var baseContentUiModel: ContentFeedUiModel

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContentAllBinding
        get() = { inflater, container, attachToRoot ->
            FragmentContentAllBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentContentAllBinding
        get() = viewBinding as FragmentContentAllBinding

    override fun viewModel(): TrendFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(TrendFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {

    }

    override fun setupView() {
        val trendSlug = activityViewModel.trendSlug.value ?: ""
        val feedRequestHeader = FeedRequestHeader(
            featureSlug = FeedContentType.FEED_SLUG.type,
            circleSlug = FeedContentType.CIRCLE_SLUG_FORYOU.type,
            hashtag = trendSlug
        )
        viewModel.getTesnds(feedRequestHeader)
        with(binding.rvContent) {
            adapter = CommonAdapter().also {
                adapterPagingCommon = it
            }
        }
        binding.rvContent.itemAnimator = null
    }

    private fun startLoadingShimmer() {
        with(binding) {
            skeletonLoading.shimmerLayoutLoading.run {
                startShimmer()
                visible()
            }
        }
    }

    private fun stopLoadingShimmer() {
        with(binding) {
            skeletonLoading.shimmerLayoutLoading.run {
                stopShimmer()
                setShimmer(null)
                gone()
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
            is FeedItemClick.FeedCommentClick -> {
                handleCommentClick(click.contentUiModel)
            }
            is FeedItemClick.FeedImageClick -> {
                handleImageItemClick(click.position, click.contentUiModel)
            }
            is FeedItemClick.FeedFollowingClick -> {
                handleFeedFollowingClick(click.contentUiModel)
            }
        }
    }

    private fun handleFeedFollowingClick(contentUiModel: ContentFeedUiModel) {
        activityViewModel.putToFollowUser(contentUiModel.userContent.castcleId).subscribeBy(
            onComplete = {
                displayMessage(
                    localizedResources.getString(R.string.feed_content_following_status)
                        .format(contentUiModel.userContent.castcleId)
                )
                adapterPagingCommon.updateStateItemFollowing(contentUiModel)
            }, onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun handleCommentClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            navigateToFeedDetailFragment(contentUiModel)
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun navigateToFeedDetailFragment(contentUiModel: ContentFeedUiModel) {
        onBoardNavigator.navigateToFeedDetailFragment(contentUiModel)
    }

    private fun handleNavigateAvatarClick(contentUiModel: ContentFeedUiModel) {
        var isMe = false
        val deeplinkType = if (contentUiModel.userContent.castcleId ==
            activityViewModel.castcleId
        ) {
            isMe = true
            ProfileType.PROFILE_TYPE_ME.type
        } else {
            contentUiModel.userContent.type
        }
        navigateToProfile(contentUiModel.userContent.castcleId, deeplinkType, isMe)
    }

    private fun navigateToProfile(castcleId: String, profileType: String, isMe: Boolean) {
        onBoardNavigator.navigateToProfileFragment(castcleId, profileType, isMe)
    }

    private fun handleLikeClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            baseContentUiModel = contentUiModel
            val likeContentRequest = LikeContentRequest(
                contentId = contentUiModel.contentId,
                feedItemId = contentUiModel.id,
                authorId = viewModel.userProfile.value?.castcleId ?: "",
                likeStatus = contentUiModel.liked
            )
            if (!contentUiModel.liked) {
                adapterPagingCommon.updateStateItemLike(contentUiModel)
            } else {
                adapterPagingCommon.updateStateItemUnLike(contentUiModel)
            }
            viewModel.updateLikeContent(likeContentRequest)
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun navigateToNotifyLoginDialog() {
        onBoardNavigator.navigateToNotiflyLoginDialogFragment()
    }

    private fun guestEnable(disable: () -> Unit, enable: () -> Unit) {
        if (viewModel.isGuestMode) {
            disable.invoke()
        } else {
            enable.invoke()
        }
    }

    private fun handleRecastClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            navigateToRecastDialogFragment(contentUiModel)
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun handleImageItemClick(position: Int, contentUiModel: ContentFeedUiModel) {
        val image = contentUiModel.photo?.map {
            it.imageOrigin
        }
        val imagePosition = when (contentUiModel.photo?.size) {
            1 -> 0
            else -> position
        }
        StfalconImageViewer.Builder(context, image, ::loadPosterImage)
            .withStartPosition(imagePosition)
            .withHiddenStatusBar(true)
            .allowSwipeToDismiss(true)
            .allowZooming(true)
            .show()
    }

    private fun loadPosterImage(imageView: ImageView, imageUrl: String) {
        imageView.loadImageWithoutTransformation(imageUrl)
    }

    private fun navigateToRecastDialogFragment(contentUiModel: ContentFeedUiModel) {
        onBoardNavigator.navigateToRecastDialogFragment(contentUiModel)

        if (contentUiModel.recasted) {
            getNavigationResult<ContentFeedUiModel>(
                onBoardNavigator,
                R.id.trendFragment,
                KEY_REQUEST_UNRECAST,
                onResult = {
                    onRecastContent(it)
                })
        } else {
            getNavigationResult<ContentFeedUiModel>(
                onBoardNavigator,
                R.id.trendFragment,
                KEY_REQUEST,
                onResult = {
                    onRecastContent(it, true)
                })
        }
    }

    private fun onRecastContent(currentContent: ContentFeedUiModel, onRecast: Boolean = false) {
        handlerUpdateRecasted(currentContent, onRecast)
        viewModel.recastContent(currentContent).subscribeBy(
            onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun handlerUpdateRecasted(
        currentContent: ContentFeedUiModel,
        onRecast: Boolean
    ) {
        if (onRecast) {
            adapterPagingCommon.updateStateItemRecast(currentContent)
        } else {
            adapterPagingCommon.updateStateItemUnRecast(currentContent)
        }
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

        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                feedTrendResponse.collectLatest {
                    adapterPagingCommon.submitData(lifecycle, it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
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
                    stopLoadingShimmer()
                    activityViewModel.onProfileLoading(false)
                } else {
                    startLoadingShimmer()
                }
            }
        }
    }

    override fun bindViewModel() {

    }
}
