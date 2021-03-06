package com.castcle.data.datasource

import com.castcle.common_model.model.userprofile.domain.CreateCastResponse
import com.castcle.common_model.model.userprofile.domain.UserProfileResponse
import com.castcle.data.repository.CreateContentMapper
import com.castcle.data.repository.UserProfileMapper
import com.castcle.networking.api.user.UserApi
import dagger.Module
import dagger.Provides
import io.reactivex.functions.Function
import retrofit2.Response
import retrofit2.Retrofit

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
//  Created by sklim on 1/9/2021 AD at 09:49.
@Module
class UserProfileDataSourceModule {

    @Provides
    fun userProfileApi(
        retrofit: Retrofit
    ): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    fun provideUserProfileResponseMapper(
        userProfileMapper: UserProfileMapper
    ): Function<Response<UserProfileResponse>, UserProfileResponse> = userProfileMapper

    @Provides
    fun provideCreateContentResponseMapper(
        createContentMapper: CreateContentMapper
    ): Function<Response<CreateCastResponse>, CreateCastResponse> = createContentMapper

}
