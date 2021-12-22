package com.castcle.ui.feed.feeddetail

import android.annotation.SuppressLint
import android.app.Activity
import android.view.*
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedDetailBinding
import com.castcle.android.databinding.ToolbarCastcleLanguageBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikeCommentRequest
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.setting.PageHeaderUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.custom.event.EndlessRecyclerViewScrollListener
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.common.dialog.recast.*
import com.castcle.ui.common.events.*
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.lyrebirdstudio.croppylib.util.extensions.showKeyboard
import com.stfalcon.imageviewer.StfalconImageViewer
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

class FeedDetailFragment : BaseFragment<FeedDetailFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedDetailBinding>,
    ToolbarBindingInflater<ToolbarCastcleLanguageBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var adapterMockCommon: CommonMockAdapter

    private lateinit var adapterComment: CommentedAdapter

    private var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

    private val feedFetaailArgs: FeedDetailFragmentArgs by navArgs()

    private var replyState: Boolean = false

    private var onReplyComment: Boolean = false

    private val contentUiModel: ContentFeedUiModel
        get() = feedFetaailArgs.contentUiModel

    private val isContent: Boolean
        get() = feedFetaailArgs.isContent

    private lateinit var userProfile: User

    private var commentId: String = ""

    private var commentLikeStatus: Boolean = false

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleLanguageBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleLanguageBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleLanguageBinding
        get() = toolbarViewBinding as ToolbarCastcleLanguageBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentFeedDetailBinding
        get() = { inflater, container, attachToRoot ->
            FragmentFeedDetailBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentFeedDetailBinding
        get() = viewBinding as FragmentFeedDetailBinding

    override fun viewModel(): FeedDetailFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(FeedDetailFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        viewModel.fetachCommentedPage(contentUiModel.contentId)

        viewModel.fetchUserProfile()
    }

    override fun onResume() {
        super.onResume()
        with(requireActivity()) {
            softInputMode = getSoftInputMode()
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onPause() {
        super.onPause()
        with(requireActivity()) {
            hideSoftKeyboard()
            setSoftInputMode(softInputMode)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setupView() {
        setupToolBar()

        binding.clContentFeed.run {
            adapter = CommonMockAdapter().also {
                adapterMockCommon = it
            }
        }

        adapterMockCommon.itemClick.subscribe {
            handleContentItemClick(it)
        }.addToDisposables()

        val linearLayoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        binding.rvViewComment.run {
            adapter = CommentedAdapter().also {
                adapterComment = it
            }
            addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    viewModel.fetachNextCommentedPage()
                }
            })
        }
    }

    private fun handleContentItemClick(click: Click) {
        when (click) {
            is FeedItemClick.FeedLikeClick -> {
                handleLikeClick(click.contentUiModel)
            }
            is FeedItemClick.FeedRecasteClick -> {
                handleRecastClick(click.contentUiModel)
            }
            is FeedItemClick.FeedCommentClick -> {
                handleCommentClick()
            }
            is FeedItemClick.FeedImageClick -> {
                handleImageItemClick(click.position, click.contentUiModel)
            }
            is FeedItemClick.FeedFollowingClick -> {
                handleFeedFollowingClick(click.contentUiModel)
            }
            is FeedItemClick.WebContentClick -> {
                handleWebContentClick(click.contentUiModel)
            }
            is FeedItemClick.WebContentMessageClick -> {
                handleWebContentMessageClick(click)
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
                adapterMockCommon.updateStateItemFollowing()
            }, onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun handleCommentClick() {
        with(binding.itemComment) {
            etInputMessages.requestFocus()
            etInputMessages.showKeyboard()
        }
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

    private fun handleRecastClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            navigateToRecastDialogFragment(contentUiModel)
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun navigateToRecastDialogFragment(contentUiModel: ContentFeedUiModel) {
        onBoardNavigator.navigateToRecastDialogFragment(contentUiModel)

        if (contentUiModel.recasted) {
            getNavigationResult<ContentFeedUiModel>(
                onBoardNavigator,
                R.id.feedDetailFragment,
                KEY_REQUEST_UNRECAST,
                onResult = {
                    onRecastContent(it)
                })
        } else {
            getNavigationResult<ContentFeedUiModel>(
                onBoardNavigator,
                R.id.feedDetailFragment,
                KEY_REQUEST,
                onResult = {
                    onRecastContent(it, true)
                })
        }
    }

    private fun onRecastContent(currentContent: ContentFeedUiModel, onRecast: Boolean = false) {
        viewModel.recastContent(currentContent).subscribeBy(
            onError = {
                displayError(it)
            }
        ).addToDisposables()
        handlerUpdateRecasted(onRecast)
    }

    private fun handlerUpdateRecasted(
        onRecast: Boolean
    ) {
        if (onRecast) {
            adapterMockCommon.updateStateItemRecast()
        } else {
            adapterMockCommon.updateStateItemUnRecast()
        }
    }

    private fun handleLikeClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            val likeContentRequest = LikeContentRequest(
                contentId = contentUiModel.contentId,
                feedItemId = contentUiModel.id,
                authorId = viewModel.userProfile.value?.castcleId ?: "",
                likeStatus = contentUiModel.liked
            )
            viewModel.updateLikeContent(likeContentRequest)
            if (!contentUiModel.liked) {
                adapterMockCommon.onUpdateItemLikeCount()
            } else {
                adapterMockCommon.onUpdateItemUnLikeCount()
            }
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

    private fun onBindStartComment(showCommentState: Boolean = false) {
        with(binding.itemComment) {
            etInputMessages.requestFocus()
            requireContext().showSoftKeyboard(etInputMessages)
        }
    }

    private fun handleEmptyState(isEmpty: Boolean) {
        with(binding) {
            rvViewComment.visibleOrGone(!isEmpty)
            empState.visibleOrGone(isEmpty)
            empState.bindUiState(EmptyState.COMMENT_EMPTY)
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.invisible()
            tvToolbarTitle.text = localizedResources.getString(R.string.feed_detail_post_of).format(
                contentUiModel.userContent.displayName
            )
            tvToolbarTitle.setTextColor(
                requireContext().getColorResource(
                    R.color.white
                )
            )
            ivToolbarLogoButton
                .subscribeOnClick {
                    navigatePopBack()
                }.addToDisposables()
        }
    }

    private fun navigatePopBack() {
        setNavigationResult(onBoardNavigator, KEY_REQUEST_COMMENTED_COUNT, commentedCount)
        onBoardNavigator.findNavController().popBackStack()
    }

    @SuppressLint("CheckResult")
    override fun bindViewEvents() {
        with(binding.itemComment) {
            tvTitle.text = localizedResources.getString(R.string.comment_title).format(
                contentUiModel.userContent.displayName
            )
            clItemComment.subscribeOnClick {
                replyState = !replyState
                onBindStartComment(replyState)
            }.addToDisposables()

            bvSentComment.subscribeOnClick {
                onSentReplyComment()
            }.addToDisposables()
        }

        adapterComment.itemClick.subscribe {
            handlerCommentItemClick(it)
        }.addToDisposables()
    }

    private fun handlerCommentItemClick(it: Click) {
        when (it) {
            is CommentItemClick.CommentReplyClick -> {
                handlerReplyClickItem(it)
            }
            is CommentItemClick.CommentedLikeChildClick -> {
                handlerCommentedLike(it)
            }
            is CommentItemClick.CommentedLikedItemClick -> {
                handlerCommentedLike(it)
            }
        }
    }

    private fun handlerCommentedLike(item: CommentItemClick) {
        when (item) {
            is CommentItemClick.CommentedLikeChildClick -> {
                onLikedReplyComment(item.commentedId, item.replyId, item.likeStatus)
            }
            is CommentItemClick.CommentedLikedItemClick -> {
                commentId = item.contentUiModel.id
                commentLikeStatus = item.contentUiModel.payLoadUiModel.likedUiModel.liked

                onLikedComment(
                    contentId = contentUiModel.contentId,
                    commentId = item.contentUiModel.id,
                    item.contentUiModel.payLoadUiModel.likedUiModel.liked
                )
            }
            else -> {
            }
        }
    }

    private fun onLikedReplyComment(contentId: String, commentId: String, likeStatus: Boolean) {
        val commentedRequest = LikeCommentRequest(
            authorId = userProfile.castcleId,
            commentId = commentId,
            contentId = contentId,
            likeStatue = likeStatus
        )
        viewModel.likedComment(commentedRequest)
        if (likeStatus) {
            adapterComment.onUpdateReplyLiked(contentId, commentId)
        } else {
            adapterComment.onUpdateReplyUnLiked(contentId, commentId)
        }
        onClearStatus()
    }

    private fun onLikedComment(contentId: String, commentId: String, likeStatus: Boolean) {
        val commentedRequest = LikeCommentRequest(
            authorId = userProfile.castcleId,
            commentId = commentId,
            contentId = contentId,
            likeStatue = likeStatus
        )
        viewModel.likedComment(commentedRequest)
        onUpdateLikedComment(commentLikeStatus, commentId)
        onClearStatus()
    }

    private fun handlerReplyClickItem(it: CommentItemClick.CommentReplyClick) {
        onBindStartComment(true)
        with(binding.itemComment) {
            val castcleId = it.contentUiModel.payLoadUiModel.author.castcleId
            commentId = it.contentUiModel.id
            commentLikeStatus = it.contentUiModel.payLoadUiModel.likedUiModel.liked

            with(etInputMessages) {
                setText(MENTION_CASTCLE_ID.format(castcleId))
                setSelection(etInputMessages.text.length)
                onReplyComment = true
            }
        }
    }

    private fun getReplyCommentRequest(): ReplyCommentRequest {
        return ReplyCommentRequest(
            message = binding.itemComment.etInputMessages.text.toString(),
            contentId = contentUiModel.contentId,
            commentId = commentId,
            authorId = userProfile.castcleId
        )
    }

    private fun onSentReplyComment() {
        with(binding.itemComment) {
            val commentRequest = getReplyCommentRequest()
            if (etInputMessages.text.isNotBlank()) {
                commentedCount = commentedCount.plus(1)
                sentReplyComment(commentRequest)
            }
        }
    }

    private fun sentReplyComment(commentRequest: ReplyCommentRequest) {
        if (onReplyComment) {
            viewModel.input.setReplyComment(commentRequest)
        } else {
            adapterMockCommon.onUpdateItemCommentedCount()
            viewModel.input.setComment(commentRequest)
        }
    }

    override fun bindViewModel() {
        onBindFeedContent()

        viewModel.commentedResponses.subscribe {
            onBindCommentedItem(it)
        }.addToDisposables()

        viewModel.showLoading.subscribe(::bindLoading).addToDisposables()

        viewModel.onError.subscribe {
            displayError(it)
            bindLoading(false)
        }.addToDisposables()

        viewModel.userPageUiModel.observe(viewLifecycleOwner, {
            onBindUserProfileComment(it)
        })

        viewModel.userProfile.observe(viewLifecycleOwner, {
            userProfile = it
        })

        viewModel.onSentCommentResponse.subscribe {
            adapterComment.onInsertComment(it)
        }.addToDisposables()
    }

    private fun onUpdateLikedComment(likeStatus: Boolean, commentId: String) {
        if (!likeStatus) {
            adapterComment.onUpdateLiked(commentId)
        } else {
            adapterComment.onUpdateUnLiked(commentId)
        }
    }

    private fun onClearStatus() {
        commentLikeStatus = false
        commentId = ""
    }

    private fun onBindUserProfileComment(userAndPage: PageHeaderUiModel) {
        with(binding.itemComment) {
            userAndPage.pageUiItem.firstOrNull()?.avatarUrl?.let {
                ivAvatar.loadCircleImage(it)
            }
        }
    }

    private fun bindLoading(showLoading: Boolean) {
        if (showLoading) {
            adapterComment.showLoading()
        } else {
            adapterComment.hideLoading()
        }
        onEmptyReply()
    }

    private var commentedCount = 0

    @SuppressLint("NotifyDataSetChanged")
    private fun onBindCommentedItem(list: List<ContentUiModel>) {
        if (list.isNotEmpty()) {
            handleEmptyState(false)
            adapterComment.uiModels = list
            if (adapterComment.uiModels.isNotEmpty()) {
                adapterComment.notifyDataSetChanged()
            }
        } else {
            handleEmptyState(true)
        }
    }

    private fun onEmptyReply() {
        onReplyComment = false
        with(binding.itemComment) {
            etInputMessages.setText("")
        }
    }

    private fun onBindFeedContent() {
        adapterMockCommon.uiModels = listOf(contentUiModel)
    }
}

private const val MENTION_CASTCLE_ID = "@%s"
