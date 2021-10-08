package com.castcle.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.databinding.FragmentSearchBinding
import com.castcle.android.databinding.ToolbarCastcleCommonBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import javax.inject.Inject

class TrendSearchFragment : BaseFragment<TrendSearchViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentSearchBinding>,
    ToolbarBindingInflater<ToolbarCastcleCommonBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var searchAdapter: SearchAdapter

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = { inflater, container, attachToRoot ->
            FragmentSearchBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentSearchBinding
        get() = viewBinding as FragmentSearchBinding

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleCommonBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleCommonBinding.inflate(inflater, container, attachToRoot)
        }

    override val toolbarBinding: ToolbarCastcleCommonBinding
        get() = toolbarViewBinding as ToolbarCastcleCommonBinding

    override fun viewModel(): TrendSearchViewModel =
        ViewModelProvider(this, viewModelFactory).get(TrendSearchViewModel::class.java)

    override fun initViewModel() {
        viewModel.getTopTrends()
    }

    override fun setupView() {
        binding.empState.bindUiState(EmptyState.SEARCH_EMPTY)

        binding.rvRank.run {
            adapter = SearchAdapter().also {
                searchAdapter = it
            }
        }
    }

    override fun bindViewEvents() {
        searchAdapter.itemClick.subscribe {

        }.addToDisposables()

        binding.clTextInputLayout.subscribeOnClick {
            navigateToSearchTrend()
        }.addToDisposables()
    }

    private fun navigateToSearchTrend() {
        onBoardNavigator.navigateToSearchTrendFragmrnt()
    }

    override fun bindViewModel() {
        viewModel.searchResponse.observe(this, {
            searchAdapter.uiModels = it
        })
    }
}
