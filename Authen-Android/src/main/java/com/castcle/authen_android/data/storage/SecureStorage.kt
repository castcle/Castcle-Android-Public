package com.castcle.authen_android.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.castcle.authen_android.data.encryption.Encryptor
import com.castcle.authen_android.data.encryption.SecretKeyException
import javax.inject.Inject

interface SecureStorage {
    var userAccessToken: String?
    var userAccessTokenExpiresAt: Long?
    var userTokenType: String?
    var userRefreshToken: String?
    var tokenId: String?
    var uxSessionId: String?
}

class SecureStorageImpl @Inject constructor(
    private val preferences: SharedPreferences,
    private val encryptor: Encryptor
) : SecureStorage {

    override var userAccessToken: String?
        get() = get(USER_ACCESS_TOKEN_KEY)
        set(accessToken) = setOrRemove(USER_ACCESS_TOKEN_KEY, accessToken)

    override var userTokenType: String?
        get() = get(USER_TOKEN_TYPE_KEY)
        set(tokenType) = setOrRemove(USER_TOKEN_TYPE_KEY, tokenType)

    override var userRefreshToken: String?
        get() = get(USER_REFRESH_TOKEN_KEY)
        set(refreshToken) = setOrRemove(USER_REFRESH_TOKEN_KEY, refreshToken)

    override var tokenId: String?
        get() = get(USER_REFRESH_TOKEN_ID_KEY)
        set(tokenId) = setOrRemove(USER_REFRESH_TOKEN_ID_KEY, tokenId)

    override var uxSessionId: String?
        get() = get(USER_REFRESH_UX_SESSION_ID_KEY)
        set(uxSessionId) = setOrRemove(USER_REFRESH_UX_SESSION_ID_KEY, uxSessionId)

    override var userAccessTokenExpiresAt: Long?
        get() = get(USER_ACCESS_TOKEN_EXPIRES_AT_KEY)?.toLong()
        set(expiresAt) = setOrRemove(USER_ACCESS_TOKEN_EXPIRES_AT_KEY, expiresAt?.toString())

    private fun get(key: String): String? {
        var value = preferences.getString(key, null)
        if (value != null) {
            value = try {
                encryptor.decodeAndDecrypt(value)
            } catch (e: SecretKeyException) {
                // Remove the item from preferences as it cannot be decrypted any more
                setOrRemove(key, null)
                null
            }
        }
        return value
    }

    private fun setOrRemove(key: String, value: String?) {
        preferences.edit {
            if (value == null) {
                remove(key)
            } else {
                putString(key, encryptor.encryptAndEncode(value))
            }
        }
    }

    private companion object Constants {
        const val USER_ACCESS_TOKEN_KEY = "user_access_token"
        const val USER_ACCESS_TOKEN_EXPIRES_AT_KEY = "user_access_token_expires_at"
        const val USER_TOKEN_TYPE_KEY = "user_token_type"
        const val USER_REFRESH_TOKEN_KEY = "user_refresh_token"
        const val USER_REFRESH_TOKEN_ID_KEY = "user_token_id"
        const val USER_REFRESH_UX_SESSION_ID_KEY = "user_ux_session_id"
    }
}
