package com.castcle.ui.feed.feeddetail

import android.annotation.SuppressLint
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedDetailBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.feed.CommentRequest
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.converter.LikeCommentRequest
import com.castcle.common_model.model.setting.PageHeaderUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.custom.event.EndlessRecyclerViewScrollListener
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.CommentItemClick
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
//  Created by sklim on 1/9/2021 AD at 18:12.

class FeedDetailFragment : BaseFragment<FeedDetailFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedDetailBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var adapterMockCommon: CommonMockAdapter

    private lateinit var adapterComment: CommentedAdapter

    private var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

    private val feedFetaailArgs: FeedDetailFragmentArgs by navArgs()

    private var replyState: Boolean = false

    private var onReplyComment: Boolean = false

    private val contentUiModel: ContentUiModel
        get() = feedFetaailArgs.contentUiModel

    private val isContent: Boolean
        get() = feedFetaailArgs.isContent

    private lateinit var userProfile: User

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

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

    override fun initViewModel() {
        viewModel.fetachCommentedPage(contentUiModel.payLoadUiModel.contentId)

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

    override fun setupView() {
        setupToolBar()
        onBindCommentStatus()

        binding.clContentFeed.run {
            adapter = CommonMockAdapter().also {
                adapterMockCommon = it
            }
        }

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

        onBindStartComment()
    }

    private fun onBindStartComment() {
        with(binding.itemComment) {
            groupOnReply.gone()
            groupReply.visible()
            etInputMessage.requestFocus()
            requireContext().showSoftKeyboard(etInputMessage)
        }
    }

    private fun onBindCommentStatus() {
        with(binding.itemFooter) {
            tvLiked.text = localizedResources.getString(
                R.string.comment_footer_retweets
            ).format(
                contentUiModel.payLoadUiModel.commentedUiModel.count
            )
            tvCommented.text = localizedResources.getString(
                R.string.comment_footer_quote
            ).format(
                contentUiModel.payLoadUiModel.reCastedUiModel.count
            )
            tvReCasted.text = localizedResources.getString(
                R.string.comment_footer_liked
            ).format(
                contentUiModel.payLoadUiModel.likedUiModel.count
            )
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
            tvToolbarTitleAction.gone()
            tvToolbarTitle.text = contentUiModel.payLoadUiModel.author.displayName
            tvToolbarTitle.setTextColor(
                requireContext().getColorResource(
                    R.color.white
                )
            )
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    @SuppressLint("CheckResult")
    override fun bindViewEvents() {
        with(binding.itemComment) {
            tvTitle.text = localizedResources.getString(R.string.comment_title).format(
                contentUiModel.payLoadUiModel.author.displayName
            )
            clItemComment.subscribeOnClick {
                replyState = !replyState
                groupOnReply.visibleOrGone(!replyState)
                groupReply.visibleOrGone(replyState)
                if (replyState) {
                    etInputMessage.requestFocus()
                }
            }.addToDisposables()

            bvSentReply.subscribeOnClick {
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
                onLikedComment(item.commentedId, item.replyId, item.likeStatus)
            }
            is CommentItemClick.CommentedLikedItemClick -> {
                onLikedComment(
                    contentUiModel.payLoadUiModel.contentId,
                    item.contentUiModel.id,
                    item.contentUiModel.payLoadUiModel.likedUiModel.liked
                )
            }
            else -> {
            }
        }
    }

    private fun onLikedComment(contentId: String, commentId: String, likeStatus: Boolean) {
        val commentedRequest = LikeCommentRequest(
            authorId = userProfile.castcleId,
            feedItemId = contentId,
            commentId = commentId,
            likeStatue = likeStatus
        )
        viewModel.likedComment(commentedRequest)
    }

    private fun handlerReplyClickItem(it: CommentItemClick.CommentReplyClick) {
        onBindStartComment()
        with(binding.itemComment) {
            val castcleId = it.contentUiModel.payLoadUiModel.author.castcleId
            with(etInputMessage) {
                setText(
                    MENTION_CASTCLE_ID.format(castcleId)
                )
                setSelection(etInputMessage.text.length)
                onReplyComment = true
            }
        }
    }

    private fun onSentReplyComment() {
        with(binding.itemComment) {
            val commentRequest = CommentRequest(
                message = etInputMessage.text.toString(),
                feedItemId = getContentId(),
                authorId = userProfile.castcleId
            )
            if (etInputMessage.text.isNotBlank()) {
                sentReplyComment(commentRequest)
            }
        }
    }

    private fun getContentId(): String {
        return if (onReplyComment) {
            contentUiModel.payLoadUiModel.contentId
        } else {
            contentUiModel.payLoadUiModel.contentId
        }
    }

    private fun sentReplyComment(commentRequest: CommentRequest) {
        viewModel.input.setComment(commentRequest)
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
    }

    private fun onBindCommentedItem(list: List<ContentUiModel>) {
        if (list.isNotEmpty()) {
            onEmptyReply()
            handleEmptyState(false)
            adapterComment.uiModels = list
        } else {
            handleEmptyState(true)
        }
    }

    private fun onEmptyReply() {
        with(binding.itemComment) {
            etInputMessage.setText("")
        }
    }

    private fun onBindFeedContent() {
        adapterMockCommon.uiModels = listOf(contentUiModel)
    }
}

private const val MENTION_CASTCLE_ID = "@%s"
