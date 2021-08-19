package com.castcle.usecase.base

import com.castcle.android.BuildConfig
import com.castcle.common.lib.schedulers.RxScheduler
import com.castcle.data.error.AppError
import io.reactivex.Flowable

abstract class FlowableUseCase<in UseCaseInput, T>(
    private val executionThread: RxScheduler,
    private val postExecutionThread: RxScheduler,
    defaultError: (Throwable) -> AppError
) : BaseUseCase(defaultError) {

    protected abstract fun create(input: UseCaseInput): Flowable<T>

    fun execute(input: UseCaseInput): Flowable<T> {
        return create(input)
            .onErrorResumeNext { error: Throwable ->
                if (isCanceledException(error)) {
                    Flowable.never()
                } else {
                    Flowable.error<T>(composeError(error))
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
