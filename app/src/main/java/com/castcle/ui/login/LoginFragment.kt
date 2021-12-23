package com.castcle.ui.login

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.net.toUri
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

    @SuppressLint("ClickableViewAccessibility")
    override fun setupView() {
        setupToolBar()
    }

    private fun navigatToHomeFeed() {
        activityViewModel.onRegisterSuccess()
        onBoardNavigator.nvaigateToFeedFragment()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.invisible()
            tvToolbarTitle.text = context?.getString(R.string.login_home)
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        with(binding) {
            tvCastcle.subscribeOnClick {
                onBoardNavigator.navigateToGreetingFragment()
            }.addToDisposables()

            with(ieEmail) {
                onTextChanged = {
                    onStatusButton(etPassword.primaryText)
                    viewModel.input.userEmail(ieEmail.primaryText)
                }
                onEditorActionNext = {
                    etPassword.setRequestFocus()
                }
            }

            with(etPassword) {
                setIconWithTransformation()
                onTextChanged = {
                    onStatusButton(it)
                    viewModel.input.password(etPassword.primaryText)
                }

                onEditorActionListener = { actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        requireActivity().hideSoftKeyboard()
                        btLogin.callOnClick()
                        true
                    } else {
                        false
                    }
                }
            }

            btLogin.subscribeOnClick {
                viewModel.getAuthLoginWithEmail().subscribeBy(
                    onError = ::handlerError
                ).addToDisposables()
            }

            tvForgetPass.subscribeOnClick {
                handlerNavigateToForgotPassword()
            }.addToDisposables()
        }
    }

    private fun onStatusButton(password: String) {
        binding.btLogin.isActivated =
            password.isNotBlank() && binding.ieEmail.primaryText.isNotBlank()
    }

    private fun handlerNavigateToForgotPassword() {
        onBoardNavigator.navigateToForgotPassword()
    }

    private fun handlerError(error: Throwable) {
        if (error is LoginError && error.hasAuthenticationAccountNotFound()) {
            binding.etPassword.setError(
                error = error.userReadableMessage(requireContext()),
                isShowErrorWithBackground = true
            )
        }
        if (error is LoginError && error.hasAuthenticationNotFound()) {
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
