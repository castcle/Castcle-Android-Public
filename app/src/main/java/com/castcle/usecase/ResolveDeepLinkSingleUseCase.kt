package com.castcle.usecase

import android.content.Context
import android.net.Uri
import com.castcle.android.R
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.data.error.Ignored
import com.castcle.usecase.ResolveDeepLinkSingleUseCase.DeepLinkTarget.UNDEFINED
import com.castcle.usecase.ResolveDeepLinkSingleUseCase.DeepLinkTarget.values
import com.castcle.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class ResolveDeepLinkSingleUseCase @Inject constructor(
    schedulerProvider: RxSchedulerProvider,
    private val context: Context
) : SingleUseCase<Uri?, ResolveDeepLinkSingleUseCase.DeepLinkTarget>(
    schedulerProvider.io(),
    schedulerProvider.main(),
    ::Ignored
) {

    private val castcleScheme by lazy {
        context.getString(R.string.deep_link_scheme_castcle)
    }

    private val scheme by lazy {
        context.getString(R.string.link_scheme)
    }

    private val host by lazy {
        context.getString(R.string.deep_link_host)
    }

    override fun create(input: Uri?): Single<DeepLinkTarget> {
        return Single.fromCallable {
            when {
                validateUriHttps(input) -> {
                    mapPathToTarget(input?.path ?: "")
                }
                validateUriThe1(input) -> {
                    val uriPath = StringBuilder()
                        .append("/")
                        .append(input?.host ?: "")
                        .append(input?.path ?: "")
                        .toString()
                    mapPathToTarget(uriPath)
                }
                else -> {
                    UNDEFINED
                }
            }
        }
    }

    private fun validateUriHttps(uri: Uri?) = uri != null
        && uri.scheme == scheme
        && uri.host == host
        && !uri.path.isNullOrEmpty()

    private fun validateUriThe1(uri: Uri?) = uri != null
        && uri.scheme == castcleScheme
        && !uri.host.isNullOrEmpty()

    private fun mapPathToTarget(path: String): DeepLinkTarget {
        return values().find { target ->
            path.contains(target.resource)
        } ?: UNDEFINED
    }

    enum class DeepLinkTarget(val resource: String) {
        HOME_FEED("home-feed"),
        UNDEFINED("undefined")
    }
}
