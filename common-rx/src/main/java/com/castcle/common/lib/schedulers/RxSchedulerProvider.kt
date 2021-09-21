package com.castcle.common.lib.schedulers

interface RxSchedulerProvider {
    fun io(): RxScheduler

    fun computation(): RxScheduler

    fun main(): RxScheduler

    fun background(): RxScheduler
}
