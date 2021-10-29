package com.castcle.di.modules.onboard

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.castcle.data.datasource.CommentDataSourceModule
import com.castcle.data.datasource.UserProfileDataSourceModule
import com.castcle.data.repository.*
import com.castcle.di.ViewModelKey
import com.castcle.di.scope.ActivityScope
import com.castcle.localization.LocalizedResources
import com.castcle.localization.LocalizedResourcesImpl
import com.castcle.networking.NetworkModule
import com.castcle.networking.api.auth.AuthenticationDataSourceModule
import com.castcle.networking.api.auth.freshtoken.AuthRefreshTokenDataSourceModule
import com.castcle.networking.api.feed.FeedNonAuthenticationDataSourceModule
import com.castcle.networking.api.nonauthen.NonAuthenticationDataSourceModule
import com.castcle.networking.api.notification.NotificationDataSourceModule
import com.castcle.networking.api.search.SearchDataSourceModule
import com.castcle.networking.api.setting.SettingDataSourceModule
import com.castcle.ui.base.BaseNavigator
import com.castcle.ui.base.BaseNavigatorImpl
import com.castcle.ui.onboard.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.onboard.navigation.OnBoardNavigatorImpl
import com.castcle.usecase.OverrideLocaleApp
import com.castcle.usecase.OverrideLocaleAppImpl
import dagger.Binds
import dagger.Module
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
//  Created by sklim on 18/8/2021 AD at 14:41.

@Module(
    includes = [
        NetworkModule::class,
        NonAuthenticationDataSourceModule::class,
        AuthRefreshTokenDataSourceModule::class,
        AuthenticationDataSourceModule::class,
        FeedNonAuthenticationDataSourceModule::class,
        UserProfileDataSourceModule::class,
        SettingDataSourceModule::class,
        CommentDataSourceModule::class,
        SearchDataSourceModule::class,
        NotificationDataSourceModule::class
    ]
)
abstract class OnBoardActivityModule {

    @Binds
    @ActivityScope
    internal abstract fun onBoardActivity(onBoardActivity: OnBoardActivity): Activity

    @Binds
    @ActivityScope
    internal abstract fun fragmentActivity(onBoardActivity: OnBoardActivity): FragmentActivity

    @Binds
    @ActivityScope
    internal abstract fun onBoardNaivgator(onBoardNavigatorImpl: OnBoardNavigatorImpl): OnBoardNavigator

    @Binds
    @ActivityScope
    internal abstract fun baseNavigateor(baseNavigatorImpl: BaseNavigatorImpl): BaseNavigator

    @Binds
    @ActivityScope
    internal abstract fun localizedResources(localizedResources: LocalizedResourcesImpl): LocalizedResources

    @Binds
    @ActivityScope
    internal abstract fun overrideLocaleApp(
        overrideLocaleAppImpl: OverrideLocaleAppImpl
    ): OverrideLocaleApp

    @Binds
    @IntoMap
    @ActivityScope
    @ViewModelKey(OnBoardViewModel::class)
    internal abstract fun onBoardViewModel(viewModel: OnBoardViewModelImpl): ViewModel

    @Binds
    @ActivityScope
    abstract fun userProfileRepository(
        userRepository: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @ActivityScope
    abstract fun commentRepository(
        commentRepository: CommentRepositoryImpl
    ): CommentRepository
}
