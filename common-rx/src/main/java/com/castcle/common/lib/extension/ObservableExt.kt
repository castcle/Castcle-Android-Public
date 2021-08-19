package com.castcle.common.lib.extension

import android.util.Log
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import com.castcle.common.lib.common.Optional
import com.castcle.common.lib.rxjava.transformers.*
import java.util.concurrent.*

/**
 * An utility method to help preventing the Observable source to emit multiple times (debounce)
 * within the [default][DEFAULT_DEBOUNCE_TIME] amount of time.
 *
 * Example: This is helpful to <b>prevent multiple click events</b> triggering the emission.
 *
 * @param [scheduler] where to manage the timers that handle timeout for each event. Default: [mainThread]
 * @param [duration] duration of debounce. Default: [DEFAULT_DEBOUNCE_TIME]
 * @return an [Observable] that performs the throttle operation.
 */
fun <T> Observable<T>.debounce(scheduler: Scheduler = mainThread(), duration: Long = DEFAULT_DEBOUNCE_TIME): Observable<T> {
    return this.throttleFirst(duration, TimeUnit.MILLISECONDS, scheduler)
}

/**
 * An extension method that helps debounce items emitted from the source stream under a specified condition
 * If the condition isn't met, the items will be emitted immediately
 */
fun <T> Observable<T>.debounceIf(
    predicate: (T) -> Boolean,
    timeout: Long,
    unit: TimeUnit,
    scheduler: Scheduler
): Observable<T> {
    return this.publish { sharedSrc ->
        Observable.merge(
            sharedSrc.debounce(timeout, unit, scheduler)
                .filter { predicate.invoke(it) },
            sharedSrc.filter { !predicate.invoke(it) }
        )
    }
}

/**
 * An extension utility to streamline the creation of an Observable that transformed with
 * [Transformers.takeWhen], helping us to write shorter syntax in kotlin
 */
inline fun <reified S, T> Observable<S>.takeWhen(`when`: Observable<T>): Observable<S> {
    return this.compose<S>(Transformers.takeWhen(`when`))
}

/**
 * An extension utility to streamline the creation of an Observable that transformed with
 * [Transformers.flowableNeverError], helping us to write shorter syntax in kotlin
 */
inline fun <reified T> Flowable<T>.neverErrorFlow(): Flowable<T> {
    return this.compose<T>(Transformers.flowableNeverError())
}

inline fun <reified T> Maybe<T>.pipeErrorTo(errorSubject: PublishSubject<Throwable>): Maybe<T> {
    return this.compose<T>(MaybeErrorTransformer(Consumer(errorSubject::onNext)))
}

inline fun <reified T> Flowable<T>.pipeErrorTo(errorSubject: PublishSubject<Throwable>): Flowable<T> {
    return this.compose<T>(FlowableErrorTransformer(Consumer(errorSubject::onNext)))
}

inline fun <reified T> Observable<T>.pipeErrorTo(errorSubject: PublishSubject<Throwable>): Observable<T> {
    return this.compose<T>(ObservableErrorTransformer(Consumer(errorSubject::onNext)))
}

/**
 * Create a Completable source that will delay the invocation of [action] with in [timeInSec].
 * There is an old familiar solution before is to use a Handler:
 *  Handler().postDelayed( { // invoke }, TIME_IN_SEC)
 *
 * @param: [timeInSec] the amount of delaying time
 * @param: [subscribeScheduler] the scheduler that the timer operator will subscribe/happen on
 * @param: [observeScheduler] the scheduler that the invocation will happen
 * @param: [action] the function that we want to delayed invoke.
 */
inline fun delayAndDo(
    timeInSec: Long,
    subscribeScheduler: Scheduler,
    observeScheduler: Scheduler,
    crossinline action: () -> Unit
): Disposable {
    return Completable.timer(timeInSec, TimeUnit.SECONDS, subscribeScheduler)
        .observeOn(observeScheduler)
        .subscribe { action.invoke() }
}

/**
 * Subscribe to the Observable<T>, with the default debounce timeout defined in method [debounce].
 * When an error happens, the stream isn't stopped, it just prints out the error with [Log.e]
 */
fun <T> Observable<T>.debounceAndSubscribe(onNext: (T) -> Unit): Disposable {
    return this
        .debounce(mainThread())
        .flatMapSingle {
            Single.fromCallable { onNext(it) }
                .onErrorReturn {
                    Log.e("debounceAndSubscribe", it.message ?: "Unknown error")
                }
        }
        .subscribe()
}

/**
 * Subscribe to the Observable<T>, with debounce timeout equal to the supplied argument.
 * When an error happens, the stream isn't stopped, it just prints out the error with [Log.e]
 *
 * @param onNext callback function to be invoked when the items emit.
 * @param duration duration of debounce.
 *
 * @return [Disposable] that performs the throttle operation and invokes the callbacks.
 */
fun <T> Observable<T>.debounceAndSubscribe(onNext: (T) -> Unit, duration: Long): Disposable {
    return this
        .debounce(mainThread(), duration)
        .flatMapSingle {
            Single.fromCallable { onNext(it) }
                .onErrorReturn {
                    Log.e("debounceAndSubscribe", it.message ?: "Unknown error")
                }
        }
        .subscribe()
}

/**
 * Subscribe to the Observable<T>, with debounce timeout equal to the supplied argument.
 * When an error happens, the stream isn't stopped, it just notifies the error through [onError] callback
 *
 * @param onNext callback function to be invoked when the items emit.
 * @param onError callback function to be invoked when an error occurs.
 * @param duration duration of debounce.
 *
 * @return [Disposable] that performs the throttle operation and invokes the callbacks.
 */
fun <T> Observable<T>.debounceAndSubscribe(
    onNext: (T) -> Unit,
    onError: (Throwable) -> Unit,
    duration: Long
): Disposable {
    return this
        .debounce(mainThread(), duration)
        .flatMapSingle {
            Single.fromCallable { onNext(it) }
                .onErrorReturn(onError)
        }
        .subscribe()
}

fun <T> Single<T>.doIfTakeLongerThan(timeout: Long, unit: TimeUnit, action: () -> Unit): Single<T> {
    return this
        .map { Optional.of(it) }
        .mergeWith(
            Single.timer(timeout, unit, mainThread())
                .map {
                    action.invoke()
                    Optional.empty<T>()
                }
        )
        .filter { it.isPresent }
        .firstOrError()
        .map { it.get() }
}

// Default debounce time for an event emission, avoid duplicated clicks.
const val DEFAULT_DEBOUNCE_TIME = 200L
const val LONG_DEBOUNCE_TIME = 1000L
