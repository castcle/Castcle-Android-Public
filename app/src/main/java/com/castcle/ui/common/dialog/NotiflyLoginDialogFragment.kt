package com.castcle.ui.common.dialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.R
import com.castcle.android.databinding.DialogFragmentNotiflyLoginBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.*
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel
import com.castcle.common_model.model.signin.domain.*
import com.castcle.extensions.openUri
import com.castcle.networking.api.response.SocialTokenResponse
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject


//  Copyright (c) 2021, Castcle and/or its affiliates. All rights reserved.
//  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//
//  This code is free software; you can redistribute it and/or modify it
//  under the terms of the GNU General Public License version 3 only, as
//  published by the Free Software Foundation.
//
//  This code is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
//  version 3 for more details (a copy is included in the LICENSE file that
//  accompanied this code).
//
//  You should have received a copy of the GNU General Public License version
//  3 along with this work; if not, write to the Free Software Foundation,
//  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
//
//  Please contact Castcle, 22 Phet Kasem 47/2 Alley, Bang Khae, Bangkok,
//  Thailand 10160, or visit www.castcle.com if you need additional information
//  or have any questions.
//
//
//  Created by sklim on 23/8/2021 AD at 12:45.

class NotiflyLoginDialogFragment : BaseBottomSheetDialogFragment<NotiflyLoginDialogViewModel>(),
    BaseFragmentCallbacks, ViewBindingInflater<DialogFragmentNotiflyLoginBinding> {

    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>

    private lateinit var googleSignInAccount: GoogleSignInAccount

    private var providerSocial = ""

    private var email = ""

    private var authToken = ""

    private var userName = ""

    private var avatarImage = ""

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> DialogFragmentNotiflyLoginBinding
        get() = { inflater, container, attachToRoot ->
            DialogFragmentNotiflyLoginBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: DialogFragmentNotiflyLoginBinding
        get() = viewBinding as DialogFragmentNotiflyLoginBinding

    override val layoutRes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun viewModel(): NotiflyLoginDialogViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(NotiflyLoginDialogViewModel::class.java)

    override fun bindViewModel() = Unit

    override fun initViewModel() = Unit

    override fun setupView() {
        initBottomSheetDialog()
        startActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    googleSignInAccount = task.getResult(ApiException::class.java)
                    loginWithSocial()
                    Timber.d("firebaseAuthWithGoogle:" + googleSignInAccount.id)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Timber.tag("GOOGLE").w(e, "Google sign in failed")
                }
            }
        }
    }

    private fun loginWithSocial() {
        val requestLoginSocial = getRequestSocialLogin()
        viewModel.authRegisterWithSocial(requestLoginSocial).subscribeBy(
            onSuccess = {
                handlerLoginResponse(it)
            }, onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun handlerLoginResponse(it: SocialTokenResponse) {
        checkHasEmail(email)
        //When has email or user in system that be login and should Navigate to Feed and
    }

    private fun getRequestSocialLogin(): RegisterWithSocialRequest {
        var requestSocial = RegisterWithSocialRequest()
        handleSocialState(
            onGoogle = {
                with(googleSignInAccount) {
                    userName = displayName ?: ""
                    this@NotiflyLoginDialogFragment.email = email ?: ""
                    authToken = idToken ?: ""
                    avatarImage = photoUrl?.toString() ?: ""

                    requestSocial = RegisterWithSocialRequest(
                        provider = providerSocial,
                        payload = RegisterWithSocialPayLoad(
                            authToken = idToken ?: "",
                            socialUser = SocialUser(
                                id = this.id ?: "",
                                first_name = account?.name ?: "",
                                username = displayName ?: "",
                                email = email ?: "",
                                photo_url = photoUrl?.toString() ?: ""
                            )
                        )
                    )
                }
            },
            onFaceBook = {
                requestSocial = RegisterWithSocialRequest(
                    provider = providerSocial,
                    payload = RegisterWithSocialPayLoad(
                        authToken = googleSignInAccount.idToken ?: ""
                    )
                )
            },
            onTwitter = {
                requestSocial = RegisterWithSocialRequest()
            }
        )
        return requestSocial
    }

    private fun handleSocialState(
        onGoogle: () -> Unit,
        onFaceBook: () -> Unit,
        onTwitter: () -> Unit
    ) {
        when (providerSocial) {
            PROVIDER_GOOGLE -> {
                onGoogle.invoke()
            }
            PROVIDER_TWITTER -> {
                onTwitter.invoke()
            }
            PROVIDER_FACEBOOK -> {
                onFaceBook.invoke()
            }
            else -> RegisterWithSocialRequest()
        }
    }

    private fun checkHasEmail(email: String) {
        viewModel.checkHasEmail(email = email ?: "").subscribeBy(
            onSuccess = {
                onHasEmail(it)
            }, onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun onHasEmail(
        emailUiVerify: AuthVerifyBaseUiModel.EmailVerifyUiModel,
    ) {
        if (emailUiVerify.exist) {
            //Merge Account
        } else {
            onNavigateToCreateDisplayName()
        }
    }

    private fun onNavigateToCreateDisplayName() {
        val registerBundle = RegisterBundle.RegisterWithSocial(
            provider = PROVIDER_GOOGLE,
            userName = userName,
            email = email,
            authToken = authToken,
            userAvatar = avatarImage
        )
        onBoardNavigator.navigateToCreateAccountFragment(registerBundle)
    }

    private fun initBottomSheetDialog() {
        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!
        with(BottomSheetBehavior.from(view)) {
            isFitToContents = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun bindViewEvents() {
        with(binding) {
            tvHaveAccount.subscribeOnClick {
                navigateToLoginFragment()
            }.addToDisposables()
            tvActionLinkUserAgreement.subscribeOnClick {
                openWebView(STATIC_LINK_USER_AGREEMENT)
            }.addToDisposables()
            tvActionLinkPrivacy.subscribeOnClick {
                openWebView(STATIC_LINK_PRIVACY_POLICY)
            }.addToDisposables()
            tvjoinUs.subscribeOnClick {
                openWebView(STATIC_LINK_JOIN_US)
            }.addToDisposables()
            tvManifesto.subscribeOnClick {
                openWebView(STATIC_LINK_MENIFESTO)
            }.addToDisposables()
            tvWhitepaper.subscribeOnClick {
                openWebView(STATIC_OPEN_PDF + STATIC_LINK_WHITEPAPER)
            }.addToDisposables()
            with(layoutLogin) {
                clLoginWithEmail.subscribeOnClick {
                    navigateToRegisterFragment()
                }.addToDisposables()

                clLoginWithGoogle.subscribeOnClick {
                    providerSocial = PROVIDER_GOOGLE
                    loginWithGoogle()
                }.addToDisposables()

                clLoginWithTwitter.subscribeOnClick {
                    providerSocial = PROVIDER_TWITTER
                    navigateToTwitterLoginFragment()
                }.addToDisposables()

                clLoginWithFackbook.subscribeOnClick {
                    providerSocial = PROVIDER_FACEBOOK
                }.addToDisposables()
            }
        }
    }

    private fun loginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.gsc_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult.launch(signInIntent)
    }

    private fun navigateToTwitterLoginFragment() {
        onBoardNavigator.navigateToTwitterLoginFragment()
    }

    private fun navigateToRegisterFragment() {
        onBoardNavigator.navigateToGreetingFragment()
    }

    private fun navigateToLoginFragment() {
        onBoardNavigator.navigateToLoginFragment()
    }

    private fun openWebView(url: String) {
        (context as Activity).openUri(url)
    }
}

const val STATIC_LINK_USER_AGREEMENT =
    "https://documents.castcle.com/terms-of-service.html"
const val STATIC_LINK_PRIVACY_POLICY =
    "https://documents.castcle.com/privacy-policy.html"
const val STATIC_LINK_JOIN_US =
    "https://jobs.blognone.com/company/castcle"
private const val STATIC_LINK_DOCS =
    "https://docs.castcle.com/"
const val STATIC_LINK_ABOUT_US =
    "https://documents.castcle.com/about-us.html"
const val STATIC_LINK_MENIFESTO =
    "https://docs.castcle.com/"
const val STATIC_LINK_WHITEPAPER =
    "https://castcle.com/whitepaper.pdf"
const val STATIC_OPEN_PDF =
    "https://drive.google.com/viewerng/viewer?embedded=true&url="
