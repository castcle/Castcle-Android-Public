package com.castcle.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.components_android.ui.custom.event.TemplateEventClick
import com.castcle.data.staticmodel.FeedContentType
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonAdapter
import com.castcle.ui.common.dialog.recast.KEY_REQUEST
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeedFragment : BaseFragment<FeedFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var adapterPagingCommon: CommonAdapter

    private var adapterFilterAdapter = FeedFilterAdapter()

    private lateinit var baseContentUiModel: ContentUiModel

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
        viewModel.getTopTrends()
    }

    override fun setupView() {
        startLoadingShimmer()
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
            tvToolbarTitle.text = localizedResources.getString(R.string.feed_title_toolbar)
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
        with(adapterFilterAdapter) {
            itemClick.subscribe(::onSelectedFilterClick)?.addToDisposables()
        }

        with(adapterPagingCommon) {
            itemClick.subscribe {
                handleContentClick(it)
            }.addToDisposables()
        }

        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                feedContentPage.collectLatest {
                    adapterPagingCommon.submitData(it)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            adapterPagingCommon.loadStateFlow.collectLatest { loadStates ->
                val refresher = loadStates.refresh
                val isError = loadStates.refresh is LoadState.Error
                val isLoading = loadStates.refresh is LoadState.Loading
                val displayEmpty = (refresher is LoadState.NotLoading &&
                    !refresher.endOfPaginationReached && adapterPagingCommon.itemCount == 0)
                handleEmptyState(displayEmpty)
                if (!isLoading) {
                    binding.swiperefresh.isRefreshing = false
                    stopLoadingShimmer()
                }
            }
        }

        with(binding.empState) {
            itemClick.subscribe {
                handleRefreshFeed(it)
            }.addToDisposables()
        }

        with(binding.swiperefresh) {
            setOnRefreshListener {
                adapterPagingCommon.refresh()
            }
        }

        viewModel.onUpdateContentLike.subscribe().addToDisposables()
    }

    private fun handleEmptyState(show: Boolean) {
        with(binding) {
            clFilter.visibleOrGone(!show)
            rvFeedContent.visibleOrGone(!show)
        }
        with(binding.empState) {
            visibleOrGone(show)
            bindUiState(EmptyState.FEED_EMPTY)
        }
    }

    private fun startLoadingShimmer() {
        with(binding) {
            skeletonLoading.shimmerLayoutLoading.run {
                startShimmer()
                visible()
            }
        }
    }


    private fun stopLoadingShimmer() {
        with(binding) {
            skeletonLoading.shimmerLayoutLoading.run {
                stopShimmer()
                setShimmer(null)
                gone()
            }
        }
    }

    private fun handleRefreshFeed(it: TemplateEventClick?) {
        if (it is TemplateEventClick.ReTryClick) {
            viewModel.input.setDefaultFeedRequestHeader()
            adapterFilterAdapter.selectedFilterDefault()
        }
    }

    private fun handleNavigateAvatarClick(contentUiModel: ContentUiModel) {
        val deeplinkType = if (contentUiModel.payLoadUiModel.author.castcleId ==
            viewModel.userProfile.value?.castcleId
        ) {
            ProfileType.PROFILE_TYPE_ME.type
        } else {
            contentUiModel.payLoadUiModel.author.type
        }
        navigateToProfile(contentUiModel.payLoadUiModel.author.castcleId, deeplinkType)
    }

    private fun handleLikeClick(contentUiModel: ContentUiModel) {
        guestEnable(enable = {
            baseContentUiModel = contentUiModel
            val likeContentRequest = LikeContentRequest(
                contentId = contentUiModel.payLoadUiModel.contentId,
                feedItemId = contentUiModel.payLoadUiModel.contentId,
                authorId = viewModel.userProfile.value?.castcleId ?: "",
                likeStatus = contentUiModel.payLoadUiModel.likedUiModel.liked
            )
            if (!contentUiModel.payLoadUiModel.likedUiModel.liked) {
                adapterPagingCommon.updateStateItemLike(contentUiModel)
            } else {
                adapterPagingCommon.updateStateItemUnLike(contentUiModel)
            }
            viewModel.input.updateLikeContent(likeContentRequest)
        }, disable = {
            navigateToNotiflyLoginDialog()
        })
    }

    private fun handleRecastClick(contentUiModel: ContentUiModel) {
        guestEnable(enable = {
            navigateToRecastDialogFragment(contentUiModel)
        }, disable = {
            navigateToNotiflyLoginDialog()
        })
    }

    private fun handleCommentClick(contentUiModel: ContentUiModel) {
        guestEnable(enable = {
            navigateToFeedDetailFragment(contentUiModel)
        }, disable = {
            navigateToNotiflyLoginDialog()
        })
    }

    private fun navigateToRecastDialogFragment(contentUiModel: ContentUiModel) {
        onBoardNavigator.navigateToRecastDialogFragment(contentUiModel)

        getNavigationResult<ContentUiModel>(
            onBoardNavigator,
            R.id.dialogRecastFragment,
            KEY_REQUEST,
            onResult = {
                adapterPagingCommon.updateStateItemRecast(it)
            })
    }

    private fun navigateToFeedDetailFragment(contentUiModel: ContentUiModel) {
        onBoardNavigator.navigateToFeedDetailFragment(contentUiModel)
    }

    private fun onSelectedFilterClick(itemFilter: SearchUiModel) {
        adapterFilterAdapter.selectedFilter(itemFilter)
        if (itemFilter is SearchUiModel.SearchHasTagUiModel) {
            val feedRequestHeader = FeedRequestHeader(
                featureSlug = FeedContentType.FEED_SLUG.type,
                circleSlug = FeedContentType.CIRCLE_SLUG_FORYOU.type,
                hashtag = itemFilter.slug
            )
            viewModel.setFetchFeedContent(feedRequestHeader)
        }
    }

    override fun bindViewModel() {
        viewModel.userProfile.observe(this, {
            onBindWhatYouMind(it)
        })

        activityViewModel.userRefeshProfile.subscribe {
            onBindWhatYouMindData(it)
        }.addToDisposables()

        viewModel.onError.subscribe {
            handleOnError(it)
        }.addToDisposables()

        viewModel.trendsResponse.observe(this, {
            onBindFilterItem(it)
        })
    }

    private fun onBindFilterItem(filterItems: List<SearchUiModel>) {
        adapterFilterAdapter.items = filterItems
    }

    private fun handleOnError(throwable: Throwable) {
        displayError(throwable)
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
            is FeedItemClick.FeedCommentClick -> {
                handleCommentClick(click.contentUiModel)
            }
            is FeedItemClick.FeedImageClick -> {
                handleImageItemClick(click.position, click.contentUiModel)
            }
        }
    }

    private fun handleImageItemClick(position: Int, contentUiModel: ContentUiModel) {
        val image = contentUiModel.payLoadUiModel.photo.imageContent.map {
            it.imageFullHd
        }
        val imagePosition = when (contentUiModel.payLoadUiModel.photo.imageContent.size) {
            1 -> 0
            else -> position
        }
        StfalconImageViewer.Builder(context, image, ::loadPosterImage)
            .withStartPosition(imagePosition)
            .allowSwipeToDismiss(true)
            .allowZooming(true)
            .show()
    }

    private fun loadPosterImage(imageView: ImageView, imageUrl: String) {
        imageView.loadImageWithoutTransformation(imageUrl)

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
            navigateToProfile(user.castcleId, ProfileType.PROFILE_TYPE_ME.type)
        }.addToDisposables()
    }

    private fun onBindWhatYouMindData(user: User) {
        user.toContentUiModel().apply {
            deepLink = makeDeepLinkUrl(
                requireContext(), Input(
                    type = DeepLinkTarget.USER_PROFILE_ME,
                    contentData = user.castcleId
                )
            ).toString()
        }.run(binding.wtWhatYouMind::bindUiModel)
    }

    private fun navigateToProfile(castcleId: String, profileType: String) {
        onBoardNavigator.navigateToProfileFragment(castcleId, profileType)
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
        viewModel.fetchUserProfile()
    }
}
