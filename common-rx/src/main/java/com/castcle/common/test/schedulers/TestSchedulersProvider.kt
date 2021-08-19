package com.castcle.common.test.schedulers

import io.reactivex.schedulers.Schedulers
import com.castcle.common.lib.schedulers.SchedulersProvider

@Suppress("IllegalIdentifier")
class TestSchedulersProvider : SchedulersProvider {
    override fun io() = Schedulers.trampoline()

    override fun computation() = Schedulers.trampoline()

    override fun main() = Schedulers.trampoline()

}
