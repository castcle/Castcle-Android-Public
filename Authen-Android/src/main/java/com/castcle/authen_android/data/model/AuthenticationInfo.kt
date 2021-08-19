package com.castcle.authen_android.data.model

import java.util.*

data class AuthenticationInfo(
    val isDeviceTrusted: Boolean,
    val authenticationMethod: Method
) {
    enum class Method {
        PIN, PASSWORD, OTP;

        companion object {
            fun from(value: String?): Method {
                return try {
                    valueOf(
                        value.orEmpty().uppercase(Locale.ROOT)
                    )
                } catch (ex: IllegalArgumentException) {
                    OTP
                }
            }
        }
    }
}
