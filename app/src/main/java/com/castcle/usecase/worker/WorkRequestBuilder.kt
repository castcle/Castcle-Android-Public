package com.castcle.usecase.worker

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkRequest
import com.castcle.usecase.worker.factory.CastWithImageLoadWorkHelper
import com.castcle.usecase.worker.factory.ImageUploaderWorkHelper
import javax.inject.Inject

interface WorkRequestBuilder {

    fun buildUpdateProfileAvatarWorkerRequest(filePath: String): WorkRequest
    fun buildPostWithImageWorkerRequest(postMessage: String): WorkRequest
}

class WorkRequestBuilderImpl @Inject constructor() : WorkRequestBuilder {

    override fun buildUpdateProfileAvatarWorkerRequest(
        filePath: String
    ): WorkRequest {
        return OneTimeWorkRequest.Builder(UpLoadProfileAvatarWorker::class.java)
            .setInputData(UpLoadProfileAvatarWorker.provideInputData(filePath))
            .addTag(ImageUploaderWorkHelper.UPLOAD_IMAGE_TAG)
            .build()
    }

    override fun buildPostWithImageWorkerRequest(postMessage: String): WorkRequest {
        return OneTimeWorkRequest.Builder(UpLoadPostCastWorker::class.java)
            .setInputData(UpLoadPostCastWorker.provideInputData(postMessage))
            .addTag(CastWithImageLoadWorkHelper.UPLOAD_CAST_WITH_IMAGE_TAG)
            .build()
    }
}
