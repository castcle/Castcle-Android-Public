package com.castcle.di.service

import com.castcle.di.scope.ServiceScope
import com.castcle.service.CastcleFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ServiceModule {

    @ServiceScope
    @ContributesAndroidInjector(modules = [CastcleFirebaseMessagingServiceModule::class])
    fun castcleFirebaseMessagingService(): CastcleFirebaseMessagingService
}
