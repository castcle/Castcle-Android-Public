package com.castcle.localization

import android.app.Activity
import android.content.res.Configuration
import androidx.annotation.*
import androidx.core.os.ConfigurationCompat
import com.castcle.android.R
import com.castcle.extensions.toUrlScheme
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

interface LocalizedResources {

    fun getText(@StringRes stringRes: Int): CharSequence

    fun getString(@StringRes stringRes: Int): String

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int): String

    fun getString(@StringRes resId: Int, vararg params: Any): String

    fun getStringList(@ArrayRes resId: Int): List<String>

    fun getCorrespondingSystemLocaleCode(): String

    fun buildCastcleUrl(@StringRes path: Int): String

    fun getUrl(@StringRes stringRes: Int): String

    fun isLocalOf(localName: String): Boolean

    fun getAnalyticString(@StringRes stringRes: Int, language: String = LANGUAGE_CODE_EN): String
}

class LocalizedResourcesImpl @Inject constructor(
    private val activity: Activity
) : LocalizedResources {

    private val activityWeakReference: WeakReference<Activity> by lazy { WeakReference(activity) }

    override fun getText(stringRes: Int): CharSequence =
        activityWeakReference.get()?.getText(stringRes) ?: ""

    override fun getString(@StringRes stringRes: Int): String =
        activityWeakReference.get()?.getString(stringRes) ?: ""

    override fun getString(@StringRes resId: Int, vararg params: Any): String {
        return activityWeakReference.get()?.getString(resId, *params) ?: ""
    }

    override fun getQuantityString(@PluralsRes resId: Int, quantity: Int): String {
        return activityWeakReference.get()?.resources?.getQuantityString(resId, quantity, quantity)
            ?: ""
    }

    override fun getStringList(resId: Int): List<String> {
        return activityWeakReference.get()?.resources?.getStringArray(resId)?.toList()
            ?: emptyList()
    }

    /**
     * Returns the corresponding application locale code that we support.
     *
     * @return: [LANGUAGE_CODE_TH] if the system is currently serving Thai;
     * [LANGUAGE_CODE_EN] as fallback.
     */
    override fun getCorrespondingSystemLocaleCode(): String {
        val activity = activityWeakReference.get()
        return if (activity != null) {
            val contextLanguage = ConfigurationCompat.getLocales(
                activity.resources.configuration
            ).get(0).language
            return when (contextLanguage) {
                LANGUAGE_CODE_TH -> contextLanguage
                else -> LANGUAGE_CODE_EN
            }
        } else {
            LANGUAGE_CODE_EN
        }
    }

    override fun buildCastcleUrl(@StringRes path: Int): String {
        val scheme = getString(R.string.link_scheme).toUrlScheme()
        val host = getString(R.string.deep_link_host)

        return "$scheme$host${getString(path)}"
    }

    override fun getUrl(stringRes: Int): String {
        return if (getCorrespondingSystemLocaleCode() == LANGUAGE_CODE_EN) {
            getString(stringRes, "/$LANGUAGE_CODE_EN")
        } else {
            getString(stringRes, EMPTY)
        }
    }

    override fun isLocalOf(localName: String): Boolean {
        return getCorrespondingSystemLocaleCode() == localName
    }

    override fun getAnalyticString(stringRes: Int, language: String): String {
        return activityWeakReference.get()?.let {
            val config = Configuration(it.resources.configuration)
            config.setLocale(Locale(language))
            it.createConfigurationContext(config).getString(stringRes)
        } ?: getString(stringRes)
    }
}

const val LANGUAGE_CODE_TH = "th"
const val LANGUAGE_CODE_EN = "en"

private const val EMPTY = ""
