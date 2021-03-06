package com.castcle.ui.common.dialog.recast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.DialogFragmentRecastBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.ContentFeedUiModel
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.setting.PageUiModel
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.rxkotlin.subscribeBy
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

class RecastDialogFragment : BaseBottomSheetDialogFragment<RecastDialogViewModel>(),
    BaseFragmentCallbacks, ViewBindingInflater<DialogFragmentRecastBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private lateinit var adapterRecast: RecastAdapter

    private val dialogArgs: RecastDialogFragmentArgs by navArgs()

    private val currentCoutent: ContentFeedUiModel
        get() = dialogArgs.contentUiModel

    private var unReCasted: Boolean = false

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> DialogFragmentRecastBinding
        get() = { inflater, container, attachToRoot ->
            DialogFragmentRecastBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: DialogFragmentRecastBinding
        get() = viewBinding as DialogFragmentRecastBinding

    override val layoutRes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetRecastDialogTheme)
    }

    override fun viewModel(): RecastDialogViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(RecastDialogViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        viewModel.fetchUserProfile()
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
            rvUserPage.run {
                adapter = RecastAdapter().also {
                    adapterRecast = it
                }
            }
        }
    }

    override fun bindViewModel() {
        viewModel.userPageUiModel.observe(viewLifecycleOwner, { it ->
            adapterRecast.uiModels = it?.pageUiItem ?: emptyList()
            it?.pageUiItem?.find {
                it.selected
            }?.let {
                onBindRecastAction(it)
            }
        })

        adapterRecast.itemClick.subscribeBy {
            handleClick(it)
        }.addToDisposables()

        viewModel.onSuccess.subscribe {
            displayMessage(
                localizedResources.getString(
                    R.string.recast_title_recasted
                )
            )
            if (unReCasted) {
                handleNavigateResultUnReCastBack()
            } else {
                handleNavigateResultBack()
            }
        }.addToDisposables()

        viewModel.onError.subscribe {
            displayError(it)
        }.addToDisposables()
    }

    private fun handleClick(user: PageUiModel) {
        viewModel.onPageSelected(user)
        onBindRecastAction(user)
        handleBindRecast()
    }

    private fun onBindRecastAction(user: PageUiModel) {
        with(binding) {
            tvDisplayName.text = user.displayName
            ivAvatar.loadRoundedCornersImage(user.avatarUrl)
            clSelectedRecast.subscribeOnClick {
                handleBindSelectPage()
            }
            if (currentCoutent.recasted) {
                unReCasted = true
                icRecastPost.tvDisplayTitle.text =
                    requireContext().getString(R.string.recast_title_unrecast)
            }
            icRecastPost.clRecastPost.subscribeOnClick {
                if (unReCasted) {
                    handleNavigateResultUnReCastBack()
                } else {
                    handleNavigateResultBack()
                }
            }
            icQuotecastPost.clQuotecastPost.subscribeOnClick {
                navigateToCreateQuoteFragment()
            }
        }
    }

    private fun navigateToCreateQuoteFragment() {
        var profileBundle: ProfileBundle = ProfileBundle.ReCastPage()
        viewModel.userPageUiModel.value?.pageUiItem?.find {
            it.selected
        }?.let {
            profileBundle = ProfileBundle.ReCastPage(
                castcleId = it.castcleId,
                displayName = it.displayName,
                avaterUrl = it.avatarUrl
            )
        }
        onBoardNavigator.navigateToCreateQuoteFragment(
            currentCoutent, profileBundle
        )
    }

    private fun handleNavigateResultBack() {
        setNavigationResult(onBoardNavigator, KEY_REQUEST, currentCoutent)
        onBoardNavigator.findNavController().popBackStack()
    }

    private fun handleNavigateResultUnReCastBack() {
        setNavigationResult(onBoardNavigator, KEY_REQUEST_UNRECAST, currentCoutent)
        onBoardNavigator.findNavController().popBackStack()
    }

    private fun handleBindSelectPage() {
        binding.flRoot.visible()
        binding.flRecastRoot.gone()
    }

    private fun handleBindRecast() {
        binding.flRoot.gone()
        binding.flRecastRoot.visible()
    }
}

const val CODE_REQUEST: Int = 10
const val KEY_REQUEST: String = "REC-001"
const val KEY_REQUEST_UNRECAST: String = "URC-001"
const val KEY_REQUEST_QUOTE_CAST: String = "QC-001"
const val KEY_REQUEST_COMMENTED_COUNT: String = "COM-001"
