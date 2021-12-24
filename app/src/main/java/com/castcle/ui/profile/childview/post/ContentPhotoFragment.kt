package com.castcle.ui.profile.childview.post

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.castcle.android.R
import com.castcle.android.databinding.FragmentContentPhotoBinding
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.feed.FeedRequestHeader
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.data.staticmodel.ContentType
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
import com.stfalcon.imageviewer.StfalconImageViewer
import io.reactivex.rxkotlin.subscribeBy
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

class ContentPhotoFragment : BaseFragment<ProfileFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentContentPhotoBinding> {

    private lateinit var adapterPagingCommon: CommonAdapter

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    override val bindingInflater: (
        LayoutInflater, ViewGroup?, Boolean
    ) -> FragmentContentPhotoBinding
        get() = { inflater, container, attachToRoot ->
            FragmentContentPhotoBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentContentPhotoBinding
        get() = viewBinding as FragmentContentPhotoBinding

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
                        viewType = ProfileType.PROFILE_TYPE_PAGE.type,
                        type = ContentType.IMAGE.type,
                        isMeId = viewModel.castcleId
                    )
                )
            },
            onProfileMe = {
                viewModel.fetachUserProfileContent(
                    FeedRequestHeader(
                        viewType = ProfileType.PROFILE_TYPE_ME.type,
                        type = ContentType.IMAGE.type,
                        isMeId = viewModel.castcleId
                    )
                )
            },
            onProfileYou = {
                viewModel.fetachUserProfileContent(
                    FeedRequestHeader(
                        castcleId = activityViewModel.isContentTypeYouId.value ?: "",
                        viewType = ProfileType.PROFILE_TYPE_PEOPLE.type,
                        type = ContentType.IMAGE.type,
                        isMeId = viewModel.castcleId
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
            else -> {}
        }
    }

    override fun setupView() {
        startLoadingShimmer()
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
                    stopLoadingShimmer()
                }
                if (!isLoading) {
                    binding.swiperefresh.isRefreshing = false
                    stopLoadingShimmer()
                }
            }
        }
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
            is FeedItemClick.WebContentClick -> {
                handleWebContentClick(click.contentUiModel)
            }
            is FeedItemClick.FeedImageClick -> {
                handleImageItemClick(click.position, click.contentUiModel)
            }
        }
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

    private fun handleWebContentMessageClick(click: FeedItemClick.WebContentMessageClick) {
        openWebView(click.url)
    }

    private fun handleWebContentClick(contentUiModel: ContentFeedUiModel) {
        contentUiModel.link?.url?.let {
            openWebView(it)
        }
    }

    private fun openWebView(url: String) {
        (context as Activity).openUri(url)
    }

    private fun handleNavigateAvatarClick(contentUiModel: ContentFeedUiModel) {
        val deepLink = makeDeepLinkUrl(
            requireContext(), Input(
                type = DeepLinkTarget.USER_PROFILE_YOU,
                contentData = contentUiModel.userContent.displayName
            )
        ).toString()
        navigateByDeepLink(deepLink)
    }

    private fun navigateByDeepLink(url: String) {
        onBoardNavigator.navigateByDeepLink(url.toUri())
    }

    private fun handleLikeClick(contentUiModel: ContentFeedUiModel) {
        val likeContentRequest = LikeContentRequest(
            contentId = contentUiModel.contentId,
            feedItemId = contentUiModel.id,
            authorId = viewModel.castcleId,
            likeStatus = contentUiModel.liked
        )
        if (!contentUiModel.liked) {
            adapterPagingCommon.updateStateItemLike(contentUiModel)
        } else {
            adapterPagingCommon.updateStateItemUnLike(contentUiModel)
        }

        viewModel.likedContent(
            likeContentRequest
        ).subscribeBy(onError = {
            adapterPagingCommon.updateStateItemUnLike(contentUiModel)
            displayError(it)
        }).addToDisposables()
    }

    private fun handleRecastClick(contentUiModel: ContentFeedUiModel) {
        navigateToRecastDialogFragment(contentUiModel)
    }

    private fun navigateToRecastDialogFragment(contentUiModel: ContentFeedUiModel) {
        onBoardNavigator.navigateToRecastDialogFragment(contentUiModel)

        if (contentUiModel.recasted) {
            getNavigationResult<ContentFeedUiModel>(
                onBoardNavigator,
                R.id.profileFragment,
                KEY_REQUEST_UNRECAST,
                onResult = {
                    onRecastContent(it)
                })
        } else {
            getNavigationResult<ContentFeedUiModel>(
                onBoardNavigator,
                R.id.profileFragment,
                KEY_REQUEST,
                onResult = {
                    onRecastContent(it, true)
                })
        }
    }

    private fun onRecastContent(currentContent: ContentFeedUiModel, onRecast: Boolean = false) {
        val castcleId = activityViewModel.castcleId
        viewModel.recastContent(castcleId, currentContent).subscribeBy(
            onError = {
                adapterPagingCommon.updateStateItemUnRecast(currentContent)
                displayError(it)
            }
        ).addToDisposables()
        handlerUpdateRecasted(currentContent, onRecast)
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
