package com.castcle.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.databinding.FragmentFeedBinding
import com.castcle.android.databinding.ToolbarCastcleCommonBinding
import com.castcle.ui.base.*

class FeedFragment : BaseFragment<FeedFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedBinding>,
    ToolbarBindingInflater<ToolbarCastcleCommonBinding> {

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleCommonBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleCommonBinding.inflate(inflater, container, attachToRoot)
        }

    override val toolbarBinding: ToolbarCastcleCommonBinding
        get() = toolbarViewBinding as ToolbarCastcleCommonBinding

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFeedBinding
        get() = { inflater, container, attachToRoot ->
            FragmentFeedBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentFeedBinding
        get() = viewBinding as FragmentFeedBinding

    override fun viewModel(): FeedFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(FeedFragmentViewModel::class.java)

    override fun initViewModel() {
    }

    override fun setupView() {
    }

    override fun bindViewEvents() {
    }

    override fun bindViewModel() {
    }
}
