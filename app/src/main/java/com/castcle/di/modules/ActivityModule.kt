package com.castcle.di.modules

import com.castcle.di.modules.onboard.OnBoardActivityModule
import com.castcle.di.modules.splashscreen.SplashScreenActivityModule
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.splashscreen.SplashScreenActivity
import com.castcle.di.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            OnBoardActivityModule::class
        ]
    )
    fun onBoardActivity(): OnBoardActivity

    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            SplashScreenActivityModule::class
        ]
    )
    fun SplashScreenActivity(): SplashScreenActivity
}