package com.castcle.ui.feed.feeddetail

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.castcle.common.lib.common.Optional
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikeCommentRequest
import com.castcle.common_model.model.setting.*
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.UserPage
import com.castcle.data.repository.CommentRepository
import com.castcle.networking.api.feed.datasource.FeedRepository
import com.castcle.ui.base.BaseViewCoroutinesModel
import com.castcle.ui.util.SingleLiveEvent
import com.castcle.usecase.feed.*
import com.castcle.usecase.userprofile.GetCachedUserProfileSingleUseCase
import com.castcle.usecase.userprofile.GetUserPageDataSingleUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
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

    abstract val commentedResponses: Observable<List<ContentUiModel>>

    abstract fun fetachCommentedPage(feedContentId: String)

    abstract fun fetachNextCommentedPage()

    abstract fun likedComment(likeCommentedRequest: LikeCommentRequest)

    abstract fun editComment(conntentId: String, commentId: String)

    abstract fun deleteComment(conntentId: String, commentId: String)

    abstract fun fetchUserProfile()

    abstract val userProfile: LiveData<User>

    abstract val userPageUiModel: SingleLiveEvent<PageHeaderUiModel>

    abstract val likedSuccess: Observable<Boolean>

    interface Input {
        fun getCommented(feedContentId: String)

        fun setComment(commentRequest: ReplyCommentRequest)

        fun setReplyComment(commentRequest: ReplyCommentRequest)
    }
}

class FeedDetailFragmentViewModelImpl @Inject constructor(
    private val feedRepository: FeedRepository,
    private val sentCommentSingleUseCase: SentCommentSingleUseCase,
    private val commentRepository: CommentRepository,
    private val likeCommentCompletableUseCase: LikeCommentCompletableUseCase,
    private val deleteCommentCompletableUseCase: DeleteCommentCompletableUseCase,
    private val getCommentedPagingSingleUseCase: GetCommentedPagingSingleUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
    private val getUserPageDataSingleUseCase: GetUserPageDataSingleUseCase,
    private val replyCommentSingleUseCase: SentReplyCommentSingleUseCase
) : FeedDetailFragmentViewModel(), FeedDetailFragmentViewModel.Input {

    override val input: Input
        get() = this

    private lateinit var _commentedResponse: Flow<PagingData<ContentDbModel>>
    override val commentedResponse: Flow<PagingData<ContentDbModel>>
        get() = _commentedResponse

    private val _showLoading = BehaviorSubject.create<Boolean>()
    override val showLoading: Observable<Boolean>
        get() = _showLoading

    private val _likedSuccess = BehaviorSubject.create<Boolean>()
    override val likedSuccess: Observable<Boolean>
        get() = _likedSuccess

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    private var _onSentCommentResponse = BehaviorSubject.create<ContentUiModel>()
    override val onSentCommentResponse: Observable<ContentUiModel>
        get() = _onSentCommentResponse

    private var _onRefreshComment = BehaviorSubject.create<Unit>()
    override val onRefreshComment: Observable<Unit>
        get() = _onRefreshComment

    private var _commentedId = BehaviorSubject.create<CommentRequest>()

    private var _commentedResponses =
        BehaviorSubject.create<List<ContentUiModel>>()
    override val commentedResponses: Observable<List<ContentUiModel>>
        get() = _commentedResponses

    private val _userProfile = MutableLiveData<User>()
    override val userProfile: LiveData<User>
        get() = _userProfile

    private val _userPageUiModel = SingleLiveEvent<PageHeaderUiModel>()
    override val userPageUiModel: SingleLiveEvent<PageHeaderUiModel>
        get() = _userPageUiModel

    override fun getCommented(feedContentId: String) {
        getCommentedPaging(CommentRequest(feedItemId = feedContentId))
    }

    private var totalPages: Int = 1
    private var nextPage: Int = 1

    private fun getCommentedPaging(commentRequest: CommentRequest) = launchPagingAsync({
        _showLoading.onNext(true)
        commentRepository.getCommentMediator(commentRequest).cachedIn(viewModelScope)
    }, {
        _showLoading.onNext(false)
        _commentedResponse = it
    })

    override fun setComment(commentRequest: ReplyCommentRequest) {
        sentCommentSingleUseCase.execute(commentRequest)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _error.onNext(it) }
            .subscribeBy(
                onSuccess = {
                    onBindSingleCommented(it)
                    _showLoading.onNext(false)
                }, onError = _error::onNext
            ).addToDisposables()
    }

    override fun setReplyComment(commentRequest: ReplyCommentRequest) {
        replyCommentSingleUseCase.execute(commentRequest)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _error.onNext(it) }
            .subscribeBy(
                onSuccess = {
                    onBindSingleReplyCommented(it, commentRequest)
                    _showLoading.onNext(false)
                }, onError = _error::onNext
            ).addToDisposables()
    }

    private fun onBindSingleReplyCommented(
        response: ContentUiModel?,
        commentRequest: ReplyCommentRequest
    ) {
        val oldData = _commentedResponses.value ?: emptyList()
        oldData.toMutableList().find {
            it.id == commentRequest.commentId
        }?.apply {
            response?.toReplyComment()?.let { it1 ->
                payLoadUiModel.replyUiModel = payLoadUiModel.replyUiModel?.toMutableList()?.apply {
                    addAll(listOf(it1))
                }
            }
        }
        _commentedResponses.onNext(oldData)
    }

    private fun onBindSingleCommented(item: ContentUiModel) {
        val oldData = _commentedResponses.value ?: emptyList()
        val commentedValue = oldData.toMutableList().apply {
            addAll(listOf(item))
        }
        _commentedResponses.onNext(commentedValue)
    }

    override fun fetachCommentedPage(feedContentId: String) {
        _commentedId.onNext(
            CommentRequest(
                feedItemId = feedContentId,
                paginationModel = PaginationModel(
                    limit = PAGE_SIZE_DEFAULT,
                    next = PAGE_NUMBER_DEFAULT
                )
            )
        )

        getCommentedPagingSingleUseCase.execute(_commentedId.blockingFirst())
            .doOnSubscribe {
                _showLoading.onNext(true)
            }.doOnError {
                _error.onNext(it)
            }.doFinally {
                _showLoading.onNext(false)
            }.subscribeBy(
                onSuccess = {
                    onBindContentUiModel(it)
                },
                onError = {
                    _error.onNext(it)
                }
            ).addToDisposables()
    }

    override fun fetachNextCommentedPage() {
        if (_commentedId.blockingFirst().paginationModel.next == 0) return
        getCommentedPagingSingleUseCase.execute(_commentedId.blockingFirst())
            .doOnSubscribe {
                _showLoading.onNext(true)
            }.doOnError {
                _error.onNext(it)
            }.doFinally {
                _showLoading.onNext(false)
            }.subscribeBy(
                onSuccess = {
                    onBindContentUiModel(it)
                },
                onError = {
                    _error.onNext(it)
                }
            ).addToDisposables()
    }

    private fun onBindContentUiModel(item: CommentedModel) {
        val oldData = _commentedResponses.value ?: emptyList()
        val commentedValue = oldData.toMutableList().apply {
            addAll(item.contentUiModel)
        }

        _commentedResponses.onNext(commentedValue)
        _commentedId.value.apply {
            nextPage = item.paginationModel.next ?: 0
        }?.let {
            _commentedId.onNext(it)
        }
    }

    override fun likedComment(likeCommentedRequest: LikeCommentRequest) {
        likeCommentCompletableUseCase.execute(likeCommentedRequest)
            .subscribeBy(
                onComplete = {
                    _likedSuccess.onNext(true)
                },
                onError = {
                    _error.onNext(it)
                }
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

    override fun fetchUserProfile() {
        cachedUserProfileSingleUseCase
            .execute(Unit)
            .flatMapCompletable {
                fetchUserPage(it)
            }.subscribeBy(
                onError = {
                    _error.onNext(it)
                }
            ).addToDisposables()
    }

    private fun fetchUserPage(user: Optional<User>): Completable {
        return getUserPageDataSingleUseCase.execute(Unit)
            .map {
                onPageUserData(it, user)
            }.ignoreElement()
    }

    private fun onPageUserData(pageList: List<UserPage>, user: Optional<User>) {
        setUserProfileData(user)
        val userPage = mutableListOf<PageUiModel>()
        val userPageHeader = if (user.isPresent) {
            user.get().toPageHeaderUiModel()
        } else {
            PageHeaderUiModel(emptyList())
        }
        val userPageData = pageList.toPageHeaderUiModel()
        userPage.apply {
            addAll(userPageHeader.pageUiItem)
            addAll(userPageData.pageUiItem)
        }
        _userPageUiModel.value = PageHeaderUiModel(
            userPage
        )
    }

    private fun setUserProfileData(user: Optional<User>) {
        if (user.isPresent) {
            _userProfile.value = user.get()
        }
    }
}


private const val PAGE_NUMBER_DEFAULT = 1
private const val PAGE_SIZE_DEFAULT = 25
