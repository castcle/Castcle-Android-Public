package com.castcle.ui.webview

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.annotation.DrawableRes
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.castcle.android.R
import com.castcle.android.databinding.FragmentWebviewBinding
import com.castcle.android.databinding.ToolbarCastcleWebviewBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import timber.log.Timber
import java.net.URISyntaxException
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
        ViewModelProvider(this, viewModelFactory)[WebViewFragmentViewModel::class.java]

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private lateinit var scheme: String

    private lateinit var schemeCastcle: String

    private lateinit var host: String

    private var webViewState: Bundle? = null

    private val args by navArgs<WebViewFragmentArgs>()

    private var customWebChromeClient: CustomWebChromeClient? = null

    private var geolocationCallback: GeolocationPermissions.Callback? = null

    private var geolocationRequestOrigin: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initValueScheme()
        initView()
        initViewEvents()

        webViewState?.let { binding.webView.restoreState(it) } ?: run { openUrl() }
    }

    private fun openUrl() {
        val intentData = requireActivity().intent.data.toString()

        val url = if (intentData.contains(PATH_PREFIX_WEBVIEW)) {
            intentData.substringAfter(PATH_PREFIX_WEBVIEW)
        } else {
            args.url
        }

        url?.let {
            val uri = Uri.parse(it)
            toolbarBinding.apply {
                if (isShowOpenInBrowserOption(uri)) {
                    ivToolbarWebViewFragmentWebViewOptionsButton.visible()
                } else {
                    ivToolbarWebViewFragmentWebViewOptionsButton.invisible()
                }
            }

            clearAppIntent()
            binding.webView.loadUrl(uri.toString())
        }
    }

    private fun clearAppIntent() {
        requireActivity().intent.data = null
    }

    private fun initValueScheme() {
        scheme = getString(R.string.link_scheme)
        host = getString(R.string.deep_link_host)
        schemeCastcle = getString(R.string.deep_link_scheme_castcle)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        with(binding) {
            with(webView) {
                activity?.let {
                    customWebChromeClient = CustomWebChromeClient(
                        this@WebViewFragment,
                        doOnProgressChanged = { view, newProgress ->
                            onWebChromeClientProgressChanged(view, newProgress)
                        },
                        doOnError = { error ->
                            handleError(error)
                        }
                    )
                    webChromeClient = customWebChromeClient
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        request?.let {
                            webView.stopLoading()
                            when {
                                it.url.toString().startsWith("intent://") -> {
                                    handleIntentType(it)
                                }
                                PatternsCompat.WEB_URL.matcher(it.url.toString()).matches() -> {
                                    webView.loadUrl(it.url.toString())
                                }
                            }
                        }
                        return true
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                CookieManager.getInstance().removeAllCookies(null)
            }
        }
    }

    private fun handleIntentType(request: WebResourceRequest) {
        try {
            val intent =
                Intent.parseUri(request.url.toString(), Intent.URI_INTENT_SCHEME)
            intent?.let {
                binding.webView.stopLoading()
                intent.data?.let {
                    try {
                        onBoardNavigator.navigateBack()
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        requireActivity().startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Uri.parse(args.url)?.apply {
                            activity?.openUri(uri = this, isOpenExternal = true)
                        }
                    }
                }
            }
        } catch (e: URISyntaxException) {
            Timber.e(e)
        }
    }

    private fun isHttpsDeepLink(uri: Uri?) = uri != null
        && uri.scheme == scheme
        && uri.host == host
        && !uri.path.isNullOrEmpty()

    private fun isCastcleDeepLink(uri: Uri?) = uri != null
        && uri.scheme == schemeCastcle
        && !uri.host.isNullOrEmpty()

    private fun onWebChromeClientProgressChanged(view: WebView, newProgress: Int) {
        ensureViewBindingNotNull(viewBinding) {
            binding.progress = newProgress
        }

        ensureViewBindingNotNull(toolbarViewBinding) {
            with(toolbarBinding) {
                tvToolbarWebViewTitle.text = view.title
                ivToolbarWebViewFragmentBackButton.setImageResource(
                    getImageResource(view.canGoBack())
                )
                ivToolbarWebViewFragmentForwardButton.setImageResource(
                    getImageResource(view.canGoForward())
                )
            }
        }
    }

    private fun handleError(error: Throwable) {
        displayError(error)
    }

    @DrawableRes
    private fun getImageResource(canMove: Boolean): Int =
        if (canMove) {
            R.drawable.ic_arrow_left_big_active
        } else {
            R.drawable.ic_arrow_left_big
        }

    private fun isShowOpenInBrowserOption(uri: Uri): Boolean {
        val openInBrowserOption = uri.getQueryParameter(QUERY_PARAM_OPEN_IN_BROWSER_OPTION)
        return QUERY_PARAM_VALUE_FALSE != openInBrowserOption
    }

    private fun ensureViewBindingNotNull(viewBinding: ViewBinding?, block: () -> Unit) {
        if (viewBinding != null) {
            block()
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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == FILE_PERMISSION_REQUEST_CODE) {
            customWebChromeClient?.doOnActivityResult(
                requestCode,
                resultCode,
                data
            )
        }
    }
}

internal const val PATH_PREFIX_WEBVIEW: String = "://webview/"
private const val LOCATION_PERMISSION_REQUEST_CODE = 7
private const val QUERY_PARAM_OPEN_IN_BROWSER_OPTION = "openInBrowserOption"
private const val QUERY_PARAM_VALUE_FALSE = "false"
