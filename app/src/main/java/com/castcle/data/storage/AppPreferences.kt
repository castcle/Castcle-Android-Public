package com.castcle.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import io.reactivex.Completable
import javax.inject.Inject

interface AppPreferences {
    var language: String?
    var preferredLanguage: String?
    var appTheme: Int?
    var cameraPermissionDenied: Boolean?
    var galleryPermissionDenied: Boolean?
    var locationPermissionDenied: Boolean?
    var appVersion: Int?
    var bottomNavigation: String?
    var castcleId: String?
    var email: String?

    fun clearAll() = Completable.fromAction {
        castcleId = null
        preferredLanguage = null
    }
}

class AppPreferencesImpl @Inject constructor(
    private val preferences: SharedPreferences
) : AppPreferences {

    override var language: String?
        get() = getString(KEY_LANGUAGE)
        set(language) = setOrRemove(KEY_LANGUAGE, language)

    override var preferredLanguage: String?
        get() = getString(KEY_LANGUAGE_PREFERRED)
        set(language) = setOrRemove(KEY_LANGUAGE_PREFERRED, language)

    override var appTheme: Int?
        get() = getInt(KEY_THEME)
        set(theme) = setOrRemove(KEY_THEME, theme)

    override var cameraPermissionDenied: Boolean?
        get() = getBoolean(KEY_CAMERA_PERMISSION)
        set(value) = setOrRemove(KEY_CAMERA_PERMISSION, value)

    override var galleryPermissionDenied: Boolean?
        get() = getBoolean(KEY_GALLERY_PERMISSION)
        set(value) = setOrRemove(KEY_GALLERY_PERMISSION, value)

    override var locationPermissionDenied: Boolean?
        get() = getBoolean(KEY_LOCATION_PERMISSION)
        set(value) = setOrRemove(KEY_LOCATION_PERMISSION, value)

    override var appVersion: Int?
        get() = getInt(KEY_APP_VERSION)
        set(value) = setOrRemove(KEY_APP_VERSION, value)

    override var bottomNavigation: String?
        get() = getString(KEY_BOTTOM_NAVIGATION)
        set(value) = setOrRemove(KEY_BOTTOM_NAVIGATION, value)

    override var castcleId: String?
        get() = getString(KEY_MEMBER_ID)
        set(value) = setOrRemove(KEY_MEMBER_ID, value)

    override var email: String?
        get() = getString(KEY_EMAIL)
        set(value) = setOrRemove(KEY_EMAIL, value)

    private fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    private fun getInt(key: String): Int? {
        if (!preferences.contains(key)) {
            return null
        }
        return preferences.getInt(key, 0)
    }

    private fun getBoolean(key: String): Boolean? {
        if (!preferences.contains(key)) {
            return null
        }
        if (key == KEY_CALL_ALLOWED_MAIN_ACTIVITY || key == KEY_CALL_ALLOWED_ONBOARDING_ACTIVITY) {
            return preferences.getBoolean(key, true)
        }
        return preferences.getBoolean(key, false)
    }

    private fun getLong(key: String): Long? {
        if (!preferences.contains(key)) {
            return null
        }
        return preferences.getLong(key, 0)
    }

    private fun setOrRemove(key: String, value: Any?) {
        preferences.edit {
            when (value) {
                null -> remove(key)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported Type")
            }
        }
    }
}

private const val KEY_LANGUAGE = "language"
private const val KEY_LANGUAGE_PREFERRED = "language-preferred"
private const val KEY_THEME = "app_theme"
private const val KEY_CAMERA_PERMISSION = "camera_permission"
private const val KEY_GALLERY_PERMISSION = "gallery_permission"
private const val KEY_LOCATION_PERMISSION = "location_permission"
private const val KEY_MEMBER_ID = "member_id"
private const val KEY_EMAIL = "email_id"
const val KEY_APP_VERSION = "app_version"
const val KEY_BOTTOM_NAVIGATION = "bottom_navigation"
const val KEY_CALL_ALLOWED_MAIN_ACTIVITY = "call_allowed"
const val KEY_CALL_ALLOWED_ONBOARDING_ACTIVITY = "call_allowed"
