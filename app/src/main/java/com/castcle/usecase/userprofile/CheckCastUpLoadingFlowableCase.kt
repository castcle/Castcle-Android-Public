package com.castcle.usecase.userprofile

import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.data.error.AppError
import com.castcle.data.error.Ignored
import com.castcle.usecase.base.FlowableUseCase
import com.castcle.usecase.worker.factory.CastWithImageLoadWorkHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

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
//  Created by sklim on 20/10/2021 AD at 14:09.

class CheckCastUpLoadingFlowableCase @Inject constructor(
    private val castWithImageWorkerHelper: CastWithImageLoadWorkHelper,
    rxSchedulerProvider: RxSchedulerProvider
) : FlowableUseCase<Unit, Pair<Boolean, String>>(
    rxSchedulerProvider.main(),
    rxSchedulerProvider.main(),
    ::Ignored
) {

    override fun create(input: Unit): Flowable<Pair<Boolean, String>> {
        return castWithImageWorkerHelper
            .uploadStatus
            .map {
                val status = if (it is CastWithImageLoadWorkHelper.Status.Error) {
                    throw AppError(cause = null, readableMessageRes = it.error)
                } else {
                    it is CastWithImageLoadWorkHelper.Status.Success
                }

                val profileImageUrl =
                    (it as? CastWithImageLoadWorkHelper.Status.Success)?.castResponse ?: ""
                return@map Pair(status, profileImageUrl)
            }
            .toFlowable(BackpressureStrategy.LATEST)
    }
}
