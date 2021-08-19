package com.castcle.networking_android.service.interceptor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.MODEL
import android.os.Build.VERSION
import android.provider.Settings
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.networking_android.service.common.AUTHORIZATION_HEADER
import com.castcle.session_memory.SessionManagerRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.*
import java.io.IOException
import java.util.*

internal class ApplicationRequestInterceptor(
    private val context: Context,
    private val sessionManagerRepository: SessionManagerRepository?,
    private val requiredAuthenticated: Boolean = false,
    private val secureStorage: SecureStorage?
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = createNewRequest(chain.request())

        if (requiredAuthenticated) {
            val tokenType = sessionManagerRepository?.getSessionToken()?.tokenType ?: ""
            val token = sessionManagerRepository?.getSessionToken()?.token ?: ""

            if (isAuthenticationInvalid(token, tokenType) && secureStorage != null) {
                FirebaseCrashlytics.getInstance()
                    .setCustomKey(
                        LOG_DEVICE_ID,
                        getDeviceId()
                    )

                val tokenTypeStorage = secureStorage.userTokenType
                val accessTokenStorage = secureStorage.userAccessToken

                if (tokenTypeStorage == null && accessTokenStorage == null) {
                    request.removeHeader(AUTHORIZATION_HEADER)
                } else {
                    request.addHeader(
                        AUTHORIZATION_HEADER,
                        "$tokenTypeStorage $accessTokenStorage"
                    )
                }
            } else {
                if (tokenType.isNotEmpty() || token.isNotEmpty()) {
                    request.addHeader(AUTHORIZATION_HEADER, "$tokenType $token")
                }
            }
        }

        return chain.proceed(request.build())
    }

    private fun isAuthenticationInvalid(token: String, tokenType: String): Boolean =
        token.isEmpty() || tokenType.isEmpty()

    private fun createNewRequest(request: Request): Request.Builder {
        val languageCode =
            sessionManagerRepository?.getLanguageCode() ?: Locale.getDefault().language
        return request
            .newBuilder()
            .addHeader(ACCEPT_LANGUAGE_HEADER, languageCode)
            .addHeader(X_DEVICE_PLATFORM, ANDROID.plus(" ").plus(VERSION.RELEASE))
            .addHeader(X_DEVICE_MODEL, MODEL)
            .addHeader(X_DEVICE_ID, getDeviceId())
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}

private const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
private const val X_DEVICE_PLATFORM = "X-Device-Platform"
private const val X_DEVICE_MODEL = "X-Device-Model"
private const val X_DEVICE_ID = "X-Device-Id"
private const val ANDROID = "Android"

const val LOG_DEVICE_ID = "log-api-token-empty-device-id"
