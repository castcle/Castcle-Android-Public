package com.castcle.usecase.base

import com.castcle.android.BuildConfig
import com.castcle.common.lib.schedulers.RxScheduler
import com.castcle.data.error.AppError
import io.reactivex.Completable

abstract class CompletableUseCase<in UseCaseInput>(
    private val executionThread: RxScheduler,
    private val postExecutionThread: RxScheduler,
    defaultError: (Throwable) -> AppError
) : BaseUseCase(defaultError) {

    protected abstract fun create(input: UseCaseInput): Completable

    fun execute(input: UseCaseInput): Completable {
        return create(input)
            .onErrorResumeNext {
                if (isCanceledException(it)) {
                    Completable.never()
                } else {
                    Completable.error(composeError(it))
                }
            }
            .doOnError(::doOnError)
            .subscribeOn(executionThread.get())
            .observeOn(postExecutionThread.get())
    }

    private fun doOnError(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace()
        }
    }
}
