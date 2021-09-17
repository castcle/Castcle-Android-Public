package com.castcle.ui.profile.childview.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.castcle.android.databinding.FragmentContentPhotoBinding
import com.castcle.data.staticmodel.ContentType
import com.castcle.ui.base.*
import com.castcle.ui.common.CommonMockAdapter
import com.castcle.ui.profile.ProfileFragmentViewModel
import com.castcle.ui.profile.adapter.VerticalDecoration

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
//  Created by sklim on 16/9/2021 AD at 19:01.

class ContentPhotoFragment : BaseFragment<ProfileFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentContentPhotoBinding> {

    private var adapterCommon: CommonMockAdapter? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContentPhotoBinding
        get() = { inflater, container, attachToRoot ->
            FragmentContentPhotoBinding.inflate(inflater, container, attachToRoot)
        }

    override val binding: FragmentContentPhotoBinding
        get() = viewBinding as FragmentContentPhotoBinding

    override fun viewModel(): ProfileFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ProfileFragmentViewModel::class.java)

    override fun initViewModel() {

    }

    override fun setupView() {
        viewModel.getFeedResponse(ContentType.IMAGE)
        val verticalDecoration = VerticalDecoration(requireContext())
        with(binding.rvContent) {
            adapter = CommonMockAdapter().also {
                adapterCommon = it
            }
        }
    }

    override fun bindViewEvents() {
        viewModel.userProfileContentMock.subscribe {
            adapterCommon?.uiModels = it
        }.addToDisposables()
    }

    override fun bindViewModel() {

    }

}
