package com.castcle.common.lib.schedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.castcle.common.test.schedulers.TestSchedulersProvider

sealed class RxScheduler {
    object MainThread : RxScheduler()
    object IoThread : RxScheduler()
    object ComputationThread: RxScheduler()

    object TestMainThread: RxScheduler()
    object TestIoThread: RxScheduler()
    object TestComputationThread: RxScheduler()

    fun get(): Scheduler {
        return when (this) {
            is MainThread -> AndroidSchedulers.mainThread()
            is IoThread -> Schedulers.io()
            is ComputationThread -> Schedulers.computation()

            is TestMainThread -> TestSchedulersProvider().main()
            is TestIoThread -> TestSchedulersProvider().io()
            is TestComputationThread -> TestSchedulersProvider().computation()
        }
    }
}
