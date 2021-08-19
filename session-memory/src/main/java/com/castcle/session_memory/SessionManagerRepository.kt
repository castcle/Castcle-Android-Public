package com.castcle.session_memory

import com.castcle.session_memory.SessionManager
import javax.inject.Inject

class SessionManagerRepository @Inject constructor(
    private val sessionManagerInMemory: SessionManager
) : SessionManager by sessionManagerInMemory
