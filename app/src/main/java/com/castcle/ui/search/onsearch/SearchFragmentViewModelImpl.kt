package com.castcle.ui.search.onsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.android.R
import com.castcle.common.lib.extension.doIfTakeLongerThan
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.localization.LocalizedResources
import com.castcle.networking.service.common.TIMEOUT_SEARCH_REQUEST
import com.castcle.networking.service.common.TIMEOUT_SHOWING_SPINNER
import com.castcle.usecase.search.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.*
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
//  Created by sklim on 7/10/2021 AD at 18:15.

class SearchFragmentViewModelImpl @Inject constructor(
    private val rxSchedulerProvider: RxSchedulerProvider,
    private val localizedResources: LocalizedResources,
    private val getSearchTrendsSingleUseCase: GetSearchTrendsSingleUseCase,
    private val setResentSearchCompletableUseCase: SetResentSearchCompletableUseCase,
    private val getResentSearchSingleUseCase: GetResentSearchSingleUseCase
) : SearchFragmentViewModel(), SearchFragmentViewModel.Input {
    override val input: Input
        get() = this

    private val _showLoading = BehaviorSubject.create<String>()
    override val showLoading: Observable<String>
        get() = _showLoading

    private val _error = PublishSubject.create<Throwable>()
    override val onError: Observable<Throwable>
        get() = _error

    private val _searchResponse = MutableLiveData<List<SearchUiModel>>()
    override val searchResponse: LiveData<List<SearchUiModel>>
        get() = _searchResponse

    private val _resentSearchResponse = MutableLiveData<List<SearchUiModel>>()
    override val resentSearchResponse: LiveData<List<SearchUiModel>>
        get() = _resentSearchResponse

    private var _cacheSearchUiModel = MutableLiveData<List<SearchUiModel>>()
    private val _keyword = BehaviorSubject.create<String>()
    override fun getSearch(keyword: String) {
        _keyword.onNext(keyword)
    }

    override val responseSearch: Observable<List<SearchUiModel>>
        get() = _keyword
            .debounce(
                TIMEOUT_SEARCH_REQUEST,
                TimeUnit.MILLISECONDS,
                rxSchedulerProvider.main().get()
            ).switchMapSingle { keywork ->
                getSearchByKeyword(keywork)
                    .doIfTakeLongerThan(TIMEOUT_SHOWING_SPINNER, TimeUnit.MILLISECONDS) {
                        _showLoading.onNext(
                            localizedResources.getString(
                                R.string.search_loading_status
                            ).format(keywork)
                        )
                    }.doFinally {
                        _showLoading.onNext("")
                    }.doOnSubscribe {
                        _showLoading.onNext(
                            localizedResources.getString(
                                R.string.search_loading_status
                            ).format(keywork)
                        )
                    }.onErrorResumeNext {
                        _showLoading.onNext("")
                        _error.onNext(it)
                        Single.just(emptyList())
                    }
            }

    private fun getSearchByKeyword(keyword: String): Single<List<SearchUiModel>> {
        return if (_cacheSearchUiModel.value.isNullOrEmpty()) {
            getSearchTrendsSingleUseCase.execute(
                keyword
            ).map {
                _cacheSearchUiModel.value = it
                it
            }.doOnSuccess {
                saveOnKeywordSearch(keyword)
            }.doOnError {
                _showLoading.onNext("")
                _error.onNext(it)
            }
        } else {
            Single.just(_cacheSearchUiModel.value)
        }
    }

    private fun saveOnKeywordSearch(keyword: String) {
        getResentSearchSingleUseCase.execute(Unit)
            .doOnSuccess {
                saveResentSearch(it, keyword)
            }.subscribe().addToDisposables()
    }

    private fun saveResentSearch(list: List<SearchUiModel>, keyword: String) {

        list.toMutableList().apply {
            val cacheKeyword = find {
                (it as SearchUiModel.SearchResentUiModel).keyword == keyword
            }
            if (cacheKeyword != null) {
                this.sortByDescending {
                    (it as SearchUiModel.SearchResentUiModel).keyword == keyword
                }
            } else {
                add(SearchUiModel.SearchResentUiModel(keyword = keyword))
            }
        }.run {
            setResentSearchCompletableUseCase.execute(this).subscribe().addToDisposables()
        }
    }

    override fun getResentSearch() {
        getResentSearchSingleUseCase.execute(Unit).subscribeBy(
            onSuccess = {
                _resentSearchResponse.value = it
            }, onError = {
                _error.onNext(it)
            }
        ).addToDisposables()
    }

    override fun onClearRecentSearch() {
        _resentSearchResponse.value = emptyList()
        setResentSearchCompletableUseCase.execute(emptyList()).subscribe().addToDisposables()
    }

    override fun onClearItem() {
        TODO("Not yet implemented")
    }

    override fun onClearCache() {
        _cacheSearchUiModel.value = emptyList()
    }
}