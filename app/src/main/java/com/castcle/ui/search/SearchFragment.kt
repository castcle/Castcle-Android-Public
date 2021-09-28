package com.castcle.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.databinding.FragmentSearchBinding
import com.castcle.android.databinding.ToolbarCastcleCommonBinding
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.ui.base.*

class SearchFragment : BaseFragment<SearchViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentSearchBinding>,
    ToolbarBindingInflater<ToolbarCastcleCommonBinding> {

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

    override fun viewModel(): SearchViewModel =
        ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        binding.empState.bindUiState(EmptyState.SEARCH_EMPTY)
    }

    override fun bindViewEvents() {

    }

    override fun bindViewModel() {

    }
}
