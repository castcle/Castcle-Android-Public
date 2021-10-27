package com.castcle.di.component

import android.app.Application
import com.castcle.CastcleApplication
import com.castcle.authen_android.di.components.AuthenticateComponent
import com.castcle.di.modules.ActivityModule
import com.castcle.di.modules.AppModule
import com.castcle.di.modules.analytics.AppCenterAnalyticModule
import com.castcle.di.scope.ApplicationScope
import com.castcle.di.service.ServiceModule
import com.castcle.di.worker.WorkerModule
import com.castcle.session_memory.SessionManagerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

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
//  Created by sklim on 18/8/2021 AD at 18:52.
@ApplicationScope
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ServiceModule::class,
        ActivityModule::class,
        SessionManagerModule::class,
        AppCenterAnalyticModule::class,
        WorkerModule::class
    ],
    dependencies = [
        AuthenticateComponent::class,
        StorageComponent::class
    ]
)
interface AppComponent : AndroidInjector<CastcleApplication> {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            authenticateComponent: AuthenticateComponent,
            storageComponent: StorageComponent
        ): AppComponent
    }
}
