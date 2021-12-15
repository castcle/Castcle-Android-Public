package com.castcle.ui.search.trend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.castcle.android.databinding.*
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.data.staticmodel.TabContentStatic
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.android.material.tabs.TabLayoutMediator
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
//  Created by sklim on 7/10/2021 AD at 18:38.

class TrendFragment : BaseFragment<TrendFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentTrendBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var contentTrendAdapter: ContentTrendAdapter

    private val trendFragmentArgs: TrendFragmentArgs by navArgs()

    private val trendSlug: String
        get() = trendFragmentArgs.trendSlug

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentTrendBinding
        get() = { inflater, container, attachToRoot ->
            FragmentTrendBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentTrendBinding
        get() = viewBinding as FragmentTrendBinding

    override fun viewModel(): TrendFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(TrendFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        activityViewModel.setTrendSlugData(trendSlug)
    }

    override fun setupView() {
        with(binding.vpPageContent) {
            adapter = ContentTrendAdapter(requireParentFragment()).also {
                contentTrendAdapter = it
            }
            offscreenPageLimit = 2
        }

        TabLayoutMediator(
            binding.tabs,
            binding.vpPageContent
        ) { Tab, position ->
            Tab.text = requireContext().getString(TabContentStatic.tabTrend[position].tabNameRes)
        }.attach()

        with(binding.tbTrendSearch) {
            tvToolbarTitle.text = trendSlug
            ivToolbarLogoButton.subscribeOnClick {
                onBoardNavigator.findNavController().navigateUp()
            }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
    }

    override fun bindViewModel() {
    }
}