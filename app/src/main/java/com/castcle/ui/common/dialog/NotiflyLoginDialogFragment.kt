package com.castcle.ui.common.dialog

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.R
import com.castcle.android.databinding.DialogFragmentNotiflyLoginBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.extensions.openUri
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

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
            tvHaveAccountLogin.subscribeOnClick {
                navigateToLoginFragment()
            }
            tvActionLinkUserAgreement.subscribeOnClick {
                openWebView(STATIC_LINK_USER_AGREEMENT)
            }
            tvActionLinkPrivacy.subscribeOnClick {
                openWebView(STATIC_LINK_PRIVACY_POLICY)
            }
            tvjoinUs.subscribeOnClick {
                openWebView(STATIC_LINK_JOIN_US)
            }
            tvManifesto.subscribeOnClick {
                openWebView(STATIC_LINK_MENIFESTO)
            }
            tvWhitepaper.subscribeOnClick {
                openWebView(STATIC_LINK_WHITEPAPER)
            }
            with(layoutLogin) {
                clLoginWithEmail.subscribeOnClick {
                    navigateToLoginFragment()
                }
            }
        }
    }

    private fun navigateToLoginFragment() {
        onBoardNavigator.navigateToLoginFragment()
    }

    private fun openWebView(url: String) {
        (context as Activity).openUri(url)
    }
}

private const val STATIC_LINK_USER_AGREEMENT =
    "https://documents.castcle.com/terms-of-service.html"
private const val STATIC_LINK_PRIVACY_POLICY =
    "https://documents.castcle.com/privacy-policy.html"
private const val STATIC_LINK_JOIN_US =
    "https://jobs.blognone.com/company/castcle"
private const val STATIC_LINK_DOCS =
    "https://docs.castcle.com/"
private const val STATIC_LINK_ABOUT_US =
    "https://documents.castcle.com/about-us.html"
private const val STATIC_LINK_MENIFESTO =
    "https://castcle.gitbook.io/document/"
private const val STATIC_LINK_WHITEPAPER =
    "https://documents.castcle.com/castcle-whitepaper-v1_3.pdf"
