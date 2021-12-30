package com.castcle.ui.search.onsearch

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentSearchTrendBinding
import com.castcle.android.databinding.ToolbarCastcleLanguageBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.SearchItemClick
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
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

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        activityViewModel.fetchUserProfile().subscribe().addToDisposables()
    }

    override fun setupView() {
        setupToolBar()
        with(binding.etTextInputPrimary) {
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setOnEditorActionListener { _, actionId, keyEven ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchByKeyword(text.toString())
                    true
                } else {
                    false
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    onSearchByKeyword(s.toString())
                }
            })
        }

        binding.ivCancel.subscribeOnClick {
            binding.etTextInputPrimary.setText("")
            onBindClose(false)
        }

        binding.rvRank.run {
            adapter = SearchTrendAdapter().also {
                searchTrendAdapter = it
            }
        }
    }

    private fun onBindClose(show: Boolean) {
        binding.ivCancel.visibleOrGone(show)
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.invisible()
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

        onBindClose(keyword.isNotEmpty())
        if (keyword.isNotEmpty()) {
            viewModel.input.getSearch(keyword)
        } else {
            viewModel.getResentSearch()
        }
    }

    override fun bindViewEvents() {
        searchTrendAdapter.itemClick.subscribe {
            handleItemClick(it)
        }.addToDisposables()
    }

    private fun handleItemClick(itemClick: Click) {
        when (itemClick) {
            is SearchItemClick.HasTagItemClick -> {
                handlerHasTag(itemClick)
            }
            is SearchItemClick.PersonItemClick -> {
                handlerPerson(itemClick)
            }
            is SearchItemClick.SuggestionItemClick -> {
                handlerSuggested(itemClick)
            }
            is SearchItemClick.SuggestionClearClick -> {
                handleClearRecentSearch()
            }
        }
    }

    private fun handleClearRecentSearch() {
        viewModel.onClearRecentSearch()
    }

    private fun handlerHasTag(itemClick: SearchItemClick.HasTagItemClick) {
        if (itemClick.searchUiModel is SearchUiModel.SearchHasTagUiModel) {
            navigateToTrendFragment(itemClick.searchUiModel.slug)
        }
    }

    private fun navigateToTrendFragment(trendSlug: String) {
        onBoardNavigator.navigateToTrendFragment(trendSlug)
    }

    private fun handlerPerson(itemClick: SearchItemClick.PersonItemClick) {
        if (itemClick.searchUiModel is SearchUiModel.SearchFollowUiModel) {
            var profileType = ""
            var isMe = false
            checkContentIsMe(itemClick.searchUiModel.castcleId,
                onPage = {
                    profileType = ProfileType.PROFILE_TYPE_PAGE_ME.type
                    isMe = true
                }, onMe = {
                    isMe = true
                    profileType = ProfileType.PROFILE_TYPE_ME.type
                }, onView = {
                    profileType = itemClick.searchUiModel.type
                }
            )
            navigateToProfile(itemClick.searchUiModel.castcleId, profileType, isMe)
        }
    }

    private fun checkContentIsMe(
        castcleId: String,
        onMe: () -> Unit,
        onPage: () -> Unit,
        onView: () -> Unit
    ) {
        activityViewModel.checkContentIsMe(castcleId,
            onProfileMe = {
                onMe.invoke()
            }, onPageMe = {
                onPage.invoke()
            }, non = {
                onView.invoke()
            })
    }

    private fun navigateToProfile(castcleId: String, type: String, isMe: Boolean = false) {
        onBoardNavigator.navigateToProfileFragment(castcleId, type, isMe)
    }

    private fun handlerSuggested(itemClick: SearchItemClick.SuggestionItemClick) {
        when (itemClick.searchUiModel) {
            is SearchUiModel.SearchKeywordUiModel -> {
                binding.etTextInputPrimary.setText(itemClick.searchUiModel.text)
            }
            is SearchUiModel.SearchResentUiModel -> {
                binding.etTextInputPrimary.setText(itemClick.searchUiModel.keyword)
            }
            else -> {
            }
        }
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

        viewModel.resentSearchResponse.observe(viewLifecycleOwner, {
            it.toMutableList().apply {
                if (isNotEmpty()) {
                    add(0, SearchUiModel.SearchResentHeaderUiModel)
                }
            }.run(::onBindSearchItem)
        })
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
        binding.pbLoading.run {
            visibleOrGone(message.isNotBlank())
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
