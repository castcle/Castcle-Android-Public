package com.castcle.ui.common.dialog.chooseimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.R
import com.castcle.android.databinding.DialogFragmentChooseBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.extensions.getColorResource
import com.castcle.extensions.setNavigationResult
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.signin.aboutyou.addlink.LINK_REQUEST_CODE
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

class ChooseDialogFragment : BaseBottomSheetDialogFragment<ChooseDialogViewModel>(),
    BaseFragmentCallbacks, ViewBindingInflater<DialogFragmentChooseBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> DialogFragmentChooseBinding
        get() = { inflater, container, attachToRoot ->
            DialogFragmentChooseBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: DialogFragmentChooseBinding
        get() = viewBinding as DialogFragmentChooseBinding

    override val layoutRes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetRecastDialogTheme)
    }

    override fun viewModel(): ChooseDialogViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ChooseDialogViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        initBottomSheetDialog()
    }

    private fun initBottomSheetDialog() {
        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!
        view.setBackgroundColor(requireContext().getColorResource(R.color.transparent))
        with(BottomSheetBehavior.from(view)) {
            isFitToContents = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun bindViewEvents() {
        with(binding) {
            tvTakePhoto.subscribeOnClick {
                handleNavigateResultBack(PhotoSelectedState.SELECT_TAKE_CAMERA)
            }.addToDisposables()

            tvChooseRoll.subscribeOnClick {
                handleNavigateResultBack(PhotoSelectedState.SELECT_CHOOSE)
            }.addToDisposables()
        }
    }

    private fun handleNavigateResultBack(state: PhotoSelectedState) {
        setNavigationResult(onBoardNavigator, KEY_CHOOSE_REQUEST, state)
        onBoardNavigator.findNavController().popBackStack()
    }

    override fun bindViewModel() {
    }
}

const val KEY_CHOOSE_REQUEST: String = "CHOOSE-001"
