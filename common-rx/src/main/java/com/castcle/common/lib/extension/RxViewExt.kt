package com.castcle.common.lib.extension

import android.util.Log
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.Disposable

/**
 * Subscribe to the [View]'s OnClick event, with debounce timeout equal to the supplied argument (default: [DEFAULT_DEBOUNCE_TIME]).
 * When an error happens, the stream isn't stopped, it just notifies the error through [onError] callback
 */
fun View.subscribeOnClick(
    onNext: () -> Unit,
    onError: (Throwable) -> Unit,
    debounceDuration: Long = DEFAULT_DEBOUNCE_TIME
): Disposable {
    return clicks()
        .debounceAndSubscribe(
            onNext = { onNext() },
            onError = onError,
            duration = debounceDuration
        )
}

/**
 * Subscribe to the [View]'s OnClick event, with debounce timeout defined in method [debounceAndSubscribe].
 * When an error happens, the stream isn't stopped, it just prints out the error with [Log.e]
 */
fun View.subscribeOnClick(onNext: () -> Unit): Disposable {
    return clicks().debounceAndSubscribe { onNext() }
}

/**
 * Subscribe to the [View]'s OnClick event, with debounce timeout passed as argument.
 * When an error happens, the stream isn't stopped, it just prints out the error with [Log.e]
 */
fun View.subscribeOnClick(onNext: () -> Unit, debounceDuration: Long): Disposable {
    return clicks().debounceAndSubscribe({ onNext() }, debounceDuration)
}
