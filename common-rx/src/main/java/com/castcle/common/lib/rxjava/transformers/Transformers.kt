package com.castcle.common.lib.rxjava.transformers

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

object Transformers {

    /**
     * Emits the latest value of the `source` Observable whenever the [when]
     * Observable emits.
     */
    fun <S, T> takeWhen(`when`: Observable<T>): TakeWhenTransformer<S, T> {
        return TakeWhenTransformer(`when`)
    }

    /**
     * Invoke the PublishSubject's onNext with Throwable receiving from the upper source
     */
    fun <T> pipeErrorTo(errorSubject: PublishSubject<Throwable>): FlowableErrorTransformer<T> {
        return FlowableErrorTransformer(Consumer(errorSubject::onNext))
    }

    /**
     * Prevents an observable from emitting error by chaining `onErrorResumeNext`.
     * // TODO: check out for `materalize()` as an alternative choice.
     */
    fun <T> neverError(): NeverErrorTransformer<T> {
        return NeverErrorTransformer()
    }

    /**
     * Prevents an flowable from erroring by chaining `onErrorResumeNext`.
     * // TODO: check out for `materalize()` as an alternative choice.
     */
    fun <T> flowableNeverError(): FlowableNeverErrorTransformer<T> {
        return FlowableNeverErrorTransformer()
    }
}
