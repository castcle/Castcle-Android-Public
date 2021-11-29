package com.castcle.ui.createpost

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.android.R
import com.castcle.common_model.model.createblog.MediaItem
import com.castcle.common_model.model.createblog.toListContent
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.userprofile.MentionUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.domain.*
import com.castcle.data.staticmodel.ContentType
import com.castcle.usecase.createblog.GetImagePathMapUseCase
import com.castcle.usecase.createblog.ScaleImagesSingleUseCase
import com.castcle.usecase.feed.QuoteCastContentSingleUseCase
import com.castcle.usecase.userprofile.*
import io.reactivex.*
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import kotlin.math.abs

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

class CreatePostFragmentViewModelImpl @Inject constructor(
    private val getImagePathMapUseCase: GetImagePathMapUseCase,
    private val cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase,
    private val createContentSingleUseCase: CreateContentSingleUseCase,
    private val isGuestModeSingleUseCase: IsGuestModeSingleUseCase,
    private val quoteCastContentSingleUseCase: QuoteCastContentSingleUseCase,
    private val getUserMentionSingleUseCase: GetUserMentionSingleUseCase,
    private val scaleImageSingleUseCase: ScaleImagesSingleUseCase,
    private val uploadCastWithImageWorkerCompletableUseCase: UploadCastWithImageWorkerCompletableUseCase
) : CreatePostFragmentViewModel(), CreatePostFragmentViewModel.Input {

    private var _userProfileUiModel = MutableLiveData<ContentUiModel>()

    private var _castUserProfile = MutableLiveData<User>()

    private val _showLoading = BehaviorSubject.create<Boolean>()
    override val showLoading: Observable<Boolean>
        get() = _showLoading

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    private val _message = BehaviorSubject.create<String>()

    private val _imageContent = BehaviorSubject.create<List<Content>>()

    private val _imageCover = BehaviorSubject.create<String>()

    private val _messageLength = BehaviorSubject.create<Pair<Int, Int>>()
    override val messageLength: Observable<Pair<Int, Int>>
        get() = _messageLength

    override val isGuestMode: Boolean
        get() = isGuestModeSingleUseCase.execute(Unit).blockingGet()

    override val userProfileUiModel: LiveData<ContentUiModel>
        get() = _userProfileUiModel

    override val input: Input
        get() = this

    private var _mediaItemImage = MutableLiveData<MutableList<MediaItem>>()
    override val mediaItemImage: LiveData<MutableList<MediaItem>>
        get() = _mediaItemImage

    private var _imageReSized = MutableLiveData<MutableList<Content>>()

    private var _mediaImageSelected = MutableLiveData<MutableList<MediaItem>>()
    override val mediaImageSelected: LiveData<MutableList<MediaItem>>
        get() = _mediaImageSelected

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
                    val charLength = Pair(this.length, abs(MAX_LIGHTH - this.length))
                    _messageLength.onNext(charLength)
                }
            } else {
                _messageLength.onNext(Pair(0, MAX_LIGHTH))
            }
            when {
                imageContent.isNotEmpty() -> true
                (message.isNotEmpty() && message.length <= MAX_LIGHTH) &&
                    (imageContent.isEmpty() || imageCover.isEmpty()) -> true
                else -> false
            }
        }

    override fun createContent(): Completable {
        val imageSelected = _mediaImageSelected.value?.toListContent() ?: mutableListOf()
        return postCreateContent(imageSelected)
    }

    private fun postCreateContent(imageList: List<Content>): Completable {
        return uploadCastWithImageWorkerCompletableUseCase.execute(
            CreateContentRequest(
                type = ContentType.SHORT.type,
                payload = Payload(
                    message = _message.value ?: "",
                    photo = Photo(
                        contents = imageList
                    )
                ),
                castcleId = _castUserProfile.value?.castcleId ?: "",
                createType = ContentType.FEED.type
            )
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.doFinally {
            _showLoading.onNext(false)
        }
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

    override fun validateImageCover(imageCover: String) {
    }

    override fun fetchCastUserProfile(): Completable {
        return cachedUserProfileSingleUseCase
            .execute(Unit)
            .doOnNext {
                _castUserProfile.value = it.get()
                _userProfileUiModel.value = it.get().toContentUiModel()
            }.doOnError(_error::onNext).firstOrError()
            .ignoreElement()
    }

    override fun fetchImageGallery(): Completable {
        return if (!_mediaItemImage.value.isNullOrEmpty()) {
            Completable.complete()
        } else {
            getImagePathMapUseCase.execute(Unit)
                .doOnError { _error.onNext(it) }
                .doOnSuccess {
                    setMediaItem(it)
                }.ignoreElement()
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
                    displayName = "",
                    path = ""
                )
            )
        }
        updateImage?.find { item ->
            item.uri == mediaItem.uri
        }?.apply {
            isSelected = !isSelected
        }?.let {
            setMediaItem(updateImage.toList())
            setImageSelected()
            return
        }.run {
            setImageSelected()
            updateImage?.add(mediaItem)
        }
    }

    override fun removeMediaItem(mediaItem: MediaItem) {
        val removeItem = _mediaImageSelected.value
        removeItem?.find { itemUpdate ->
            itemUpdate.uri == mediaItem.uri
        }?.let {
            removeItem.remove(it)
            updateImageSelected(removeItem)
        }
        val updateImage = _mediaItemImage.value
        updateImage?.find { itemUpdate ->
            itemUpdate.uri == mediaItem.uri
        }?.apply {
            isSelected = !isSelected
        }?.let {
            setMediaItem(updateImage.toList())
        }
    }

    override fun addMediaItemSelected(mediaItem: List<MediaItem>) {
        val updateImage = _mediaItemImage.value

        mediaItem.forEach {
            updateImage?.find { item ->
                item.uri == it.uri
            }?.apply {
                isSelected = true
            }?.let {
                setMediaItem(updateImage.toList())
                setImageSelected()
                return
            }
        }
        updateImage?.apply {
            addAll(mediaItem)
        }.run {
            this?.let { setMediaItem(it) }
            setImageSelected()
        }
    }

    private fun updateImageSelected(removeItem: MutableList<MediaItem>) {
        _mediaImageSelected.value = removeItem
    }

    override fun updateSelectedImage(item: MediaItem.ImageMediaItem) {
        addMediaItem(item)
    }

    private fun setImageSelected() {
        val updateImage = _mediaItemImage.value
        updateImage?.filter {
            it.isSelected
        }?.let { it ->
            _mediaImageSelected.value = it.toMutableList()
        }
    }

    private fun onScaleImageSelected(imageSelected: MutableList<MediaItem>) {
        if (imageSelected.isNotEmpty()) {
            scaleImageSingleUseCase.execute(imageSelected.map {
                it.uri
            }).subscribeBy {
                _imageReSized.value = it.toMutableList()
            }.addToDisposables()
        } else {
            _imageReSized.value = mutableListOf()
        }
    }

    override fun quoteCasteContent(
        contentUiModel: ContentUiModel,
        castcleId: String
    ) {
        RecastRequest(
            reCasted = contentUiModel.payLoadUiModel.reCastedUiModel.recasted,
            contentId = contentUiModel.payLoadUiModel.contentId,
            authorId = castcleId
        ).run {
            postReCastContent(this)
                .subscribeBy(
                    onSuccess = {
                        _onSuccess.onNext(true)
                    },
                    onError = {
                        _error.onNext(it)
                        _showLoading.onNext(true)
                    }
                ).addToDisposables()
        }
    }

    @SuppressLint("CheckResult")
    private fun postReCastContent(recastRequest: RecastRequest): Single<ContentUiModel> {
        return quoteCastContentSingleUseCase.execute(
            recastRequest
        ).doOnSubscribe {
            _showLoading.onNext(true)
        }.doFinally {
            _showLoading.onNext(false)
        }
    }

    override fun onClearState() {
        _messageLength.onNext(Pair(0, MAX_LIGHTH))
        _imageReSized.value = mutableListOf()
        setMediaItem(emptyList())
        updateImageSelected(mutableListOf())
    }

    override fun getUserMention(keyword: String): Single<List<MentionUiModel>> {
        return getUserMentionSingleUseCase
            .execute(MentionRequest(keyword = keyword))
            .map { it.userMention }
    }

    override fun setUserProfile(user: User) {
        _castUserProfile.value = user
    }
}

const val MAX_LIGHTH = 280
const val LIMIT_IMAGE_SELECTED = 4
