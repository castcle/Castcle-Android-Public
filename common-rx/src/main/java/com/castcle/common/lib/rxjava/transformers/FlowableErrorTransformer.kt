package com.castcle.common.lib.rxjava.transformers

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.functions.Consumer
import org.reactivestreams.Publisher

class FlowableErrorTransformer<T>(
    private val errorConsumer: Consumer<Throwable>?) : FlowableTransformer<T, T> {

    override fun apply(source: Flowable<T>): Publisher<T> {
        return source.doOnError { throwable ->
            if (errorConsumer != null) {
                this.errorConsumer.accept(throwable)
            }
        }.onErrorResumeNext(Flowable.empty())
    }
}
