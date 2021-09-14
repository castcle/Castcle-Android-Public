package com.castcle.ui.createbloc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.toContentUiModel
import com.castcle.common_model.model.userprofile.*
import com.castcle.data.staticmodel.ContentType
import com.castcle.usecase.userprofile.*
import io.reactivex.*
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
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
//  Created by sklim on 13/9/2021 AD at 10:05.

class CreateBlogFragmentViewModelImpl @Inject constructor(
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
    private val createContentSingleUseCase: CreateContentSingleUseCase,
    private val getCastcleIdSingleUseCase: GetCastcleIdSingleUseCase,
) : CreateBlogFragmentViewModel(), CreateBlogFragmentViewModel.Input {

    private var _userProfileUiModel = MutableLiveData<ContentUiModel>()

    private var _castUserProfile = MutableLiveData<User>()

    private val _showLoading = BehaviorSubject.create<Boolean>()

    private val _error = PublishSubject.create<Throwable>()

    private val _message = BehaviorSubject.create<String>()

    private val _imageContent = BehaviorSubject.create<List<Content>>()

    private val _imageCover = BehaviorSubject.create<String>()

    override val isGuestMode: Boolean
        get() = getCastcleIdSingleUseCase.execute(Unit).blockingGet()

    override val userProfileUiModel: LiveData<ContentUiModel>
        get() = _userProfileUiModel

    override val input: Input
        get() = this

    override val enableSubmitButton: Observable<Boolean>
        get() = Observables.combineLatest(
            _message,
            _imageContent,
            _imageCover
        ) { message, imageContent, imageCover ->
            when {
                message.isNotEmpty() || imageContent.isEmpty() || imageCover.isEmpty() -> true
                else -> false
            }
        }

    override fun createContent(): Single<CreateContentUiModel> {
        return createContentSingleUseCase.execute(
            CreateContentRequest(
                type = contentType.blockingFirst().type,
                payload = Payload(
                    message = _message.blockingFirst()
                ),
                authorId = _castUserProfile.value?.castcleId ?: "",
                createType = ContentType.FEED.type
            )
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.doOnSuccess {
            _showLoading.onNext(true)
        }.onErrorReturn { CreateContentUiModel() }
    }

    private val contentType: Observable<ContentType>
        get() = Observables.combineLatest(
            _message,
            _imageContent,
            _imageCover
        ) { message, imageContent, imageCover ->
            when {
                message.isNotEmpty() || imageContent.isEmpty() || imageCover.isEmpty() ->
                    ContentType.SHORT
                message.isNotEmpty() || imageContent.isEmpty() || imageCover.isNotEmpty() ->
                    ContentType.BLOG
                message.isNotEmpty() || imageContent.isNotEmpty() || imageCover.isEmpty() ->
                    ContentType.IMAGE
                else -> ContentType.SHORT
            }
        }

    override fun validateMessage(message: String): Completable =
        Completable
            .fromCallable { require(message.isNotEmpty()) }
            .doOnSubscribe {
                _message.onNext(message)
                _imageContent.onNext(emptyList())
                _imageCover.onNext("")
            }

    override fun validateImageContent(imageList: List<String>?) {
    }

    override fun validateImageCover(imageCover: String) {
    }

    override fun fetchCastUserProfile(): Completable {
        return cachedUserProfileSingleUseCase
            .execute(Unit)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnNext {
                _castUserProfile.value = it.get()
                _userProfileUiModel.value = it.get().toContentUiModel()
            }
            .doFinally { _showLoading.onNext(false) }
            .doOnError(_error::onNext).firstOrError()
            .ignoreElement()
    }
}
