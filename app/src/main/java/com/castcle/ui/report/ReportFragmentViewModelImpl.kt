package com.castcle.ui.report

import android.annotation.SuppressLint
import com.castcle.usecase.userprofile.BlockUserUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
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
//  Created by sklim on 19/8/2021 AD at 11:34.
@SuppressLint("StaticFieldLeak")
class ReportFragmentViewModelImpl @Inject constructor(
    private val blockUserUseCase: BlockUserUseCase,
) : ReportFragmentViewModel() {

    private val _showLoading = BehaviorSubject.create<Boolean>()
    override val showLoading: Observable<Boolean>
        get() = _showLoading

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    override fun blockUser(userId: String): Completable {
        return blockUserUseCase.execute(userId)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _showLoading.onNext(false) }
            .doFinally { _showLoading.onNext(false) }
    }

}
