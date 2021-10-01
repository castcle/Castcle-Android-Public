package com.castcle.ui.setting.applanguage

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentAppLanguageBinding
import com.castcle.android.databinding.ToolbarCastcleLanguageBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.setting.LanguageUiModel
import com.castcle.extensions.gone
import com.castcle.ui.base.*
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.setting.language.LanguageFragmentViewModel
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

class AppLanguageFragment : BaseFragment<LanguageFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentAppLanguageBinding>,
    ToolbarBindingInflater<ToolbarCastcleLanguageBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    private val appLanguageFragmentArgs: AppLanguageFragmentArgs by navArgs()

    private lateinit var appLanguageAdapter: AppLanguageAdapter

    private val isAppLanguage: Boolean
        get() = appLanguageFragmentArgs.isAppLanguage

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleLanguageBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleLanguageBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleLanguageBinding
        get() = toolbarViewBinding as ToolbarCastcleLanguageBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentAppLanguageBinding
        get() = { inflater, container, attachToRoot ->
            FragmentAppLanguageBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentAppLanguageBinding
        get() = viewBinding as FragmentAppLanguageBinding

    override fun viewModel(): LanguageFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(LanguageFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
    }

    override fun setupView() {
        setupToolBar()
        binding.rvLanguageList.run {
            adapter = AppLanguageAdapter().also {
                appLanguageAdapter = it
            }
        }
        validateIsAppLanguage(
            appLanguage = {
                activityViewModel.currentAppLanguage.value?.let {
                    appLanguageAdapter.items = it
                }
            },
            preferred = {
                activityViewModel.preferredLanguage.observe(this, { it ->
                    handleItemSelected(it)
                })
            }
        )
    }

    private fun handleItemSelected(preferredData: List<LanguageUiModel>) {
        appLanguageAdapter.items = preferredData
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            ivToolbarProfileButton.gone()
            tvToolbarTitle.text = requireContext().getString(R.string.setting_display_language)
            ivToolbarLogoButton.subscribeOnClick {
                findNavController().navigateUp()
            }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        appLanguageAdapter.itemClick.subscribe {
            handleLanguageSelected(it)
        }.addToDisposables()

        binding.etTextInputPrimary.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(text: Editable?) {
                activityViewModel.filterSearchLanguage(text.toString())
            }
        })
    }

    private fun handleLanguageSelected(item: LanguageUiModel) {
        validateIsAppLanguage(
            appLanguage = {
                activityViewModel.setAppLanguage(item.code, true)
            },
            preferred = {
                activityViewModel.setPerferredLanguage(item.code)
            }
        )
    }

    private fun validateIsAppLanguage(appLanguage: () -> Unit, preferred: () -> Unit) {
        if (isAppLanguage) {
            appLanguage.invoke()
        } else {
            preferred.invoke()
        }
    }

    override fun bindViewModel() {
        activityViewModel.onSetLanguageSuccess.subscribe {
            updateAppLanguage()
        }.addToDisposables()
    }

    private fun updateAppLanguage() {
        activity?.let {
            it.finish()
            OnBoardActivity.start(it)
        }
    }
}
