package com.castcle.ui.createbloc

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.android.R
import com.castcle.common_model.model.createblog.MediaItem
import com.castcle.common_model.model.createblog.toListUri
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.userprofile.*
import com.castcle.data.staticmodel.ContentType
import com.castcle.usecase.createblog.GetImagePathMapUseCase
import com.castcle.usecase.feed.QuoteCastContentSingleUseCase
import com.castcle.usecase.feed.ReduceAndScaleImageSingleUseCase
import com.castcle.usecase.userprofile.*
import io.reactivex.*
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
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
    private val getImagePathMapUseCase: GetImagePathMapUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
    private val createContentSingleUseCase: CreateContentSingleUseCase,
    private val getCastcleIdSingleUseCase: GetCastcleIdSingleUseCase,
    private val reduceAndScaleImageSingleUseCase: ReduceAndScaleImageSingleUseCase,
    private val quoteCastContentSingleUseCase: QuoteCastContentSingleUseCase
) : CreateBlogFragmentViewModel(), CreateBlogFragmentViewModel.Input {

    private var _userProfileUiModel = MutableLiveData<ContentUiModel>()

    private var _castUserProfile = MutableLiveData<User>()

    private val _showLoading = BehaviorSubject.create<Boolean>()

    private val _error = PublishSubject.create<Throwable>()

    private val _message = BehaviorSubject.create<String>()

    private val _imageContent = BehaviorSubject.create<List<Content>>()

    private val _imageCover = BehaviorSubject.create<String>()

    private val _messageLength = BehaviorSubject.create<Pair<Int, Int>>()
    override val messageLength: Observable<Pair<Int, Int>>
        get() = _messageLength

    override val isGuestMode: Boolean
        get() = getCastcleIdSingleUseCase.execute(Unit).blockingGet()

    override val userProfileUiModel: LiveData<ContentUiModel>
        get() = _userProfileUiModel

    override val input: Input
        get() = this

    private var _mediaItemImage = MutableLiveData<MutableList<MediaItem>>()
    override val mediaItemImage: LiveData<MutableList<MediaItem>>
        get() = _mediaItemImage

    override val onSuccess: Observable<Boolean>
        get() = _onSuccess
    private val _onSuccess = PublishSubject.create<Boolean>()

    override val enableSubmitButton: Observable<Boolean>
        get() = Observables.combineLatest(
            _message,
            _imageContent,
            _imageCover
        ) { message, imageContent, imageCover ->
            if (message.isNotBlank()) {
                message.trim().run {
                    val charLength = Pair(this.length, MAX_LIGHTH - this.length)
                    _messageLength.onNext(charLength)
                }
            }
            when {
                imageContent.isNotEmpty() -> true
                message.isNotEmpty() && (imageContent.isEmpty() || imageCover.isEmpty()) -> true
                else -> false
            }
        }

    override fun createContent(): Single<CreateContentUiModel> {
        val imageSelected = takeImageSelected()
        return if (imageSelected.isNullOrEmpty()) {
            postCreateContent()
        } else {
            reduceAndScaleImageSingleUseCase.execute(
                imageSelected
            ).flatMap(::postCreateContent)
        }
    }

    private fun postCreateContent(imageList: List<Content>): Single<CreateContentUiModel> {
        return createContentSingleUseCase.execute(
            CreateContentRequest(
                type = contentType.blockingFirst().type,
                payload = Payload(
                    message = _message.blockingFirst(),
                    photo = Photo(
                        contents = imageList
                    )
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

    private fun postCreateContent(): Single<CreateContentUiModel> {
        return createContentSingleUseCase.execute(
            CreateContentRequest(
                type = contentType.blockingFirst().type,
                payload = Payload(
                    message = _message.blockingFirst(),
                    photo = Photo(
                        contents = _mediaItemImage.value?.toRequestPhoto() ?: emptyList()
                    )
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
            .fromCallable { message }
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

    override fun fetchImageGallery(): Completable {
        return if (!_mediaItemImage.value.isNullOrEmpty()) {
            Completable.complete()
        } else {
            getImagePathMapUseCase.execute(Unit)
                .doOnError { _error.onNext(it) }
                .doOnSubscribe { _showLoading.onNext(true) }
                .doOnSuccess {
                    setMediaItem(it)
                }
                .doFinally { _showLoading.onNext(false) }
                .ignoreElement()
        }
    }

    override fun setMediaItem(mediaItem: List<MediaItem>) {
        _mediaItemImage.value = mediaItem.toMutableList()
    }

    override fun addMediaItem(mediaItem: MediaItem) {
        val updateImage = _mediaItemImage.value

        if (updateImage.isNullOrEmpty()) {
            updateImage?.add(
                0, MediaItem.OpenCamera(
                    id = "",
                    uri = "",
                    imgRes = R.drawable.ic_camera,
                    displayName = ""
                )
            )
        }
        updateImage?.find { item ->
            item.uri == mediaItem.uri
        }?.apply {
            isSelected = !isSelected
        }?.let {
            setMediaItem(updateImage.toList())
            return
        }.run {
            updateImage?.add(mediaItem)
        }

    }

    override fun updateSelectedImage(id: String) {
        val updateImage = _mediaItemImage.value
        updateImage?.find { item ->
            item.id == id
        }?.apply {
            isSelected = !isSelected
        }?.let {
            if (it.isSelected) {
                _imageContent.onNext(listOf(Content(id)))
            } else {
                _imageContent.onNext(emptyList())
            }
            setMediaItem(updateImage.toList())
        }
    }

    private fun takeImageSelected(): List<String> {
        val updateImage = _mediaItemImage.value
        return updateImage?.filter {
            it.isSelected
        }?.toListUri() ?: emptyList()
    }

    override fun quoteCasteContent(contentUiModel: ContentUiModel) {
        RecastRequest(
            reCasted = contentUiModel.payLoadUiModel.reCastedUiModel.recasted,
            contentId = contentUiModel.payLoadUiModel.contentId,
            authorId = contentUiModel.payLoadUiModel.author.id
        ).run {
            postReCastContent(this)
                .subscribeBy(
                    onError = {
                        _error.onNext(it)
                    }
                ).addToDisposables()
        }
    }

    @SuppressLint("CheckResult")
    private fun postReCastContent(recastRequest: RecastRequest): Completable {
        return quoteCastContentSingleUseCase.execute(
            recastRequest
        ).doOnSuccess {
            _onSuccess.onNext(true)
        }.onErrorReturn {
            Completable.error(it)
            ContentUiModel()
        }.ignoreElement()
    }
}

const val MAX_LIGHTH = 280
