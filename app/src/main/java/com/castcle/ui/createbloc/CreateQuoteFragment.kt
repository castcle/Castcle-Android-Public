package com.castcle.ui.createbloc

import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCreateQuoteBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.userprofile.CreateContentUiModel
import com.castcle.common_model.model.userprofile.MentionUiModel
import com.castcle.components_android.ui.custom.mention.MentionView
import com.castcle.components_android.ui.custom.mention.adapter.MentionArrayAdapter
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.createbloc.adapter.CommonQuoteCastAdapter
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


class CreateQuoteFragment : BaseFragment<CreateBlogFragmentViewModel>(),
    BaseFragmentCallbacks,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding>,
    ViewBindingInflater<FragmentCreateQuoteBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

    private lateinit var commonMockAdapter: CommonQuoteCastAdapter

    private val quoteFragmentArgs: CreateQuoteFragmentArgs by navArgs()

    private val currentContent: ContentFeedUiModel
        get() = quoteFragmentArgs.contentUiModel

    private val profileBundle: ProfileBundle
        get() = quoteFragmentArgs.profileBundle

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateQuoteBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCreateQuoteBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCreateQuoteBinding
        get() = viewBinding as FragmentCreateQuoteBinding

    override fun viewModel(): CreateBlogFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(CreateBlogFragmentViewModel::class.java)


    override fun onResume() {
        super.onResume()
        with(requireActivity()) {
            softInputMode = getSoftInputMode()
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onPause() {
        super.onPause()
        with(requireActivity()) {
            hideSoftKeyboard()
            setSoftInputMode(softInputMode)
        }
    }

    override fun initViewModel() {
    }

    override fun setupView() {
        setupToolBar()

        onBindMessageCount(Pair(0, MAX_LIGHTH))

        (context as OnBoardActivity).onGoneBottomNavigate()

        addOnBackPressedCallback {
            navigateToFeed()
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.invisible()
            tvToolbarTitle.text = localizedResources.getString(R.string.create_blog_toolbar_title)
            requireContext().getColorResource(R.color.white).let {
                tvToolbarTitle.setTextColor(it)
            }
            ivToolbarLogoButton
                .subscribeOnClick {
                    navigateToFeed()
                }.addToDisposables()
        }
    }

    private fun navigateToFeed() {
        onBoardNavigator.findNavController().navigateUp()
    }

    override fun bindViewEvents() {
        onBindUserMention()

        with(binding.rvContent) {
            adapter = CommonQuoteCastAdapter().also {
                commonMockAdapter = it
            }
        }

        commonMockAdapter.uiModels = listOf(currentContent)

        val currentUserPage = profileBundle as ProfileBundle.ReCastPage
        binding.btCast.subscribeOnClick {
            viewModel.input.quoteCasteContent(
                currentContent,
                currentUserPage.castcleId
            )
        }
    }

    private lateinit var mentionAdapter: MentionArrayAdapter
    private var mentionData = listOf<MentionUiModel>()

    private fun onBindUserMention() {
        mentionAdapter = MentionArrayAdapter(requireContext(), mentionData)
        with(binding.etInputMessage) {
            setMentionAdapter(mentionAdapter)
            addTextChangedListener { it ->
                viewModel.input.validateMessage(it.toString())
                    .subscribeBy(
                        onError = ::handleOnError
                    ).addToDisposables()
                setMentionTextChangedListener(
                    object : MentionView.OnChangedListener {
                        override fun onChanged(view: MentionView, text: CharSequence) {
                            fetchMentionUser(text)
                        }
                    }
                )
            }
        }
    }

    private fun fetchMentionUser(text: CharSequence) {
        viewModel.getUserMention(text.toString()).subscribeBy(
            onSuccess = {
                onBindMentionItem(it)
            },
            onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun onBindMentionItem(list: List<MentionUiModel>) {
        mentionData = list
        mentionAdapter.items = list
        mentionAdapter.notifyDataSetChanged()
    }

    override fun bindViewModel() {
        val currentUserPage = profileBundle as ProfileBundle.ReCastPage
        if (currentUserPage.displayName.isEmpty()) {
            viewModel.fetchCastUserProfile()
                .subscribe()
                .addToDisposables()
        } else {
            binding.utUserBar.bindUiModel(currentUserPage)
        }

        viewModel.userProfileUiModel.observe(this, {
            binding.utUserBar.bindUiModel(it)
        })

        viewModel.enableSubmitButton.subscribe {
            enableButtonCast(it)
        }.addToDisposables()

        viewModel.messageLength.subscribe {
            onBindMessageCount(it)
        }.addToDisposables()

        viewModel.onSuccess.subscribe {
            navigateToFeed()
        }.addToDisposables()

        viewModel.onError.subscribe {
            displayError(it)
        }.addToDisposables()

        viewModel.showLoading.subscribe {
            binding.groupLoading.visibleOrGone(it)
        }.addToDisposables()
    }

    private fun onBindMessageCount(messageCount: Pair<Int, Int>) {
        val textFormatCount = "%s"
        with(binding.tvCountChar) {
            if (messageCount.first > com.castcle.ui.createpost.MAX_LIGHTH) {
                setTextColor(requireContext().getColorResource(R.color.red_primary))
                text = "0"
                enableButtonCast(false)
            } else {
                text = textFormatCount.format(messageCount.second)
            }
        }
    }

    private fun handleOnError(case: Throwable) {
        (context as OnBoardActivity).displayError(case)
    }

    private fun handleOnResponse(createContentUiModel: CreateContentUiModel) {
        if (!createContentUiModel.type.isNullOrBlank()) {
            Toast.makeText(activity, "Can not Create Post", Toast.LENGTH_LONG).also {
                it.show()
            }
        } else {
            navigateToFeed()
        }
    }

    private fun enableButtonCast(enable: Boolean) {
        with(binding.btCast) {
            isActivated = enable
            isEnabled = enable
        }
    }
}
