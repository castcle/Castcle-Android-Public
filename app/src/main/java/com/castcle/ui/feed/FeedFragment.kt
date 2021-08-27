package com.castcle.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedBinding
import com.castcle.android.databinding.ToolbarCastcleCommonBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.api.response.FeedResponse
import com.castcle.common_model.model.feed.toContentFeedUiModel
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.gson.Gson
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

class FeedFragment : BaseFragment<FeedFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedBinding>,
    ToolbarBindingInflater<ToolbarCastcleCommonBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator
    private var adapterCommon: CommonMockAdapter? = null

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
        viewModel.getMockFeed()
    }

    override fun setupView() {
        setupToolbar()
        with(binding.rvFeedContent) {
            adapter = CommonMockAdapter().also {
                adapterCommon = it
            }
        }
    }

    private fun setupToolbar() {
        with(toolbarBinding) {
            tvToolbarTitle.text = getString(R.string.feed_title_toolbar)
            ivToolbarProfileButton.subscribeOnClick {
                navigateToNotiflyLoginDialog()
            }
        }
    }

    private fun navigateToNotiflyLoginDialog() {
        onBoardNavigator.navigateToNotiflyLoginDialogFragment()
    }

    override fun bindViewEvents() {
        adapterCommon?.uiModels =
            getFeedResponse().payload.toContentFeedUiModel().feedContentUiModel
    }

    private fun getFeedResponse(): FeedResponse {
        return Gson().fromJson(
            JSONObject(readJSONFromAsset() ?: "").toString(),
            FeedResponse::class.java
        )
    }

    private fun readJSONFromAsset(): String? {
        val json: String?
        try {
            val inputStream: InputStream? = context?.resources?.openRawResource(
                R.raw.feed_mock
            )
            json = inputStream?.bufferedReader().use { it?.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

    override fun bindViewModel() {
    }
}
