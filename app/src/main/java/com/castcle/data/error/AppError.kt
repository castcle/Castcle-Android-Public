package com.castcle.data.error

import android.content.Context
import androidx.annotation.StringRes
import com.castcle.android.R
import com.castcle.networking.service.exception.ApiException

open class AppError(
    cause: Throwable?,
    open val readableMessage: String? = null,
    @StringRes open val readableMessageRes: Int? = null
) : Throwable(cause) {

    open val code: String?
        get() = (cause as? ApiException)?.code
}

fun getErrorMessage(cause: Throwable?): String? {
    return (cause as? ApiException)?.message
}

class Ignored(cause: Throwable?) : AppError(cause, null, null)

fun Throwable.userReadableMessage(context: Context): String {
    return when (this) {
        is AppError -> {
            readableMessage ?: context.getString(readableMessageRes ?: R.string.error_generic)
        }
        else -> context.getString(R.string.error_generic)
    }
}

val Throwable.errorCode: String?
    get() = (this as AppError).code

const val CODE_USER_ACCOUNT_NOT_FOUND = "user_account_not_found"
