package com.castcle.usecase.worker.factory

import android.content.Context
import androidx.work.*
import javax.inject.Inject
import javax.inject.Provider

class CastcleWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ListenableWorker>,
        @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return workerFactories.entries.firstOrNull { entry ->
            Class.forName(workerClassName).isAssignableFrom(entry.key)
        }?.value?.get()?.create(appContext, workerParameters)
    }
}
