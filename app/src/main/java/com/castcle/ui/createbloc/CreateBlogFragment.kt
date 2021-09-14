package com.castcle.ui.createbloc

import android.net.Uri
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCreateBlocBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.userprofile.CreateContentUiModel
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class CreateBlogFragment : BaseFragment<CreateBlogFragmentViewModel>(),
    BaseFragmentCallbacks,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding>,
    ViewBindingInflater<FragmentCreateBlocBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateBlocBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCreateBlocBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCreateBlocBinding
        get() = viewBinding as FragmentCreateBlocBinding

    override fun viewModel(): CreateBlogFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(CreateBlogFragmentViewModel::class.java)

    override fun onResume() {
        super.onResume()
        with(requireActivity()) {
            registerKeyboardListener(contentKeyboardListener)
            softInputMode = getSoftInputMode()
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onPause() {
        super.onPause()
        with(requireActivity()) {
            hideSoftKeyboard()
            setSoftInputMode(softInputMode)
            unregisterKeyboardListener(contentKeyboardListener)
        }

        contentKeyboardListener.onVisibilityStateChanged(false)
    }

    override fun initViewModel() {
    }

    override fun setupView() {
        (context as OnBoardActivity).onGoneBottomNavigate()
        setupToolBar()

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
        binding.etInputMessage.addTextChangedListener {
            viewModel.input.validateMessage(it.toString())
                .subscribeBy(
                    onError = ::handleOnError
                ).addToDisposables()
        }

        binding.btCast.subscribeOnClick {
            viewModel.createContent().subscribeBy(
                onSuccess = ::handleOnResponse,
                onError = ::handleOnError
            ).addToDisposables()
        }
    }

    private fun handleOnError(case: Throwable) {

    }

    private fun handleOnResponse(createContentUiModel: CreateContentUiModel) {
        if (!createContentUiModel.type.isNullOrBlank()) {
            //is error
        } else {

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
    }

    private fun enableButtonCast(enable: Boolean) {
        with(binding.btCast) {
            isActivated = enable
            isEnabled = enable
        }
    }

    private val contentKeyboardListener = object : KeyboardListener() {
        override fun onVisibilityStateChanged(isShown: Boolean) {
            val delay = if (isShown) HIDE_DELAY_IN_MILLISECONDS else SHOW_DELAY_IN_MILLISECONDS
            binding.clToolBarCast.visibleOrGone(isShown, delay)
        }
    }
}

private const val HIDE_DELAY_IN_MILLISECONDS = 0L
private const val SHOW_DELAY_IN_MILLISECONDS = 200L