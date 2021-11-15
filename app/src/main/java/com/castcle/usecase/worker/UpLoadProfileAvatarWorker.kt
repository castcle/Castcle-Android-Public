package com.castcle.usecase.worker

import android.content.Context
import androidx.work.*
import com.castcle.common_model.model.setting.UpLoadType
import com.castcle.common_model.model.setting.domain.CreatePageRequest
import com.castcle.common_model.model.setting.domain.ImagesPageRequest
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.domain.*
import com.castcle.common_model.model.userprofile.toStringModel
import com.castcle.data.error.AppError
import com.castcle.usecase.setting.UpdatePageWorkerSingleUseCase
import com.castcle.usecase.userprofile.ScaleImageSingleUseCase
import com.castcle.usecase.userprofile.UploadProfileAvatarWorkerCompletableUseCase
import com.castcle.usecase.worker.factory.ChildWorkerFactory
import com.castcle.usecase.worker.factory.ImageUploaderWorkHelper
import io.reactivex.Single
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
//  Created by sklim on 20/10/2021 AD at 10:36.

class UpLoadProfileAvatarWorker(
    appContext: Context,
    params: WorkerParameters,
    private val scaleImageSingleUseCase: ScaleImageSingleUseCase,
    private val uploadProfileAvatarWorkerCompletableUseCase: UploadProfileAvatarWorkerCompletableUseCase,
    private val updatePageSingleUseCase: UpdatePageWorkerSingleUseCase
) : Worker(appContext, params) {

    override fun doWork(): Result {
        val updateRequestString = inputData.getString(EXTRA_UPLOAD_AVATAR_PARAM) ?: ""
        val imageUpLoad = updateRequestString.toImageRequestModel()
        val imageToScale = upLoadStateType(imageUpLoad)
        return scaleImageSingleUseCase.execute(imageToScale)
            .flatMap {
                handleStateUplLoad(it, imageUpLoad)
            }
            .doOnError { error ->
                val outputData = buildErrorOutput(error as AppError)
                Result.failure(outputData)
            }
            .map { userResponse ->
                val outputData = buildSuccessOutput(userResponse.toStringModel())
                Result.success(outputData)
            }.blockingGet()
    }

    private fun handleStateUplLoad(it: Content, imageToScale: ImagesRequest): Single<User> {
        return when (imageToScale.upLoadType) {
            UpLoadType.UPLOAD_PAGE_AVATAR.type,
            UpLoadType.UPLOAD_PAGE_COVER.type -> upLoadPageImage(it, imageToScale)
            else -> upLoadImage(it, imageToScale.upLoadType)
        }
    }

    private fun upLoadPageImage(image: Content, upLoadType: ImagesRequest): Single<User> {
        val imageRequest = when (upLoadType.upLoadType) {
            UpLoadType.UPLOAD_PAGE_AVATAR.type -> {
                ImagesPageRequest(
                    avatar = image.image ?: ""
                )
            }
            else -> ImagesPageRequest(
                cover = image.image ?: ""
            )
        }
        val pageUpdateRequest = CreatePageRequest(
            images = imageRequest,
            castcleId = upLoadType.castcleId ?: ""

        )

        return updatePageSingleUseCase.execute(pageUpdateRequest)
    }

    private fun upLoadImage(image: Content, upLoadType: String): Single<User> {
        val imageRequest = when (upLoadType) {
            UpLoadType.UPLOAD_AVATAR.type -> {
                ImagesRequest(avatar = image.image)
            }
            UpLoadType.UPLOAD_COVER.type -> {
                ImagesRequest(cover = image.image)
            }
            else -> ImagesRequest(avatar = image.image)
        }
        return uploadProfileAvatarWorkerCompletableUseCase.execute(
            UserUpdateRequest(images = imageRequest)
        )
    }

    private fun upLoadStateType(imageUpLoad: ImagesRequest): String {
        return when (imageUpLoad.upLoadType) {
            UpLoadType.UPLOAD_COVER.type, UpLoadType.UPLOAD_PAGE_COVER.type -> {
                imageUpLoad.cover ?: ""
            }
            UpLoadType.UPLOAD_AVATAR.type, UpLoadType.UPLOAD_PAGE_AVATAR.type -> {
                imageUpLoad.avatar ?: ""
            }
            else -> imageUpLoad.avatar ?: ""
        }
    }

    private fun buildSuccessOutput(profileImageUrl: String): Data {
        val status = ImageUploaderWorkHelper.Result.SUCCESS.ordinal

        return workDataOf(
            ImageUploaderWorkHelper.EXTRA_UPLOAD_RESULT to status,
            ImageUploaderWorkHelper.EXTRA_UPLOAD_URL_RESULT to profileImageUrl
        )
    }

    private fun buildErrorOutput(error: AppError): Data {
        val status = ImageUploaderWorkHelper.Result.FAILURE.ordinal
        val errorMessageRes = error.readableMessageRes

        return workDataOf(
            ImageUploaderWorkHelper.EXTRA_UPLOAD_RESULT to status,
            ImageUploaderWorkHelper.EXTRA_UPLOAD_ERROR_RESULT to errorMessageRes
        )
    }

    class Factory @Inject constructor(
        private val scaleImageSingleUseCase: ScaleImageSingleUseCase,
        private val uploadProfileAvatarWorkerCompletableUseCase: UploadProfileAvatarWorkerCompletableUseCase,
        private val updatePageSingleUseCase: UpdatePageWorkerSingleUseCase
    ) : ChildWorkerFactory {

        override fun create(
            appContext: Context,
            workerParams: WorkerParameters
        ): ListenableWorker {
            return UpLoadProfileAvatarWorker(
                appContext, workerParams,
                scaleImageSingleUseCase,
                uploadProfileAvatarWorkerCompletableUseCase,
                updatePageSingleUseCase
            )
        }
    }

    companion object {
        fun provideInputData(imagePath: String): Data =
            workDataOf(
                EXTRA_UPLOAD_AVATAR_PARAM to imagePath,
            )
    }
}

private const val EXTRA_UPLOAD_AVATAR_PARAM = "EXTRA_UPLOAD_AVATAR_PARAM"
