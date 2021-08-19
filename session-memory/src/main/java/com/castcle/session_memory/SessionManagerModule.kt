package com.castcle.session_memory

import dagger.Module
import dagger.Provides

@Module
class SessionManagerModule {

    @Provides
    fun provideSessionManagerInMemory(): SessionManager = SessionManagerInMemory

    @Provides
    fun provideSessionManagerRepository(
        sessionManagerInMemory: SessionManager
    ): SessionManagerRepository {
        return SessionManagerRepository(sessionManagerInMemory)
    }
}
