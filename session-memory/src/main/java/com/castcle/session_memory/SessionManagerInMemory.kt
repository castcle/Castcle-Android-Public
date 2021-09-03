package com.castcle.session_memory

import com.castcle.session_memory.model.SessionEnvironment

object SessionManagerInMemory : SessionManager {

    private var token: SessionEnvironment.SessionToken? = null
    private var refreshToken: SessionEnvironment.SessionRefreshToken? = null
    private var languageCode: String? = null

    override fun getSessionToken(): SessionEnvironment.SessionToken? {
        return token
    }

    override fun setSessionToken(token: SessionEnvironment.SessionToken) {
        SessionManagerInMemory.token = token
    }

    override fun setSessionRefreshToken(refreshToken: SessionEnvironment.SessionRefreshToken) {
        SessionManagerInMemory.refreshToken = refreshToken
    }

    override fun setSessionEnvironment(environment: SessionEnvironment) {
        if (environment.sessionToken != null) {
            token = environment.sessionToken
        }

        languageCode = environment.languageCode
    }

    override fun clearSession() {
        token = null
        languageCode = null
    }

    override fun getLanguageCode(): String? {
        return languageCode
    }

    override fun setLanguageCode(languageCode: String?) {
        SessionManagerInMemory.languageCode = languageCode
    }
}
