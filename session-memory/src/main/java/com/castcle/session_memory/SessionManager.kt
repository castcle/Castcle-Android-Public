package com.castcle.session_memory

import com.castcle.session_memory.model.SessionEnvironment


interface SessionManager {
    fun getSessionToken(): SessionEnvironment.SessionToken?

    fun setSessionToken(token: SessionEnvironment.SessionToken)

    fun setSessionRefreshToken(token: SessionEnvironment.SessionRefreshToken)

    fun getSessionRefreshToken(): SessionEnvironment.SessionRefreshToken?

    fun setSessionEnvironment(environment: SessionEnvironment)

    fun clearSession()

    fun getLanguageCode(): String?

    fun setLanguageCode(languageCode: String?)
}
