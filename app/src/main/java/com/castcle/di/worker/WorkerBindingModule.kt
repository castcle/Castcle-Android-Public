package com.castcle.di.worker

import com.castcle.data.datasource.CommentDataSourceModule
import com.castcle.data.datasource.UserProfileDataSourceModule
import com.castcle.data.repository.UserWorkerRepository
import com.castcle.data.repository.UserWorkerRepositoryImpl
import com.castcle.di.modules.common.SessionEnvironmentModule
import com.castcle.di.scope.ActivityScope
import com.castcle.networking.NetworkModule
import com.castcle.networking.api.auth.AuthenticationDataSourceModule
import com.castcle.networking.api.auth.freshtoken.AuthRefreshTokenDataSourceModule
import com.castcle.networking.api.nonauthen.NonAuthenticationDataSourceModule
import com.castcle.session_memory.SessionManagerModule
import com.castcle.usecase.worker.UpLoadProfileAvatarWorker
import com.castcle.usecase.worker.factory.*
import com.co.the1.the1app.di.annotation.WorkerKey
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
//  Created by sklim on 20/10/2021 AD at 13:15.

@Module(
    includes = [
        NetworkModule::class,
        NonAuthenticationDataSourceModule::class,
        AuthRefreshTokenDataSourceModule::class,
        AuthenticationDataSourceModule::class,
        UserProfileDataSourceModule::class,
        CommentDataSourceModule::class,
        SessionEnvironmentModule::class,
        SessionManagerModule::class
    ]
)
interface WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(UpLoadProfileAvatarWorker::class)
    fun bindingUpLoadProfileAvatarWorker(
        factory: UpLoadProfileAvatarWorker.Factory
    ): ChildWorkerFactory

    @Binds
    fun bindImageUploader(
        imageUploader: ImageUploaderWorkHelperImpl
    ): ImageUploaderWorkHelper

    @Binds
    fun userWorkerRepository(
        userWorkerRepository: UserWorkerRepositoryImpl
    ): UserWorkerRepository
}
