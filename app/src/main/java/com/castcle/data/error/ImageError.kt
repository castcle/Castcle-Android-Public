package com.castcle.data.error

import androidx.annotation.StringRes
import com.castcle.android.R

sealed class ImageError(
    cause: Throwable?,
    @StringRes readableMessageRes: Int? = null
) : AppError(cause, null, readableMessageRes) {

    class GenerateImageError(cause: Throwable?) : ImageError(cause, R.string.error_get_image)
    class SaveImageError(cause: Throwable?) : ImageError(cause, R.string.error_get_image)
}
