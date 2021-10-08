package com.castcle.ui.search.onsearch

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentSearchTrendBinding
import com.castcle.android.databinding.ToolbarCastcleLanguageBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.extensions.gone
import com.castcle.extensions.visibleOrGone
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.search.SearchAdapter
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
//  Created by sklim on 7/10/2021 AD at 18:14.

class SearchFragment : BaseFragment<SearchFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentSearchTrendBinding>,
    ToolbarBindingInflater<ToolbarCastcleLanguageBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var searchTrendAdapter: SearchTrendAdapter

    private var _cacheKeyword: String = ""

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchTrendBinding
        get() = { inflater, container, attachToRoot ->
            FragmentSearchTrendBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentSearchTrendBinding
        get() = viewBinding as FragmentSearchTrendBinding

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleLanguageBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleLanguageBinding.inflate(inflater, container, attachToRoot)
        }

    override val toolbarBinding: ToolbarCastcleLanguageBinding
        get() = toolbarViewBinding as ToolbarCastcleLanguageBinding

    override fun viewModel(): SearchFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(SearchFragmentViewModel::class.java)

    override fun initViewModel() {

    }

    override fun setupView() {
        setupToolBar()
        binding.etTextInputPrimary.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                onSearchByKeyword(s.toString())
            }
        })

        binding.rvRank.run {
            adapter = SearchTrendAdapter().also {
                searchTrendAdapter = it
            }
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.gone()
            tvToolbarTitle.text = requireContext().getString(R.string.search_title_bar)
            ivToolbarLogoButton.subscribeOnClick {
                findNavController().navigateUp()
            }.addToDisposables()
        }
    }

    private fun onSearchByKeyword(keyword: String) {
        if (_cacheKeyword != keyword) {
            viewModel.onClearCache()
            _cacheKeyword = keyword
        }
        viewModel.input.getSearch(keyword)
    }

    override fun bindViewEvents() {

    }

    override fun bindViewModel() {
        viewModel.responseSearch.subscribe {
            onBindSearchItem(it)
        }.addToDisposables()

        viewModel.showLoading.subscribe {
            onBindShowLoading(it)
        }.addToDisposables()

        viewModel.onError.subscribe {
            onBindEmptyState(true)
        }.addToDisposables()
    }

    private fun onBindSearchItem(list: List<SearchUiModel>) {
        if (list.isNotEmpty()) {
            onBindEmptyState(false)
            searchTrendAdapter.uiModels = list
        } else {
            onBindEmptyState(true)
        }
    }

    private fun onBindShowLoading(message: String) {
        binding.groupLoding.run {
            visibleOrGone(message.isNotBlank())
            binding.tvLoadingStatus.text = message
        }
    }

    private fun onBindEmptyState(showError: Boolean) {
        binding.empState.run {
            visibleOrGone(showError)
            bindUiState(EmptyState.SEARCH_EMPTY)
        }

        binding.rvRank.visibleOrGone(!showError)
    }
}

private const val LIMIT_SEARCH = 3