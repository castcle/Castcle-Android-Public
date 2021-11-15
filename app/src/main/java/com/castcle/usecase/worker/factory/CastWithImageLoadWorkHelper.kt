package com.castcle.usecase.worker.factory

import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.castcle.common_model.model.userprofile.domain.CreateContentRequest
import com.castcle.common_model.model.userprofile.domain.toStringModel
import com.castcle.usecase.worker.WorkRequestBuilder
import com.castcle.usecase.worker.factory.CastWithImageLoadWorkHelper.Companion.EXTRA_UPLOAD_ERROR_RESULT
import com.castcle.usecase.worker.factory.CastWithImageLoadWorkHelper.Companion.EXTRA_UPLOAD_RESULT
import com.castcle.usecase.worker.factory.CastWithImageLoadWorkHelper.Companion.EXTRA_UPLOAD_URL_RESULT
import io.reactivex.Observable
import javax.inject.Inject

interface CastWithImageLoadWorkHelper {

    companion object {
        const val UPLOAD_CAST_WITH_IMAGE_TAG = "UPLOAD_CAST_WITH_IMAGE_TAG"
        const val EXTRA_UPLOAD_RESULT = "EXTRA_UPLOAD_RESULT"
        const val EXTRA_UPLOAD_URL_RESULT = "EXTRA_UPLOAD_URL_RESULT"
        const val EXTRA_UPLOAD_ERROR_RESULT = "EXTRA_UPLOAD_ERROR_RESULT"
    }

    sealed class Status {
        class Error(@StringRes val error: Int?) : Status()

        object Uploading : Status()

        class Success(val castResponse: String) : Status()
    }

    enum class Result {
        SUCCESS,
        FAILURE
    }

    val uploadStatus: Observable<Status>

    fun uploadPost(messagePost: CreateContentRequest)
}

class CastWithImageLoadWorkHelperImpl @Inject constructor(
    private val workManager: WorkManager,
    private val workRequestBuilder: WorkRequestBuilder
) : CastWithImageLoadWorkHelper {

    override val uploadStatus: Observable<CastWithImageLoadWorkHelper.Status> =
        Observable.create<CastWithImageLoadWorkHelper.Status> { emitter ->
            val statusesByTagLiveData = workManager.getWorkInfosByTagLiveData(
                CastWithImageLoadWorkHelper.UPLOAD_CAST_WITH_IMAGE_TAG
            )
            val observer: Observer<List<WorkInfo>> = Observer { workInfo ->
                workInfo.lastOrNull()?.let { info ->
                    var status: CastWithImageLoadWorkHelper.Status? = null
                    if (info.state == WorkInfo.State.RUNNING) {
                        status = CastWithImageLoadWorkHelper.Status.Uploading
                    }
                    if (info.state.isFinished) {
                        val result = CastWithImageLoadWorkHelper.Result.values()[
                            info.outputData.getInt(
                                EXTRA_UPLOAD_RESULT,
                                CastWithImageLoadWorkHelper.Result.SUCCESS.ordinal
                            )
                        ]
                        status = when (result) {
                            CastWithImageLoadWorkHelper.Result.SUCCESS -> {
                                val profileImageUrl =
                                    info.outputData.getString(EXTRA_UPLOAD_URL_RESULT)
                                CastWithImageLoadWorkHelper.Status.Success(profileImageUrl ?: "")
                            }

                            CastWithImageLoadWorkHelper.Result.FAILURE -> {
                                val errorResult = info.outputData.getInt(
                                    EXTRA_UPLOAD_ERROR_RESULT, 0
                                )
                                CastWithImageLoadWorkHelper.Status.Error(errorResult.takeIf { it != 0 })
                            }
                        }
                        workManager.pruneWork()
                    }
                    status?.let(emitter::onNext)
                }
            }
            emitter.setCancellable { statusesByTagLiveData.removeObserver(observer) }
            statusesByTagLiveData.observeForever(observer)
        }.share()

    override fun uploadPost(messagePost: CreateContentRequest) {
        workManager.enqueue(
            workRequestBuilder.buildPostWithImageWorkerRequest(messagePost.toStringModel())
        )
    }
}
