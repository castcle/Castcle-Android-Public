package com.castcle.usecase.base

import com.castcle.data.error.AppError
import okhttp3.internal.http2.StreamResetException
import java.io.IOException

open class BaseUseCase(
    private val defaultError: (Throwable) -> AppError
) {
    protected fun composeError(error: Throwable): Throwable = when (error) {
        is AppError -> error
        else -> defaultError(error)
    }

    /** True if the request was canceled. */
    protected fun isCanceledException(error: Throwable) =
        isCanceledIOException(error) || isCanceledStreamResetException(error)

    private fun isCanceledIOException(error: Throwable) =
        error is IOException && error.message == MESSAGE_REQUEST_CANCELED

    private fun isCanceledStreamResetException(error: Throwable) =
        error is StreamResetException && error.message == MESSAGE_STREAM_WAS_RESET_CANCEL
}

private const val MESSAGE_REQUEST_CANCELED = "Canceled"
private const val MESSAGE_STREAM_WAS_RESET_CANCEL = "stream was reset: CANCEL"
