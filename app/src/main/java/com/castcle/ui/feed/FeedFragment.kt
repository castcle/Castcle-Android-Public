package com.castcle.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.toContentUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.data.staticmodel.FeedFilterMock.feedFilter
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import javax.inject.Inject

class FeedFragment : BaseFragment<FeedFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private lateinit var adapterCommon: CommonMockAdapter

    private var adapterFilterAdapter = FeedFilterAdapter()

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
        with(binding.tbProfile) {
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
        adapterFilterAdapter.items = feedFilter
        adapterFilterAdapter.itemClick.subscribe(::onSelectedFilterClick)?.addToDisposables()

        with(adapterCommon) {
            itemClick.subscribe {
                handleContentClick(it)
            }.addToDisposables()
        }
    }

    private fun handleNavigateAvatarClick(contentUiModel: ContentUiModel) {
        val deepLink = makeDeepLinkUrl(
            requireContext(), Input(
                type = DeepLinkTarget.USER_PROFILE_YOU,
                contentData = contentUiModel.payLoadUiModel.author.displayName
            )
        ).toString()
        navigateByDeepLink(deepLink)
    }

    private fun handleLikeClick(contentUiModel: ContentUiModel) {
        adapterCommon.onUpdateItem(contentUiModel)
        viewModel.input.updateLikeContent(contentUiModel.payLoadUiModel.author.displayName)
    }

    private fun onSelectedFilterClick(itemFilter: FilterUiModel) {
        adapterFilterAdapter.selectedFilter(itemFilter)
    }

    override fun bindViewModel() {
        viewModel.userProfile.observe(this, {
            onBindWhatYouMind(it)
        })

        activityViewModel.user.subscribe {
            onRefreshProfile()
        }.addToDisposables()

        viewModel.getFeedResponseMock()

        viewModel.feedContentMock.observe(this, {
            adapterCommon.uiModels = it
        })
    }

    private fun handleContentClick(click: Click) {
        when (click) {
            is FeedItemClick.FeedAvatarClick -> {
                handleNavigateAvatarClick(click.contentUiModel)
            }
            is FeedItemClick.FeedLikeClick -> {
                handleLikeClick(click.contentUiModel)
            }
        }
    }

    private fun onBindWhatYouMind(user: User) {
        user.toContentUiModel().apply {
            deepLink = makeDeepLinkUrl(
                requireContext(), Input(
                    type = DeepLinkTarget.USER_PROFILE_ME,
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
