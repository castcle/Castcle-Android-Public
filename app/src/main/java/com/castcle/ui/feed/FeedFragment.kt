package com.castcle.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedBinding
import com.castcle.android.databinding.ToolbarCastcleCommonBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.api.response.FeedResponse
import com.castcle.common_model.model.feed.toContentFeedUiModel
import com.castcle.common_model.model.feed.toContentUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.data.staticmodel.FeedFilterMock.feedFilter
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.onboard.OnBoardViewModel
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

    private var adapterFilterAdapter = FeedFilterAdapter()

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

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        if (!viewModel.isGuestMode) {
            viewModel.fetchUserProfile()
                .subscribe()
                .addToDisposables()
        }
        viewModel.getMockFeed()
    }

    override fun setupView() {
        setupToolbar(viewModel.isGuestMode)
        with(binding) {
            wtWhatYouMind.visibleOrGone(!viewModel.isGuestMode)
            rcFeedFillter.visibleOrGone(!viewModel.isGuestMode)
            rvFeedContent.adapter = CommonMockAdapter().also {
                adapterCommon = it
            }

            with(rcFeedFillter) {
                adapter = adapterFilterAdapter
                setupHorizontalSnapCarousel(
                    spacing = R.dimen._6sdp
                )
            }
        }
    }

    private fun setupToolbar(guestMode: Boolean) {
        with(toolbarBinding) {
            tvToolbarTitle.text = getString(R.string.feed_title_toolbar)
            if (guestMode) {
                ivToolbarProfileButton.subscribeOnClick {
                    navigateToNotiflyLoginDialog()
                }
            } else {
                ivToolbarProfileButton.setImageDrawable(
                    context?.getDrawableRes(
                        R.drawable.ic_hamburger
                    )
                )
                ivToolbarProfileButton.subscribeOnClick {
                    navigateToSettingFragment()
                }
            }
        }
    }

    private fun navigateToNotiflyLoginDialog() {
        onBoardNavigator.navigateToNotiflyLoginDialogFragment()
    }

    private fun navigateToSettingFragment() {
        onBoardNavigator.navigateToSettingFragment()
    }

    override fun bindViewEvents() {
        val mock = getFeedResponse().payload.toContentFeedUiModel().feedContentUiModel

        adapterCommon?.uiModels = mock
        adapterFilterAdapter.items = feedFilter
        adapterFilterAdapter.itemClick.subscribe(::onSelectedFilterClick)?.addToDisposables()
    }

    private fun onSelectedFilterClick(itemFilter: FilterUiModel) {
        adapterFilterAdapter.selectedFilter(itemFilter)
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
        viewModel.userProfile.observe(this, {
            onBindWhatYouMind(it)
        })

        activityViewModel.user.subscribe {
            onRefreshProfile()
        }.addToDisposables()
    }

    private fun onBindWhatYouMind(user: User) {
        user.toContentUiModel().apply {
            deepLink = makeDeepLinkUrl(
                requireContext(), Input(
                    type = DeepLinkTarget.USER_PROFILE,
                    contentData = user.castcleId
                )
            ).toString()
        }.run(binding.wtWhatYouMind::bindUiModel)

        binding.wtWhatYouMind.clickStatus.subscribe {
            handleNavigateOnClick(it)
        }.addToDisposables()
    }

    private fun handleNavigateOnClick(click: TemplateClicks) {
        when (click) {
            is TemplateClicks.AvatarClick -> {
                navigateByDeepLink(click.deepLink)
            }
            else -> {
            }
        }
    }

    private fun navigateByDeepLink(url: String) {
        onBoardNavigator.navigateByDeepLink(url.toUri())
    }

    private fun onRefreshProfile() {
        setupView()
        bindViewEvents()
    }
}
