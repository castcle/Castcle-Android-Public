package com.castcle.ui.feed

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.R
import com.castcle.android.databinding.FragmentFeedBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.empty.EmptyState
import com.castcle.common_model.model.feed.*
import com.castcle.common_model.model.feed.converter.LikeContentRequest
import com.castcle.common_model.model.login.domain.CreatePostBundle
import com.castcle.common_model.model.search.SearchUiModel
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.components_android.ui.custom.event.TemplateEventClick
import com.castcle.data.staticmodel.FeedContentType
import com.castcle.data.staticmodel.ModeType
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonAdapter
import com.castcle.ui.common.LoaderStateAdapter
import com.castcle.ui.common.dialog.dialogeditcontent.EditContentState
import com.castcle.ui.common.dialog.dialogeditcontent.KEY_CHOOSE_EDIT_REQUEST
import com.castcle.ui.common.dialog.recast.*
import com.castcle.ui.common.dialog.user.KEY_USER_CHOOSE_REQUEST
import com.castcle.ui.common.dialog.user.UserState
import com.castcle.ui.common.events.Click
import com.castcle.ui.common.events.FeedItemClick
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.util.RemotePresentationState
import com.castcle.ui.util.asRemotePresentationState
import com.facebook.shimmer.Shimmer
import com.stfalcon.imageviewer.StfalconImageViewer
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeedFragment : BaseFragment<FeedFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentFeedBinding> {

    @Inject
    lateinit var onBoardNavigator: OnBoardNavigator

    @Inject
    lateinit var localizedResources: LocalizedResources

    private lateinit var adapterPagingCommon: CommonAdapter

    private var adapterFilterAdapter = FeedFilterAdapter()

    private var adapterLoadState = LoaderStateAdapter()

    private lateinit var baseContentUiModel: ContentFeedUiModel

    private var cacheContentUiModel: ContentFeedUiModel? = null

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
                .subscribeBy(
                    onError = {
                        displayError(it)
                    }
                ).addToDisposables()
        }
    }

    override fun setupView() {
        setupToolbar(viewModel.isGuestMode)
        with(binding) {
            wtWhatYouMind.visibleOrGone(!viewModel.isGuestMode)
            val itemAnimator: DefaultItemAnimator = object : DefaultItemAnimator() {
                override fun canReuseUpdatedViewHolder(
                    viewHolder: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
            }

            rvFeedContent.adapter = CommonAdapter().also {
                adapterPagingCommon = it
                adapterPagingCommon.withLoadStateFooter(adapterLoadState)
            }
            rvFeedContent.itemAnimator = itemAnimator
            rvFeedContent.recycledViewPool.setMaxRecycledViews(0, 0)
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
                    navigateToNotifyLoginDialog()
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
                ivToolbarLogoButton.subscribeOnClick {
                    refreshPosition()
                }.addToDisposables()
            }
        }
    }

    private fun refreshPosition() {
        binding.rvFeedContent.scrollToPosition(0)
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

        val notLoading = adapterPagingCommon.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        lifecycleScope.launch {
            notLoading.collect {
                stopLoadingShimmer()
            }
        }

        with(viewModel) {
            viewLifecycleOwner.lifecycleScope.launch {
                feedContentPage.distinctUntilChanged().collectLatest {
                    adapterPagingCommon.submitData(lifecycle, it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapterPagingCommon.loadStateFlow.collectLatest { loadStates ->
                val refresher = loadStates.refresh
                val isError = loadStates.refresh is LoadState.Error
                val isLoading = loadStates.refresh is LoadState.Loading
                val isLoadingRemote = loadStates.mediator?.refresh is LoadState.Loading

                val isNothingLoad = adapterPagingCommon.itemCount == 0

                if (isError) {
                    handleEmptyState(isError)
                    stopLoadingShimmer()
                }

                if (viewModel.isGuestMode) {
                    if (isLoading) {
                        binding.empState.gone()
                        handleEmptyState(!isLoading)
                        if (isNothingLoad) {
                            startLoadingShimmer()
                        }
                    } else {
                        binding.swiperefresh.isRefreshing = false
                        handleEmptyState(isLoading)
                        stopLoadingShimmer()
                    }
                } else {
                    if (isLoadingRemote) {
                        binding.empState.gone()
                        handleEmptyState(!isLoadingRemote)
                        if (isNothingLoad) {
                            startLoadingShimmer()
                        } else {
                            stopLoadingShimmer()
                        }
                    } else {
                        binding.swiperefresh.isRefreshing = false
                        handleEmptyState(isLoadingRemote)
                        stopLoadingShimmer()
                    }
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

        activityViewModel.onRefreshPositionRes.observe(viewLifecycleOwner, {
            refreshPosition()
        })

        getNavigationResult<Int>(
            onBoardNavigator,
            R.id.feedFragment,
            KEY_REQUEST_COMMENTED_COUNT,
            onResult = {
                cacheContentUiModel?.let { it1 ->
                    onBindCommentedCount(it, it1)
                }
            })
    }

    override fun bindViewModel() {
        viewModel.showLoading.subscribe {
            handlerShowLoading(it)
        }.addToDisposables()

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

        viewModel.castPostResponse.observe(viewLifecycleOwner, {
            adapterPagingCommon.refresh()
        })

    }

    private fun navigateToNotifyLoginDialog() {
        onBoardNavigator.navigateToNotiflyLoginDialogFragment()
    }

    private fun navigateToSettingFragment() {
        onBoardNavigator.navigateToSettingFragment()
    }

    private fun handleEmptyState(show: Boolean) {
        with(binding) {
            clFilter.visibleOrGone(!show)
            rvFeedContent.visibleOrGone(!show)
        }
    }

    private fun startLoadingShimmer() {
        val shimmerBuilder = Shimmer.AlphaHighlightBuilder()

        with(binding) {
            skeletonLoading.shimmerLayoutLoading.run {
                setShimmer(
                    shimmerBuilder.setDirection(Shimmer.Direction.LEFT_TO_RIGHT).setTilt(0f).build()
                )
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

    private fun navigateToRecastDialogFragment(contentUiModel: ContentFeedUiModel) {
        onBoardNavigator.navigateToRecastDialogFragment(contentUiModel)

        if (contentUiModel.recasted) {
            getNavigationResult<ContentFeedUiModel>(
                onBoardNavigator,
                R.id.feedFragment,
                KEY_REQUEST_UNRECAST,
                onResult = {
                    onRecastContent(it)
                })
        } else {
            getNavigationResult<ContentFeedUiModel>(
                onBoardNavigator,
                R.id.feedFragment,
                KEY_REQUEST,
                onResult = {
                    onRecastContent(it, true)
                })
        }
    }

    private fun onRecastContent(currentContent: ContentFeedUiModel, onRecast: Boolean = false) {
        viewModel.input.recastContent(currentContent).subscribeBy(
            onError = {
                displayError(it)
            }
        ).addToDisposables()
        handlerUpdateRecasted(currentContent, onRecast)
    }

    private fun handlerUpdateRecasted(
        currentContent: ContentFeedUiModel,
        onRecast: Boolean
    ) {
        if (onRecast) {
            adapterPagingCommon.updateStateItemRecast(currentContent)
        } else {
            adapterPagingCommon.updateStateItemUnRecast(currentContent)
        }
    }

    private fun navigateToFeedDetailFragment(contentUiModel: ContentFeedUiModel) {
        onBoardNavigator.navigateToFeedDetailFragment(contentUiModel)
    }

    private fun onBindCommentedCount(count: Int, contentUiModel: ContentFeedUiModel) {
        cacheContentUiModel = null
        adapterPagingCommon.updateCommented(count, contentUiModel)
    }

    private fun onSelectedFilterClick(itemFilter: SearchUiModel) {
        adapterFilterAdapter.selectedFilter(itemFilter)
        if (itemFilter is SearchUiModel.SearchHasTagUiModel) {
            val feedRequestHeader = FeedRequestHeader(
                featureSlug = FeedContentType.FEED_SLUG.type,
                circleSlug = FeedContentType.CIRCLE_SLUG_FORYOU.type,
                hashtag = itemFilter.slug,
                mode = ModeType.MODE_CURRENT.type,
                isMeId = viewModel.userProfile.value?.castcleId ?: ""
            )
            viewModel.setFetchFeedContent(feedRequestHeader)
        }
    }

    private fun onBindCastPostSuccess(content: ContentFeedUiModel) {
        adapterPagingCommon.updateContentPost(content)
        binding.rvFeedContent.scrollToPosition(0)
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
            is FeedItemClick.EditContentClick -> {
                handleEditContentClick(click.contentUiModel)
            }
            is FeedItemClick.FeedFollowingClick -> {
                handleFeedFollowingClick(click.contentUiModel)
            }
            is FeedItemClick.WebContentClick -> {
                handleWebContentClick(click.contentUiModel)
            }
            is FeedItemClick.WebContentMessageClick -> {
                handleWebContentMessageClick(click)
            }
        }
    }

    private fun handleWebContentMessageClick(click: FeedItemClick.WebContentMessageClick) {
        openWebView(click.url)
    }

    private fun handleWebContentClick(contentUiModel: ContentFeedUiModel) {
        contentUiModel.link?.url?.let {
            openWebView(it)
        }
    }

    private fun openWebView(url: String) {
        (context as Activity).openUri(url)
    }

    private fun handleFeedFollowingClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            activityViewModel.putToFollowUser(
                contentUiModel.userContent.castcleId,
                contentUiModel.authorId
            ).subscribeBy(
                onComplete = {
                    displayMessage(
                        localizedResources.getString(R.string.feed_content_following_status)
                            .format(contentUiModel.userContent.castcleId)
                    )
                    adapterPagingCommon.updateStateItemFollowing(contentUiModel)
                }, onError = {
                    displayError(it)
                }
            ).addToDisposables()
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun handleNavigateAvatarClick(contentUiModel: ContentFeedUiModel) {
        var profileType = ""
        checkContentIsMe(contentUiModel.userContent.castcleId,
            onPage = {
                profileType = ProfileType.PROFILE_TYPE_PAGE_ME.type
            }, onMe = {
                profileType = ProfileType.PROFILE_TYPE_ME.type
            }, onView = {
                profileType = contentUiModel.userContent.type
            }
        )

        navigateToProfile(contentUiModel.userContent.castcleId, profileType)
    }

    private fun handleLikeClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            baseContentUiModel = contentUiModel
            val likeContentRequest = LikeContentRequest(
                contentId = contentUiModel.contentId,
                feedItemId = contentUiModel.id,
                authorId = viewModel.userProfile.value?.castcleId ?: "",
                likeStatus = contentUiModel.liked
            )
            if (!contentUiModel.liked) {
                adapterPagingCommon.updateStateItemLike(contentUiModel)
            } else {
                adapterPagingCommon.updateStateItemUnLike(contentUiModel)
            }
            viewModel.input.updateLikeContent(likeContentRequest)
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun handleRecastClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            navigateToRecastDialogFragment(contentUiModel)
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun handleCommentClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            cacheContentUiModel = contentUiModel
            navigateToFeedDetailFragment(contentUiModel)
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun handleImageItemClick(position: Int, contentUiModel: ContentFeedUiModel) {
        val image = contentUiModel.photo?.map {
            it.imageMedium
        }
        val imagePosition = when (contentUiModel.photo?.size) {
            1 -> 0
            else -> position
        }
        StfalconImageViewer.Builder(context, image, ::loadPosterImage)
            .withStartPosition(imagePosition)
            .withHiddenStatusBar(true)
            .allowSwipeToDismiss(true)
            .allowZooming(true)
            .show()
    }

    private fun handleEditContentClick(contentUiModel: ContentFeedUiModel) {
        guestEnable(enable = {
            getNavigationResult<UserState>(
                onBoardNavigator,
                R.id.feedFragment,
                KEY_USER_CHOOSE_REQUEST,
                onResult = {
                    onHandlerUserChoose(it, contentUiModel)
                })
            onNavigateToChooseUserEdit(contentUiModel)
        }, disable = {
            navigateToNotifyLoginDialog()
        })
    }

    private fun handleOptionalContentClick(contentUiModel: ContentFeedUiModel) {
        getNavigationResult<EditContentState>(
            onBoardNavigator,
            R.id.feedFragment,
            KEY_CHOOSE_EDIT_REQUEST,
            onResult = {
                handlerOptionalState(it, contentUiModel)
            })
        checkContentIsMe(contentUiModel.userContent.castcleId, onMe = {
            onBoardNavigator.navigateToEditContentDialogFragment()
        }, onView = {

        }, onPage = {
            onBoardNavigator.navigateToEditContentDialogFragment()
        })
    }

    private fun handlerOptionalState(
        editContentState: EditContentState,
        contentUiModel: ContentFeedUiModel
    ) {
        checkContentIsMe(contentUiModel.userContent.castcleId, onMe = {
            if (editContentState == EditContentState.DELETE_CONTENT) {
                viewModel.input.deleteContentFeed(
                    contentId = contentUiModel.contentId
                ).subscribeBy(
                    onComplete = {
                        onBindDeleteContentItem(contentUiModel)
                    }, onError = {
                        displayError(it)
                    }
                ).addToDisposables()
            } else {
                viewModel.input.deleteContentFeed(
                    contentId = contentUiModel.contentId
                ).subscribeBy(
                    onComplete = {
                        onBindDeleteContentItem(contentUiModel)
                    }, onError = {
                        displayError(it)
                    }
                ).addToDisposables()
            }
        }, onPage = {

        }, onView = {})
    }

    private fun onBindDeleteContentItem(contentUiModel: ContentFeedUiModel) {
        adapterPagingCommon.updateDeleteContent(contentUiModel)
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
            handlerWhatYouMindClick(it, user)
        }.addToDisposables()
    }

    private fun handlerWhatYouMindClick(it: TemplateClicks?, user: User) {
        when (it) {
            is TemplateClicks.CreatePostClick -> {
                val createPostBundle = CreatePostBundle.CreatePostProfileBundle(
                    displayName = user.displayName,
                    castcleId = user.castcleId,
                    avaterUrl = user.avatar,
                    verifyed = user.verified
                )

                navigateToCreatePost(createPostBundle)
            }
            is TemplateClicks.AvatarClick -> {
                navigateToProfile(user.castcleId, ProfileType.PROFILE_TYPE_ME.type)
            }
            else -> {}
        }
    }

    private fun navigateToCreatePost(createPostBundle: CreatePostBundle) {
        onBoardNavigator.navigateToCreatePostFragment(createPostBundle, true)
    }

    private fun checkContentIsMe(
        castcleId: String,
        onMe: () -> Unit,
        onPage: () -> Unit,
        onView: () -> Unit
    ) {
        viewModel.checkContentIsMe(castcleId,
            onProfileMe = {
                onMe.invoke()
            }, onPageMe = {
                onPage.invoke()
            }, non = {
                onView.invoke()
            })
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

    private fun onNavigateToChooseUserEdit(contentUiModel: ContentFeedUiModel) {
        onBoardNavigator.navigateToUserChooseDialogFragment(contentUiModel.userContent.displayName)
    }

    private fun onNavigateToReportProfile(
        castcleId: String,
        profileType: String,
        displayName: String
    ) {
        activity?.runOnUiThread {
            onBoardNavigator.navigateToReportFragment(castcleId, profileType, displayName, true)
        }
    }

    private fun handlerShowLoading(it: Boolean) {
        binding.flCoverLoading.visibleOrGone(it)
    }

    private fun onHandlerUserChoose(state: UserState, contentFeedUiModel: ContentFeedUiModel) {
        when (state) {
            UserState.SHARE -> {
                requireActivity().let {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, "share something")
                    it.startActivity(Intent.createChooser(intent, "share"))
                }
            }

            UserState.BLOCK -> {
                viewModel.blockUser(contentFeedUiModel.authorId).subscribeBy(
                    onComplete = {
                        var profileType = ""
                        checkContentIsMe(contentFeedUiModel.userContent.castcleId,
                            onPage = {
                                profileType = ProfileType.PROFILE_TYPE_PAGE_ME.type
                            }, onMe = {
                                profileType = ProfileType.PROFILE_TYPE_ME.type
                            }, onView = {
                                profileType = ProfileType.PROFILE_TYPE_PEOPLE.type
                            }
                        )
                        navigateToProfile(contentFeedUiModel.userContent.castcleId, profileType)
                    },
                    onError = {
                        displayError(it)
                    }
                ).addToDisposables()
            }

            UserState.REPORT -> {
                viewModel.reportContent(contentFeedUiModel.contentId).subscribeBy(
                    onComplete = {
                        var profileType = ""
                        checkContentIsMe(contentFeedUiModel.userContent.castcleId,
                            onPage = {
                                profileType = ProfileType.PROFILE_TYPE_PAGE_ME.type
                            }, onMe = {
                                profileType = ProfileType.PROFILE_TYPE_ME.type
                            }, onView = {
                                profileType = ProfileType.PROFILE_TYPE_PEOPLE.type
                            }
                        )
                        onNavigateToReportProfile(
                            contentFeedUiModel.userContent.castcleId,
                            profileType,
                            contentFeedUiModel.userContent.displayName
                        )
                    },
                    onError = {
                        displayError(it)
                    }
                ).addToDisposables()
            }
        }
    }

    private fun guestEnable(disable: () -> Unit, enable: () -> Unit) {
        if (viewModel.isGuestMode) {
            disable.invoke()
        } else {
            enable.invoke()
        }
    }
}
