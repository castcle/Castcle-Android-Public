package com.castcle.common.lib.rxjava.transformers

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Consumer

class NeverErrorTransformer<T>(
    private val errorConsumer: Consumer<Throwable>? = null
) : ObservableTransformer<T, T> {

    override fun apply(source: Observable<T>): ObservableSource<T> {
        return source
            .doOnError { throwable ->
                errorConsumer?.accept(throwable)
            }
            .onErrorResumeNext(Observable.empty())
    }
}
