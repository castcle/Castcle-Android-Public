package com.castcle.di.modules

import com.castcle.di.modules.common.SessionEnvironmentModule
import com.castcle.di.modules.onboard.OnBoardActivityModule
import com.castcle.di.modules.onboard.OnBoardFragmentModule
import com.castcle.di.modules.splashscreen.SplashScreenActivityModule
import com.castcle.di.scope.ActivityScope
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.splashscreen.SplashScreenActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            OnBoardActivityModule::class,
            OnBoardFragmentModule::class,
            SessionEnvironmentModule::class,
        ]
    )
    fun onBoardActivity(): OnBoardActivity

    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            SplashScreenActivityModule::class,
            SessionEnvironmentModule::class
        ]
    )
    fun SplashScreenActivity(): SplashScreenActivity
}
