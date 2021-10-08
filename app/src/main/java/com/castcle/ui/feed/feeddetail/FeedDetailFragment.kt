package com.castcle.ui.feed.feeddetail

import android.annotation.SuppressLint
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedDetailBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.feed.CommentRequest
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.CommentItemClick
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.jakewharton.rxbinding3.view.focusChanges
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
//  Created by sklim on 1/9/2021 AD at 18:12.

class FeedDetailFragment : BaseFragment<FeedDetailFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedDetailBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var adapterMockCommon: CommonMockAdapter

    private lateinit var adapterComment: CommentAdapter

    private var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

    private val feedFetaailArgs: FeedDetailFragmentArgs by navArgs()

    private var replyState: Boolean = false

    private val contentUiModel: ContentUiModel
        get() = feedFetaailArgs.contentUiModel

    private val isContent: Boolean
        get() = feedFetaailArgs.isContent

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
        viewModel.input.getCommented(contentUiModel.payLoadUiModel.contentId)
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

        binding.rvViewComment.run {
            adapter = CommentAdapter().also {
                adapterComment = it
            }
        }

        lifecycleScope.launchWhenCreated {
            adapterComment.loadStateFlow.collectLatest { loadStates ->
                val refresher = loadStates.refresh
                val displayEmpty = (refresher is LoadState.NotLoading &&
                    !refresher.endOfPaginationReached && adapterComment.itemCount == 0)
                handleEmptyState(displayEmpty)
            }
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

    private fun handleEmptyState(show: Boolean) {
        with(binding) {
            rvViewComment.visibleOrGone(!show)
            empState.visibleOrGone(true)
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
            is CommentItemClick.CommentedLikeClick -> {

            }
        }
    }

    private fun handlerReplyClickItem(it: CommentItemClick.CommentReplyClick) {
        with(binding.itemComment) {
            replyState = true
            groupOnReply.visibleOrGone(false)
            groupReply.visibleOrGone(true)

            if (replyState) {
                etInputMessage.requestFocus()
            }
        }
    }

    private fun onSentReplyComment() {
        with(binding.itemComment) {
            val commentRequest = CommentRequest(
                message = etInputMessage.text.toString(),
                feedItemId = contentUiModel.payLoadUiModel.contentId,
                authorId = contentUiModel.payLoadUiModel.author.castcleId
            )
            if (etInputMessage.text.isNotBlank()) {
                sentReplyComment(commentRequest)
            }
        }
    }

    private fun sentReplyComment(commentRequest: CommentRequest) {
        viewModel.input.setComment(commentRequest)
    }

    override fun bindViewModel() {
        onBindFeedContent()

        with(viewModel) {
            launchOnLifecycleScope {
                commentedResponse.collectLatest {
                    adapterComment.submitData(lifecycle, it)
                }
            }

            onSentCommentResponse.subscribe {

            }.addToDisposables()

            onRefreshComment.subscribe {
                adapterComment.refresh()
            }.addToDisposables()
        }
    }

    private fun onBindFeedContent() {
        adapterMockCommon.uiModels = listOf(contentUiModel)
    }

}