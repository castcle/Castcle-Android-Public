package com.castcle.ui.common.dialog.recast

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.RecastRequest
import com.castcle.usecase.feed.RecastContentSingleUseCase
import io.reactivex.Completable
import io.reactivex.Observable
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
//  Created by sklim on 23/8/2021 AD at 12:50.

class RecastDialogViewModelImpl @Inject constructor(
    private val recastContentSingleUseCase: RecastContentSingleUseCase
) : RecastDialogViewModel(), RecastDialogViewModel.Input {

    override val input: Input
        get() = this

    private val _error = PublishSubject.create<Throwable>()

    override val onSuccess: Observable<Boolean>
        get() = _onSuccess
    private val _onSuccess = PublishSubject.create<Boolean>()

    override val onError: Observable<Throwable>
        get() = _error

    private val _contentUiModel = MutableLiveData<ContentUiModel>()

    override fun recastContent(contentUiModel: ContentUiModel) {
        RecastRequest(
            reCasted = contentUiModel.payLoadUiModel.reCastedUiModel.recasted,
            contentId = contentUiModel.payLoadUiModel.contentId,
            authorId = contentUiModel.payLoadUiModel.author.id
        ).run {
            postReCastContent(this)
                .subscribe()
                .addToDisposables()
        }
    }

    @SuppressLint("CheckResult")
    private fun postReCastContent(recastRequest: RecastRequest): Completable {
        return recastContentSingleUseCase.execute(
            recastRequest
        ).doOnSuccess {
            _onSuccess.onNext(true)
            _contentUiModel.value = it
        }.onErrorReturn {
            _error.onNext(it)
            ContentUiModel()
        }.ignoreElement()
    }
}