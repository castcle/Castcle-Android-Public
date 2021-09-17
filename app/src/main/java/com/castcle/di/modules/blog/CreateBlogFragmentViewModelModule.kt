package com.castcle.di.modules.blog

import androidx.lifecycle.ViewModel
import com.castcle.di.ViewModelKey
import com.castcle.di.modules.login.PermissionOnProfileChooseFragment
import com.castcle.di.scope.FragmentScope
import com.castcle.ui.createbloc.*
import com.castcle.ui.signin.profilechooseimage.ProfileChooseFragment
import com.permissionx.guolindev.PermissionMediator
import com.permissionx.guolindev.PermissionX
import dagger.*
import dagger.multibindings.IntoMap

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
//  Created by sklim on 13/9/2021 AD at 10:14.
@Module(includes = [PermissionOnCreateBlogFragment::class])
interface CreateBlogFragmentViewModelModule {

    @FragmentScope
    @Binds
    @IntoMap
    @ViewModelKey(CreateBlogFragmentViewModel::class)
    fun createBlogViewModel(viewModel: CreateBlogFragmentViewModelImpl): ViewModel
}

@Module
class PermissionOnCreateBlogFragment {

    @Provides
    fun permissionX(fragment: CreateBlogFragment): PermissionMediator =
        PermissionX.init(fragment)
}

