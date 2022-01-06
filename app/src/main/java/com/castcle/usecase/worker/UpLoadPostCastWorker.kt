package com.castcle.usecase.worker

import android.content.Context
import androidx.work.*
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.feed.toModelString
import com.castcle.common_model.model.userprofile.domain.*
import com.castcle.data.error.AppError
import com.castcle.usecase.createblog.ScaleImagesSingleUseCase
import com.castcle.usecase.userprofile.CreateContentWorkerSingleUseCase
import com.castcle.usecase.worker.factory.*
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

class UpLoadPostCastWorker(
    appContext: Context,
    params: WorkerParameters,
    private val scaleImageSingleUseCase: ScaleImagesSingleUseCase,
    private val uploadCastWithImageWorker: CreateContentWorkerSingleUseCase
) : Worker(appContext, params) {

    override fun doWork(): Result {
        val updateRequestString = inputData.getString(EXTRA_UPLOAD_POST_PARAM) ?: ""
        val createContentRequest = updateRequestString.toCreateContentRequest()
        val imageCast = createContentRequest.payload.photo?.contents?.map {
            it.image ?: ""
        } ?: emptyList()

        return scaleImageSingleUseCase.execute(imageCast)
            .flatMap {
                upLoadPostWithImage(it, createContentRequest)
            }.doOnError { error ->
                val outputData = buildErrorOutput(error as AppError)
                Result.failure(outputData)
            }.map { userResponse ->
                val outputData = buildSuccessOutput(userResponse.toModelString())
                Result.success(outputData)
            }.blockingGet()
    }

    private fun upLoadPostWithImage(
        list: List<Content>,
        createPost: CreateContentRequest
    ): Single<ContentFeedUiModel> {
        val contentRequest = createPost.apply {
            payload.photo?.contents = list
        }
        return uploadCastWithImageWorker.execute(contentRequest)
    }

    private fun buildSuccessOutput(profileImageUrl: String): Data {
        val status = CastWithImageLoadWorkHelper.Result.SUCCESS.ordinal

        return workDataOf(
            CastWithImageLoadWorkHelper.EXTRA_UPLOAD_RESULT to status,
            CastWithImageLoadWorkHelper.EXTRA_UPLOAD_URL_RESULT to profileImageUrl
        )
    }

    private fun buildErrorOutput(error: AppError): Data {
        val status = CastWithImageLoadWorkHelper.Result.FAILURE.ordinal
        val errorMessageRes = error.readableMessageRes

        return workDataOf(
            CastWithImageLoadWorkHelper.EXTRA_UPLOAD_RESULT to status,
            CastWithImageLoadWorkHelper.EXTRA_UPLOAD_ERROR_RESULT to errorMessageRes
        )
    }

    class Factory @Inject constructor(
        private val scaleImageSingleUseCase: ScaleImagesSingleUseCase,
        private val uploadCastWithImageWorker: CreateContentWorkerSingleUseCase
    ) : ChildWorkerFactory {

        override fun create(
            appContext: Context,
            workerParams: WorkerParameters
        ): UpLoadPostCastWorker {
            return UpLoadPostCastWorker(
                appContext, workerParams,
                scaleImageSingleUseCase,
                uploadCastWithImageWorker
            )
        }
    }

    companion object {
        //CreateContentRequest request create post
        fun provideInputData(messagePost: String): Data =
            workDataOf(
                EXTRA_UPLOAD_POST_PARAM to messagePost,
            )
    }
}

private const val EXTRA_UPLOAD_POST_PARAM = "EXTRA_UPLOAD_POST_PARAM"
