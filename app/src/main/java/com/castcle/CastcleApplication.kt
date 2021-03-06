package com.castcle

import androidx.work.Configuration
import com.castcle.analytics.appcenter.AppCenterAnalytics
import com.castcle.authen_android.di.components.DaggerAuthenticateComponent
import com.castcle.di.component.DaggerAppComponent
import com.castcle.di.component.DaggerStorageComponent
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import java.io.IOException
import java.net.SocketException
import javax.inject.Inject


class CastcleApplication : DaggerApplication(), Configuration.Provider {

    @Inject lateinit var appCenterAnalytics: AppCenterAnalytics

    @Inject lateinit var workerConfiguration: Configuration

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        appCenterAnalytics.start()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val storageComponent = DaggerStorageComponent
            .factory()
            .create(this)

        val authenticateComponent = DaggerAuthenticateComponent
            .factory()
            .create(this, storageComponent)

        return DaggerAppComponent
            .factory()
            .create(this, authenticateComponent, storageComponent)
    }

    private fun registerRxGlobalErrorHandler() {
        RxJavaPlugins.setErrorHandler {
            val e = if (it is UndeliverableException) it.cause else it

            when (e) {
                is IOException, is SocketException -> {
                    // fine, irrelevant network problem or API that throws on cancellation
                    return@setErrorHandler
                }
                is InterruptedException -> {
                    // fine, some blocking code was interrupted by a dispose call
                    return@setErrorHandler
                }
                is NullPointerException, is IllegalArgumentException -> {
                    // that's likely a bug in the application
                    Thread.currentThread()
                        .uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), e)
                    return@setErrorHandler
                }
                is IllegalStateException -> {
                    // that's a bug in RxJava or in a custom operator
                    Thread.currentThread()
                        .uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), e)
                    return@setErrorHandler
                }
            }

            Timber.e(e, "Undeliverable exception received, not sure what to do")
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return workerConfiguration
    }
}
