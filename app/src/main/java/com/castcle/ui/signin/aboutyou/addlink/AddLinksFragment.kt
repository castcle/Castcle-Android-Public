package com.castcle.ui.signin.aboutyou.addlink

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentAddLinksBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.userprofile.LinksRequestUiModel
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
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
//  Created by sklim on 1/9/2021 AD at 18:12.

class AddLinksFragment : BaseFragment<AddLinksFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentAddLinksBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private val addLinksFragmentArgs: AddLinksFragmentArgs by navArgs()

    private val linksUiModel: LinksRequestUiModel
        get() = addLinksFragmentArgs.linkUiModel

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddLinksBinding
        get() = { inflater, container, attachToRoot ->
            FragmentAddLinksBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentAddLinksBinding
        get() = viewBinding as FragmentAddLinksBinding

    override fun viewModel(): AddLinksFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(AddLinksFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
        onBindLinksItem(linksUiModel)
    }

    private fun onBindLinksItem(linksRequest: LinksRequestUiModel) {
        linksRequest.facebook.isNotEmpty().run {
            if (this) {
                binding.itLinkFacebook.primaryText = linksRequest.facebook
            }
        }
        linksRequest.twitter.isNotEmpty().run {
            if (this) {
                binding.itLinkTwitter.primaryText = linksRequest.twitter
            }
        }
        linksRequest.youtube.isNotEmpty().run {
            if (this) {
                binding.itLinkYouTube.primaryText = linksRequest.youtube
            }
        }
        linksRequest.medium.isNotEmpty().run {
            if (this) {
                binding.itLinkMedium.primaryText = linksRequest.medium
            }
        }
        linksRequest.website.isNotEmpty().run {
            if (this) {
                binding.itLinkWebSite.primaryText = linksRequest.website
            }
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.apply {
                visible()
                text = context.getString(R.string.tool_bar_apply)
            }.run {
                subscribeOnClick {
                    onApplyLinks()
                }
            }
            tvToolbarTitle.text = context?.getString(R.string.add_link_title_tool_bar)
            tvToolbarTitle.setTextColor(requireContext().getColorResource(R.color.white))
            ivToolbarLogoButton.subscribeOnClick {
                findNavController().navigateUp()
            }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
    }

    private fun onApplyLinks() {
        with(binding) {
            val links = LinksRequestUiModel(
                facebook = itLinkFacebook.primaryText,
                twitter = itLinkTwitter.primaryText,
                youtube = itLinkYouTube.primaryText,
                website = itLinkWebSite.primaryText,
                medium = itLinkMedium.primaryText,
            )
            handleNavigateResultBack(links)
        }
    }

    private fun handleNavigateResultBack(links: LinksRequestUiModel) {
        setNavigationResult(onBoardNavigator, LINK_REQUEST_CODE, links)
        onBoardNavigator.findNavController().popBackStack()
    }

    override fun bindViewModel() = Unit
}

const val LINK_REQUEST_CODE: String = "LINKS-001"
