package com.castcle.common.lib.rxjava.transformers

import io.reactivex.Maybe
import io.reactivex.MaybeSource
import io.reactivex.MaybeTransformer
import io.reactivex.functions.Consumer

class MaybeErrorTransformer<T>(
    private val errorConsumer: Consumer<Throwable>
) : MaybeTransformer<T, T> {

    override fun apply(source: Maybe<T>): MaybeSource<T> {
        return source
            .doOnError(errorConsumer::accept)
            .onErrorComplete()
    }
}
