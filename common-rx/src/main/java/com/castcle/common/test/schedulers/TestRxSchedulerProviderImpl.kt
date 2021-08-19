package com.castcle.common.test.schedulers

import com.castcle.common.lib.schedulers.RxScheduler
import com.castcle.common.lib.schedulers.RxSchedulerProvider

class TestRxSchedulerProviderImpl : RxSchedulerProvider {
    override fun io(): RxScheduler = RxScheduler.TestIoThread

    override fun computation(): RxScheduler = RxScheduler.TestComputationThread

    override fun main(): RxScheduler = RxScheduler.TestMainThread
}
