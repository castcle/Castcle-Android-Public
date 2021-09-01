package com.castcle.di.modules.onboard

import com.castcle.di.modules.common.dialog.NotiflyLoginDialogFragmentModule
import com.castcle.di.modules.feed.FeedFragmentViewModelModule
import com.castcle.di.modules.login.LoginFragmentViewModelModule
import com.castcle.di.modules.webview.WebViewFragmentViewModelModule
import com.castcle.di.scope.FragmentScope
import com.castcle.ui.common.dialog.NotiflyLoginDialogFragment
import com.castcle.ui.feed.FeedFragment
import com.castcle.ui.login.LoginFragment
import com.castcle.ui.webview.WebViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

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
//  Created by sklim on 18/8/2021 AD at 14:49.

@Module
interface OnBoardFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [FeedFragmentViewModelModule::class])
    fun feedFragment(): FeedFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [NotiflyLoginDialogFragmentModule::class])
    fun notiflyLoginDialogFragment(): NotiflyLoginDialogFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [WebViewFragmentViewModelModule::class])
    fun webViewFragment(): WebViewFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [LoginFragmentViewModelModule::class])
    fun loginFragment(): LoginFragment
}
