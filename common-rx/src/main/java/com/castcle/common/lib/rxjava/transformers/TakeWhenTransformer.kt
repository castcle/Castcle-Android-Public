package com.castcle.common.lib.rxjava.transformers

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

class TakeWhenTransformer<S, in T> constructor(private val `when` : Observable<in T>) : ObservableTransformer<S, S> {

    override fun apply(upstream: Observable<S>): ObservableSource<S> {
        return `when`.withLatestFrom(upstream, BiFunction { _, t2 -> t2 })
    }
}
