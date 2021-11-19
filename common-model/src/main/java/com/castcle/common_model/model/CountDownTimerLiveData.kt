package com.castcle.common_model.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData

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
//  Created by sklim on 16/11/2021 AD at 11:16.

class CountDownTimerLiveData(
    private val millisInFuture: Long,
    private val countDownInterval: Long
) : LiveData<Long>() {
    private var countDownTimer: CountDownTimer? = null
    private var remainingDuration: Long = millisInFuture

    init {
        initCountDownTimer()
    }

    fun start() {
        countDownTimer?.start()
        postValue(millisInFuture)
    }

    fun stop() {
        countDownTimer?.cancel()
        initCountDownTimer()
    }

    fun reset() {
        remainingDuration = millisInFuture
        stop()
        postValue(millisInFuture)
    }

    private fun initCountDownTimer() {
        countDownTimer = object : CountDownTimer(remainingDuration, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                remainingDuration = millisUntilFinished
                postValue(millisUntilFinished.toSeconds())
            }

            override fun onFinish() {
                postValue(0)
            }
        }
    }
}

fun Long.toSeconds(): Long {
    return (this / 1000) % 60
}