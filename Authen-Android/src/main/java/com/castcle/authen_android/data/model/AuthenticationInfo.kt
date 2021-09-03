package com.castcle.authen_android.data.model

import java.util.*

data class AuthenticationInfo(
    val isDeviceTrusted: Boolean,
    val authenticationMethod: Method
) {
    enum class Method {
        PIN, PASSWORD, OTP, EMAIL;

        companion object {
            fun from(value: String?): Method {
                return try {
                    valueOf(
                        value.orEmpty().toUpperCase(Locale.ROOT)
                    )
                } catch (ex: IllegalArgumentException) {
                    OTP
                }
            }
        }
    }
}
