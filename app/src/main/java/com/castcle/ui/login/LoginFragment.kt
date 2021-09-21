package com.castcle.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentLoginBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.data.error.LoginError
import com.castcle.data.error.userReadableMessage
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class LoginFragment : BaseFragment<LoginFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentLoginBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {


    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    override val toolbarBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }

    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = { inflater, container, attachToRoot ->
            FragmentLoginBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentLoginBinding
        get() = viewBinding as FragmentLoginBinding

    override fun viewModel(): LoginFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(LoginFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun navigatToHomeFeed() {
        onBoardNavigator.nvaigateToFeedFragment()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.gone()
            tvToolbarTitle.text = context?.getString(R.string.login_home)
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        with(binding) {
            tvCastcleSignUp.subscribeOnClick {
                onBoardNavigator.navigateToGreetingFragment()
            }

            ieEmail.onTextChanged = {
                viewModel.input.userEmail(ieEmail.primaryText)
            }

            etPassword.onTextChanged = {
                viewModel.input.password(etPassword.primaryText)
            }

            btLogin.subscribeOnClick {
                viewModel.getAuthLoginWithEmail().subscribeBy(
                    onComplete = {
                        navigatToHomeFeed()
                    }, onError = ::handlerError
                ).addToDisposables()
            }
        }
    }

    private fun handlerError(error: Throwable) {
        if (error is LoginError && error.hasAuthenticationAccountNotFound()) {
            binding.etPassword.setError(
                error = error.userReadableMessage(requireContext()),
                isShowErrorWithBackground = true
            )
        }
        if (error is LoginError && error.hasAuthenticationLoginInvaildChannel()) {
            binding.etPassword.setError(
                error = error.userReadableMessage(requireContext()),
                isShowErrorWithBackground = true
            )
        }
    }

    override fun bindViewModel() {
        viewModel.enableLogin.observe(this, {
            binding.btLogin.isEnabled = it
        })

        viewModel.userResponse.observe(this, {
            navigatToHomeFeed()
            activityViewModel.onRefreshProfile()
        })

        viewModel.showLoading.subscribe {
            handleOnLoading(it)
        }.addToDisposables()
    }

    private fun handleOnLoading(show: Boolean) {
        binding.btLogin.visibleOrInvisible(!show)
        binding.progressBar.visibleOrGone(show)
    }
}
