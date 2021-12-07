package com.castcle.ui.setting.language

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.castcle.android.R
import com.castcle.android.databinding.FragmentLanguageBinding
import com.castcle.android.databinding.ToolbarCastcleLanguageBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.setting.LanguageUiModel
import com.castcle.extensions.invisible
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardViewModel
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

class LanguageAppFragment : BaseFragment<LanguageFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentLanguageBinding>,
    ToolbarBindingInflater<ToolbarCastcleLanguageBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private lateinit var languageAdapter: LanguageAdapter

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleLanguageBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleLanguageBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleLanguageBinding
        get() = toolbarViewBinding as ToolbarCastcleLanguageBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentLanguageBinding
        get() = { inflater, container, attachToRoot ->
            FragmentLanguageBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentLanguageBinding
        get() = viewBinding as FragmentLanguageBinding

    override fun viewModel(): LanguageFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(LanguageFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        viewModel.getMeteAppLanguage()

        activityViewModel.getCurrentAppLanguage()
            .subscribe()
            .addToDisposables()

        activityViewModel.getCachePreferredLanguage()
            .subscribe()
            .addToDisposables()
    }

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.invisible()
            ivToolbarProfileButton.subscribeOnClick {
                navigateToAppLanguageFragment()
            }.addToDisposables()
            tvToolbarTitle.text = requireContext().getString(R.string.setting_language_title)
            ivToolbarLogoButton.subscribeOnClick {
                findNavController().navigateUp()
            }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        binding.tvAppLanguage.subscribeOnClick {
            navigateToAppLanguageFragment(true)
        }.addToDisposables()

        binding.tvActionTitle.subscribeOnClick {
            navigateToAppLanguageFragment()
        }.addToDisposables()

        binding.rvLanguage.run {
            adapter = LanguageAdapter().also {
                languageAdapter = it
            }
        }

        languageAdapter.itemClick.subscribe {
            activityViewModel.setPerferredLanguage(it.code,true)
        }.addToDisposables()
    }

    private fun navigateToAppLanguageFragment(isAppLanguage: Boolean = false) {
        onBoardNavigator.navigateToAppLanguageFragment(isAppLanguage)
    }

    @SuppressLint("CheckResult")
    override fun bindViewModel() {
        with(viewModel) {
            preferredLanguage.observe(this@LanguageAppFragment, {
                activityViewModel.setPreferredLanguageData(it)
            })
        }

        activityViewModel.currentAppLanguage.observe(this@LanguageAppFragment, {
            currentAppLanguage(it)
        })

        activityViewModel.preferredLanguageSelected.observe(this, { languageSelected ->
            languageAdapter.items = languageSelected
        })

        if (!activityViewModel.preferredLanguage.value.isNullOrEmpty()) {
            activityViewModel.preferredLanguage.value?.let {
                viewModel.getMapPreferredLanguage(it)
                    .subscribe()
                    .addToDisposables()
            }
        }
    }

    private fun currentAppLanguage(language: List<LanguageUiModel>) {
        language.find {
            it.isSelected
        }?.let {
            binding.tvAppLanguage.text = it.display
        }
    }
}
