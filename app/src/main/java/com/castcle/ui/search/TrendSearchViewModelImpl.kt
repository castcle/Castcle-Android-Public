package com.castcle.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.usecase.search.GetTopTrendsSingleUseCase
import com.castcle.usecase.userprofile.IsGuestModeSingleUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
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
//  Created by sklim on 28/9/2021 AD at 08:53.

class TrendSearchViewModelImpl @Inject constructor(
    private val isGuestModeSingleUseCase: IsGuestModeSingleUseCase,
    private val getTopTrendsSingleUseCase: GetTopTrendsSingleUseCase
) : TrendSearchViewModel(), TrendSearchViewModel.Input {
    override val input: Input
        get() = this

    private val _showLoading = BehaviorSubject.create<Boolean>()
    override val showLoading: Observable<Boolean>
        get() = _showLoading

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    private val _searchResponse = MutableLiveData<List<SearchUiModel>>()
    override val searchResponse: LiveData<List<SearchUiModel>>
        get() = _searchResponse

    override val isGuestMode: Boolean
        get() = isGuestModeSingleUseCase.execute(Unit).blockingGet()

    override fun getTopTrends() {
        getTopTrendsSingleUseCase.execute(Unit)
            .doOnSubscribe { _showLoading.onNext(true) }
            .doFinally { _showLoading.onNext(false) }
            .subscribeBy(
                onSuccess = {
                    setSearchResponse(it)
                },
                onError = {
                    _showLoading.onNext(false)
                    _error.onNext(it)
                }
            ).addToDisposables()
    }

    private fun setSearchResponse(list: List<SearchUiModel>) {
        _searchResponse.value = list
    }

    override fun getSearch(keyword: String) {

    }

}