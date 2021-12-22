package com.castcle.ui.login.twitterlogin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.R
import com.castcle.android.databinding.FragmentLoginTwitterBinding
import com.castcle.extensions.displayErrorMessage
import com.castcle.ui.base.*
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import java.net.URLDecoder


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
//  Created by sklim on 19/11/2021 AD at 09:51.

class TwitterLoginFragment : BaseFragment<TwitterLoginFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentLoginTwitterBinding> {

    lateinit var oauthToken: String

    private lateinit var authClient: TwitterAuthClient

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginTwitterBinding
        get() = { inflater, container, attachToRoot ->
            FragmentLoginTwitterBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentLoginTwitterBinding
        get() = viewBinding as FragmentLoginTwitterBinding

    override fun viewModel(): TwitterLoginFragmentViewModel =
        ViewModelProvider(
            this, viewModelFactory
        ).get(TwitterLoginFragmentViewModel::class.java)

    override fun initViewModel() {
    }

    override fun setupView() {

        val twitterAppId = getString(R.string.twitter_app_id)
        val twitterSecret = getString(R.string.twitter_app_secret)
        val authConfig = TwitterAuthConfig(twitterAppId, twitterSecret)
        val twitterConfig =
            TwitterConfig.Builder(requireContext()).twitterAuthConfig(authConfig).build()
        Twitter.initialize(twitterConfig)
        authClient = TwitterAuthClient()

        authClient.authorize(requireActivity(), object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                result?.data?.authToken
            }

            override fun failure(exception: TwitterException?) {
                displayErrorMessage(exception?.message ?: "")
            }
        })
    }

    override fun bindViewEvents() {

    }


    override fun bindViewModel() {

    }

    @SuppressLint("NewApi")
    private fun onLoadWebView(oauthToken: String?) {
        val url = "https://api.twitter.com/oauth/authorize?oauth_token=$oauthToken&force_login=true"
        binding.webView.webViewClient = webCreateViewClient
        binding.webView.loadUrl(url)
    }

    private val webCreateViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request!!.url.toString().startsWith("")) {
                val decodedUrl = URLDecoder.decode(request.url.toString(), "UTF-8")
                if (decodedUrl.contains("oauth_token=")) {
                    val uri = Uri.parse(decodedUrl)
                    val authToken = uri.getQueryParameter("oauth_token")
                    val authVerifier = uri.getQueryParameter("oauth_verifier")

                    //Check to make sure oauth tokens match
                    if (oauthToken == authToken) {
                        val data = Intent()
                        data.putExtra("oauth_token", authToken)
                        data.putExtra("oauth_verifier", authVerifier)
                    } else {
                        //Tokens don't match, do not proceed
                    }
                }
                return true
            }
            return false
        }
    }
}
