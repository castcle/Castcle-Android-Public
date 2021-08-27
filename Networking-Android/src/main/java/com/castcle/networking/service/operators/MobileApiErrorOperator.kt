package com.castcle.networking.service.operators

import com.castcle.networking.service.errors.UnknownException
import com.castcle.networking.service.exception.ApiException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.*
import io.reactivex.FlowableOperator
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import retrofit2.Response
import java.lang.reflect.Type

class MobileApiErrorOperator<T>
internal constructor() : FlowableOperator<T, Response<T>> {

    override fun apply(subscriber: Subscriber<in T>): Subscriber<in Response<T>> {
        return object : Subscriber<Response<T>> {
            override fun onComplete() {
                subscriber.onComplete()
            }

            override fun onSubscribe(s: Subscription) {
                subscriber.onSubscribe(s)
            }

            override fun onNext(response: Response<T>) {
                if (!response.isSuccessful) {
                    emitError(subscriber, response)
                } else {
                    subscriber.onNext(response.body())
                    subscriber.onComplete()
                }
            }

            override fun onError(throwable: Throwable) {
                subscriber.onError(throwable)
            }
        }
    }

    private fun emitError(subscriber: Subscriber<in T>, response: Response<T>?) {
        try {
            if (response?.message() != null || response?.errorBody() != null) {
                val errorBody = response.errorBody()?.string() ?: ""
                sentLogCrashlytics(response.raw(), errorBody)

                val source = if (response.message().isNullOrEmpty()) {
                    errorBody
                } else {
                    response.message()
                }
                val apiError = try {
                    parseApiError(source)
                } catch (exception: JsonSyntaxException) {
                    parseApiError(errorBody)
                }
                subscriber.onError(
                    ApiException(
                        message = apiError?.message,
                        code = apiError?.code ?: GENERIC_ERROR,
                        type = apiError?.type
                    )
                )
            } else {
                subscriber.onError(UnknownException)
            }
        } catch (exception: Exception) {
            subscriber.onError(exception)
        }
    }

    private fun sentLogCrashlytics(response: okhttp3.Response?, errorBody: String?) {
        FirebaseCrashlytics.getInstance()
            .setCustomKey(
                CRASHLYTICS_NAME_TRACE_ID,
                getResponseHeader(rawResponse = response, key = CRASHLYTICS_KEY_X_TRACE_ID)
            )
        FirebaseCrashlytics.getInstance()
            .setCustomKey(
                CRASHLYTICS_NAME_DEVICE,
                getRequestHeader(rawResponse = response, key = CRASHLYTICS_KEY_X_DEVICE_ID)
            )

        FirebaseCrashlytics.getInstance()
            .setCustomKey(
                CRASHLYTICS_NAME_ERROR_DATA,
                errorBody ?: ""
            )

        FirebaseCrashlytics.getInstance()
            .setCustomKey(
                CRASHLYTICS_NAME_HTTP_STATUS,
                response?.code ?: -1
            )

        FirebaseCrashlytics.getInstance()
            .setCustomKey(CRASHLYTICS_NAME_URL, getRequestURL(response))
    }

    private fun getResponseHeader(rawResponse: okhttp3.Response?, key: String): String {
        return rawResponse?.header(key, "") ?: ""
    }

    private fun getRequestHeader(rawResponse: okhttp3.Response?, key: String): String {
        return rawResponse?.request?.header(key) ?: ""
    }

    private fun getRequestURL(rawResponse: okhttp3.Response?): String {
        return rawResponse?.request?.url.toString()
    }

    private fun parseApiError(source: String): ApiErrorResponse? {
        return source.let {
            val gsonBuilder =
                GsonBuilder().registerTypeAdapter(ApiErrorResponse::class.java, deserializer)
                    .create()
            gsonBuilder.fromJson(source, ApiErrorResponse::class.java)
        }
    }

    private var deserializer = object : JsonDeserializer<ApiErrorResponse> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ApiErrorResponse? {
            json?.let {
                val jsonObject = it.asJsonObject
                val code = jsonObject.get(CODE)?.asString ?: GENERIC_ERROR
                val message = jsonObject.get(MESSAGE)?.asString
                val type = jsonObject.get(TYPE)?.toString()
                return ApiErrorResponse(
                    code = code,
                    message = message,
                    type = type
                )
            }
            return null
        }
    }
}

const val GENERIC_ERROR = "generic error"
const val CODE = "code"
const val MESSAGE = "message"
const val TYPE = "type"

const val CRASHLYTICS_NAME_TRACE_ID = "log-api-x-trace-id"
const val CRASHLYTICS_NAME_URL = "log-api-x-url"
const val CRASHLYTICS_NAME_DEVICE = "log-api-x-device"
const val CRASHLYTICS_NAME_HTTP_STATUS = "log-api-x-http-status"
const val CRASHLYTICS_NAME_ERROR_DATA = "log-api-x-error-data"

const val CRASHLYTICS_KEY_X_TRACE_ID = "x-trace-id"
const val CRASHLYTICS_KEY_X_DEVICE_ID = "X-Device-Id"
