package com.castcle.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.toContentUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.data.staticmodel.FeedFilterMock.feedFilter
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonAdapter
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.common.dialog.recast.KEY_REQUEST
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class FeedFragment : BaseFragment<FeedFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private lateinit var adapterCommon: CommonMockAdapter

    private lateinit var adapterPagingCommon: CommonAdapter

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
    }

    override fun setupView() {
        setupToolbar(viewModel.isGuestMode)
        with(binding) {
            wtWhatYouMind.visibleOrGone(!viewModel.isGuestMode)
            rcFeedFillter.visibleOrGone(!viewModel.isGuestMode)
            ivFilter.visibleOrGone(!viewModel.isGuestMode)
            rvFeedContent.adapter = CommonAdapter().also {
                adapterPagingCommon = it
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

        with(adapterPagingCommon) {
            itemClick.subscribe {
                handleContentClick(it)
            }.addToDisposables()
        }

        with(viewModel) {
            launchOnLifecycleScope {
                feedContentPage.collectLatest {
                    adapterPagingCommon.submitData(it)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            adapterPagingCommon.loadStateFlow.collectLatest { loadStates ->
                val refresher = loadStates.refresh
                val displayEmpty = (refresher is LoadState.NotLoading &&
                    refresher.endOfPaginationReached && adapterPagingCommon.itemCount == 0)
                handleEmptyState(displayEmpty)
            }
        }
    }

    private fun handleEmptyState(show: Boolean) {
        with(binding) {
            rvFeedContent.visibleOrGone(show)
            wtWhatYouMind.visibleOrGone(show)
            clFilter.visibleOrGone(show)
        }
        with(binding.empState) {
            visibleOrGone(!show)
            bindUiState(EmptyState.FEED_EMPTY)
            itemClick.subscribe {
                viewModel.getAllFeedContent()
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
        guestEnable(enable = {
            viewModel.input.updateLikeContent(contentUiModel)
        }, disable = {})
    }

    private fun handleRecastClick(contentUiModel: ContentUiModel) {
        guestEnable(enable = {
            navigateToRecastDialogFragment(contentUiModel)
        }, disable = {})
    }

    private fun navigateToRecastDialogFragment(contentUiModel: ContentUiModel) {
        onBoardNavigator.navigateToRecastDialogFragment(contentUiModel)

        getNavigationResult<ContentUiModel>(
            onBoardNavigator,
            R.id.dialogRecastFragment,
            KEY_REQUEST,
            onResult = {
                adapterPagingCommon.updateStateItemLike(it)
            })
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

//        viewModel.feedContentMock.observe(this, {
//            adapterPagingCommon.uiModels = it
//        })
//
//        viewModel.onUpdateContentLike.subscribe {
//            adapterCommon.onUpdateItem(it)
//        }.addToDisposables()
    }

    private fun handleContentClick(click: Click) {
        when (click) {
            is FeedItemClick.FeedAvatarClick -> {
                handleNavigateAvatarClick(click.contentUiModel)
            }
            is FeedItemClick.FeedLikeClick -> {
                handleLikeClick(click.contentUiModel)
            }
            is FeedItemClick.FeedRecasteClick -> {
                handleRecastClick(click.contentUiModel)
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

    private fun guestEnable(disable: () -> Unit, enable: () -> Unit) {
        if (viewModel.isGuestMode) {
            disable.invoke()
        } else {
            enable.invoke()
        }
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
    }
}
