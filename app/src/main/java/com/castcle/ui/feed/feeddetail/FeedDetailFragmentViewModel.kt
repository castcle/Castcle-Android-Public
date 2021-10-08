package com.castcle.ui.feed.feeddetail

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.castcle.common_model.model.feed.*
import com.castcle.data.repository.CommentRepository
import com.castcle.networking.api.feed.datasource.FeedRepository
import com.castcle.ui.base.BaseViewCoroutinesModel
import com.castcle.usecase.feed.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
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
//  Created by sklim on 1/9/2021 AD at 18:14.

abstract class FeedDetailFragmentViewModel : BaseViewCoroutinesModel() {

    abstract val input: Input

    abstract val showLoading: Observable<Boolean>

    abstract val onError: Observable<Throwable>

    abstract val onRefreshComment: Observable<Unit>

    abstract val commentedResponse: Flow<PagingData<ContentDbModel>>

    abstract val onSentCommentResponse: Observable<ContentUiModel>

    abstract fun likedComment(
        conntentId: String,
        commentId: String,
        likedStatus: Boolean
    )

    abstract fun editComment(conntentId: String, commentId: String)

    abstract fun deleteComment(conntentId: String, commentId: String)

    interface Input {
        fun getCommented(feedContentId: String)

        fun setComment(commentRequest: CommentRequest)
    }
}

class FeedDetailFragmentViewModelImpl @Inject constructor(
    private val feedRepository: FeedRepository,
    private val sentCommentSingleUseCase: SentCommentSingleUseCase,
    private val commentRepository: CommentRepository,
    private val likeCommentCompletableUseCase: LikeCommentCompletableUseCase,
    private val deleteCommentCompletableUseCase: DeleteCommentCompletableUseCase
) : FeedDetailFragmentViewModel(), FeedDetailFragmentViewModel.Input {

    override val input: Input
        get() = this

    private lateinit var _commentedResponse: Flow<PagingData<ContentDbModel>>
    override val commentedResponse: Flow<PagingData<ContentDbModel>>
        get() = _commentedResponse

    private val _showLoading = BehaviorSubject.create<Boolean>()
    override val showLoading: Observable<Boolean>
        get() = _showLoading

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    private var _onSentCommentResponse = BehaviorSubject.create<ContentUiModel>()
    override val onSentCommentResponse: Observable<ContentUiModel>
        get() = _onSentCommentResponse

    private var _onRefreshComment = BehaviorSubject.create<Unit>()
    override val onRefreshComment: Observable<Unit>
        get() = _onRefreshComment

    override fun getCommented(feedContentId: String) {
        getCommentedPaging(CommentRequest(feedItemId = feedContentId))
    }

    private fun getCommentedPaging(commentRequest: CommentRequest) = launchPagingAsync({
        _showLoading.onNext(true)
        commentRepository.getCommentMediator(commentRequest).cachedIn(viewModelScope)
    }, {
        _showLoading.onNext(false)
        _commentedResponse = it
    })

    override fun setComment(commentRequest: CommentRequest) {
        addCommented(commentRequest)
        sentCommentSingleUseCase.execute(commentRequest)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _error.onNext(it) }
            .subscribeBy(
                onSuccess = {
                    _onSentCommentResponse.onNext(it)
                    _showLoading.onNext(false)
                }, onError = _error::onNext
            ).addToDisposables()
    }

    private fun addCommented(commentRequest: CommentRequest) {
        val commented = ContentDbModel(
            id = UUID.randomUUID().variant().toString(),
            replyUiModel = listOf(
                ReplyUiModel(
                    id = UUID.randomUUID().variant().toString(),
                    created = "",
                    message = commentRequest.message ?: "",
                    author = AuthorUiModel()
                )
            ),
            payloadContent = PayloadContentUiModel(
                message = commentRequest.message
            ),
            liked = LikedUiModel(),
            commented = CommentedUiModel(),
            recasted = RecastedUiModel(),
            author = AuthorUiModel(
                displayName = "Test",
                castcleId = "Rx",
                followed = true,
                verified = true
            )
        )
        viewModelScope.launch {
            commentRepository.addCommentMediator(commented)
        }
    }

    override fun likedComment(conntentId: String, commentId: String, likedStatus: Boolean) {
        likeCommentCompletableUseCase.execute(
            LikeCommentCompletableUseCase.Input(conntentId, commentId, likedStatus)
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.subscribeBy(
            onComplete = {},
            onError = {}
        ).addToDisposables()
    }

    override fun editComment(conntentId: String, commentId: String) {

    }

    override fun deleteComment(conntentId: String, commentId: String) {
        deleteCommentCompletableUseCase.execute(
            DeleteCommentCompletableUseCase.Input(conntentId, commentId)
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.subscribeBy(
            onComplete = {},
            onError = {}
        ).addToDisposables()
    }
}
