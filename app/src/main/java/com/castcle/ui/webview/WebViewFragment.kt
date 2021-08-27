package com.castcle.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.extensions.gone
import com.castcle.android.R
import com.castcle.android.databinding.FragmentWebviewBinding
import com.castcle.android.databinding.ToolbarCastcleWebviewBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.ui.base.*

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
//  Created by sklim on 24/8/2021 AD at 08:13.

class WebViewFragment : BaseFragment<WebViewFragmentViewModel>(),
    ViewBindingInflater<FragmentWebviewBinding>,
    ToolbarBindingInflater<ToolbarCastcleWebviewBinding> {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWebviewBinding
        get() = { inflater, container, attachToRoot ->
            FragmentWebviewBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentWebviewBinding
        get() = viewBinding as FragmentWebviewBinding

    override val toolbarBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleWebviewBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleWebviewBinding.inflate(inflater, container, attachToRoot)
        }

    override val toolbarBinding: ToolbarCastcleWebviewBinding
        get() = toolbarViewBinding as ToolbarCastcleWebviewBinding

    override fun viewModel(): WebViewFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(WebViewFragmentViewModel::class.java)

    private lateinit var scheme: String
    private var webViewState: Bundle? = null
    private val args by navArgs<WebViewFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initValueScheme()
        initView()
        initViewEvents()

        webViewState?.let { binding.webView.restoreState(it) } ?: run { openUrl() }
    }

    private fun openUrl() {
        args.url?.let {
            clearAppIntent()
            binding.webView.loadUrl(it)
        }
    }

    private fun clearAppIntent() {
        requireActivity().intent.data = null
    }

    private fun initValueScheme() {
        scheme = getString(R.string.link_scheme)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        with(binding) {
            with(webView) {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        request?.let {
                            webView.stopLoading()
                            if (PatternsCompat.WEB_URL.matcher(it.url.toString()).matches()) {
                                webView.loadUrl(it.url.toString())
                            }
                        }
                        return true
                    }
                }
                settings.javaScriptEnabled = true
                CookieManager.getInstance().removeAllCookies(null)
            }
        }
    }

    private fun initViewEvents() {
        with(toolbarBinding) {
            ivToolbarWebViewFragmentBackButton
                .subscribeOnClick {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    }
                }
                .addToDisposables()

            ivToolbarWebViewFragmentForwardButton
                .subscribeOnClick {
                    if (binding.webView.canGoForward()) {
                        binding.webView.goForward()
                    }
                }
                .addToDisposables()

            ivToolbarWebViewFragmentCloseButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }
                .addToDisposables()
            ivToolbarWebViewFragmentWebViewOptionsButton.gone()
        }
    }

    override fun onPause() {
        super.onPause()
        Bundle().apply {
            webViewState = this
            binding.webView.saveState(this)
        }
    }
}
