package com.castcle.ui.base

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.*

//  Copyright (c) 2021, Castcle and/or its affiliates. All rights reserved.
//  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//
//  This code is free software; you can redistribute it and/or modify it
//  under the terms of the GNU General Public License version 3 only, as
//  published by the Free Software Foundation.
//
//  This code is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
//  version 3 for more details (a copy is included in the LICENSE file that
//  accompanied this code).
//
//  You should have received a copy of the GNU General Public License version
//  3 along with this work; if not, write to the Free Software Foundation,
//  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
//
//  Please contact Castcle, 22 Phet Kasem 47/2 Alley, Bang Khae, Bangkok,
//  Thailand 10160, or visit www.castcle.com if you need additional information
//  or have any questions.
//
//
//  Created by sklim on 30/8/2021 AD at 22:39.

class RxImmediateSchedulerRule : TestRule {
    private val immediate: Scheduler = object : Scheduler() {

        override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            return super.scheduleDirect(run, 0, unit)
        }

        override fun createWorker(): Worker {
            return ExecutorScheduler.ExecutorWorker(Executor { obj: Runnable -> obj.run() }, true)
        }
    }

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                RxJavaPlugins
                    .setInitIoSchedulerHandler { scheduler: Callable<Scheduler?>? ->
                        immediate
                    }
                RxJavaPlugins
                    .setInitComputationSchedulerHandler { scheduler: Callable<Scheduler?>? ->
                        immediate
                    }
                RxJavaPlugins
                    .setInitNewThreadSchedulerHandler { scheduler: Callable<Scheduler?>? ->
                        immediate
                    }
                RxJavaPlugins
                    .setInitSingleSchedulerHandler { scheduler: Callable<Scheduler?>? ->
                        immediate
                    }
                RxAndroidPlugins
                    .setInitMainThreadSchedulerHandler { scheduler: Callable<Scheduler?>? ->
                        immediate
                    }
                try {
                    base.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
    }
}
