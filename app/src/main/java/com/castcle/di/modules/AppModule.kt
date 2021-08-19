package com.castcle.di.modules

import android.app.Application
import android.content.Context
import dagger.*
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common.lib.schedulers.RxSchedulerProviderImpl
import com.castcle.di.scope.ApplicationScope

@Module
@SuppressWarnings("UnnecessaryAbstractClass")
abstract class AppModule {

    @Binds
    @ApplicationScope
    internal abstract fun applicationContext(application: Application): Context

    companion object {

        @Provides
        @ApplicationScope
        fun rxSchedulerProvider(): RxSchedulerProvider = RxSchedulerProviderImpl()
    }
}
