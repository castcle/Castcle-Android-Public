package com.castcle.common.test.schedulers

import io.reactivex.schedulers.TestScheduler
import com.castcle.common.lib.schedulers.SchedulersProvider

@Suppress("IllegalIdentifier")
class AdvancedTestSchedulersProvider : SchedulersProvider {
    val scheduler = TestScheduler()

    override fun io() = scheduler

    override fun computation() = scheduler

    override fun main() = scheduler
}
