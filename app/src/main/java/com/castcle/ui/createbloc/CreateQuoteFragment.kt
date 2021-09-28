package com.castcle.ui.createbloc

import android.net.Uri
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCreateQuoteBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.feed.api.response.FeedResponse
import com.castcle.common_model.model.feed.toContentFeedUiModel
import com.castcle.common_model.model.userprofile.CreateContentUiModel
import com.castcle.components_android.ui.custom.mention.MentionView
import com.castcle.components_android.ui.custom.mention.adapter.MentionArrayAdapter
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.createbloc.adapter.CommonQuoteCastAdapter
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.gson.Gson
import io.reactivex.rxkotlin.subscribeBy
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject


class CreateQuoteFragment : BaseFragment<CreateBlogFragmentViewModel>(),
    BaseFragmentCallbacks,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding>,
    ViewBindingInflater<FragmentCreateQuoteBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

    private lateinit var commonMockAdapter: CommonQuoteCastAdapter

    private val quoteFragmentArgs: CreateQuoteFragmentArgs by navArgs()

    private val currentCoutent: ContentUiModel
        get() = quoteFragmentArgs.contentUiModel

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

            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
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
            tvToolbarTitleAction.gone()
            tvToolbarTitle.text = context?.getString(R.string.create_blog_toolbar_title)
            context?.getColorResource(R.color.white)?.let {
                tvToolbarTitle.setTextColor(it)
            }
            ivToolbarLogoButton
                .subscribeOnClick {
                    navigateToFeed()
                }.addToDisposables()
        }
    }

    private fun navigateToFeed() {
        makeDeepLinkUrl(
            requireContext(),
            Input("", DeepLinkTarget.HOME_FEED)
        ).run(::navigateByDeepLink)
    }

    private fun navigateByDeepLink(url: Uri) {
        onBoardNavigator.navigateByDeepLink(url, true)
    }

    override fun bindViewEvents() {
        var mentionData = listOf<ContentUiModel>()
        val mentionAdapter = MentionArrayAdapter(requireContext(), mentionData)
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
                            fetchMentionUser().run {
                                mentionData = this
                                mentionAdapter.items = this
                                mentionAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                )
            }
        }

        with(binding.rvContent) {
            adapter = CommonQuoteCastAdapter().also {
                commonMockAdapter = it
            }
        }

        commonMockAdapter.uiModels = listOf(currentCoutent)

        binding.btCast.subscribeOnClick {
            viewModel.input.quoteCasteContent(currentCoutent)
        }
    }

    override fun bindViewModel() {
        if (!viewModel.isGuestMode) {
            viewModel.fetchCastUserProfile()
                .subscribe()
                .addToDisposables()
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
    }

    private fun onBindMessageCount(messageCount: Pair<Int, Int>) {
        val textFormatCount = "%s/%d"
        with(binding.tvCountChar) {
            text = if (messageCount.first > messageCount.second) {
                setTextColor(requireContext().getColorResource(R.color.red_primary))
                "${messageCount.first - MAX_LIGHTH}"
            } else {
                textFormatCount.format(messageCount.first, messageCount.second)
            }
        }
    }

    private fun fetchMentionUser(): MutableList<ContentUiModel> {
        return getFeedResponse().payload.toContentFeedUiModel().feedContentUiModel
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
