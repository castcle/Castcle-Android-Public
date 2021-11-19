package com.castcle.networking.service.interceptor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.MODEL
import android.os.Build.VERSION
import android.provider.Settings
import android.util.Log
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.networking.service.common.*
import com.castcle.session_memory.SessionManagerRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
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
            val token = sessionManagerRepository?.getSessionToken()?.token ?: ""

            if (isAuthenticationInvalid(token) && secureStorage != null) {
                FirebaseCrashlytics.getInstance()
                    .setCustomKey(
                        LOG_DEVICE_ID,
                        getDeviceId()
                    )

                val accessTokenStorage = secureStorage.userAccessToken

                if (accessTokenStorage == null) {
                    request.removeHeader(AUTHORIZATION_HEADER)
                } else {
                    request.addHeader(
                        AUTHORIZATION_HEADER,
                        "$TOKEN_TYPE $accessTokenStorage"
                    )
                }
            } else {
                if (token.isNotEmpty()) {
                    request.addHeader(AUTHORIZATION_HEADER, "$TOKEN_TYPE $token")
                }
            }
        }

        return chain.proceed(request.build())
    }

    private fun isAuthenticationInvalid(token: String): Boolean = token.isEmpty()

    private fun createNewRequest(request: Request): Request.Builder {
        val languageCode =
            sessionManagerRepository?.getLanguageCode() ?: Locale.getDefault().language
        return request
            .newBuilder()
            .addHeader(ACCEPT_VERSION, DEFAULT_ACCEPT_VERSION)
            .addHeader(CONTENT_TYPE_HEADER, HTTP_HEADER_CONTENT_TYPE_APPLICATION_JSON)
            .addHeader(ACCEPT_LANGUAGE_HEADER, languageCode)
            .addHeader(DEVICE_PLATFORM, ANDROID.plus(" ").plus(VERSION.RELEASE))
            .addHeader(DEVICE_MODEL, MODEL)
            .addHeader(API_META, "ip=${getIPAddress()},$SRC,$DEST")
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    private fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("IP Address", ex.toString())
        }
        return null
    }

    private fun getIPAddress(useIPv4: Boolean = true): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4: Boolean = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                if (delim < 0) {
                                    return sAddr.toUpperCase()
                                } else {
                                    return sAddr.substring(0, delim).toUpperCase()
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
        }
        return ""
    }

}

private const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
private const val ACCEPT_VERSION = "Accept-Version"
private const val DEFAULT_ACCEPT_VERSION = "1.0"
private const val DEVICE_PLATFORM = "Platform"
private const val DEVICE_MODEL = "Device"
private const val X_DEVICE_ID = "X-Device-Id"
private const val ANDROID = "Android"
private const val TOKEN_TYPE = "Bearer"
private const val API_META = "API-Metadata"
private const val SRC = "src=web"
private const val DEST = "dest=castcle"

const val LOG_DEVICE_ID = "log-api-token-empty-device-id"
