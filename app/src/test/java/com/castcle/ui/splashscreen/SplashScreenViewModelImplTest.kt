package com.castcle.ui.splashscreen

import com.castcle.session_memory.SessionManagerRepository
import com.castcle.ui.base.BaseViewModelTest
import com.castcle.usecase.EngagementTackingCompletableUseCase
import com.castcle.usecase.GuestLoginCompletableUseCase
import com.castcle.usecase.setting.GetCurrentLocalSingleUseCase
import com.castcle.usecase.setting.SetAppLanguageUseCase
import com.castcle.usecase.userprofile.GetCachedUserProfileSingleUseCase
import com.castcle.usecase.userprofile.IsGuestModeSingleUseCase
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

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
//  Created by sklim on 30/8/2021 AD at 22:33.
@RunWith(MockitoJUnitRunner::class)
class SplashScreenViewModelImplTest : BaseViewModelTest() {

    private lateinit var splashScreenViewModel: SplashScreenViewModel

    @Mock
    private lateinit var getLoginGuestUseCase: GuestLoginCompletableUseCase

    @Mock
    private lateinit var isGuestModeSingleUseCase: IsGuestModeSingleUseCase

    @Mock
    private lateinit var guestLoginCompletableUseCase: GuestLoginCompletableUseCase

    @Mock
    private lateinit var getCurrentLocalSingleUseCase: GetCurrentLocalSingleUseCase

    @Mock
    private lateinit var setAppLanguageUseCase: SetAppLanguageUseCase

    @Mock
    private lateinit var engagementTackingCompletableUseCase: EngagementTackingCompletableUseCase

    @Mock
    private lateinit var cachedUserProfileSingleUseCase: GetCachedUserProfileSingleUseCase

    override fun setUpTest() {
        splashScreenViewModel = SplashScreenViewModelImpl(
            isGuestModeSingleUseCase,
            guestLoginCompletableUseCase,
            getCurrentLocalSingleUseCase,
            setAppLanguageUseCase,
            engagementTackingCompletableUseCase,
            cachedUserProfileSingleUseCase
        )
    }

    @Test
    fun `get Login Guest,session memoy can get Token`() {
        whenever(getLoginGuestUseCase.execute(any()))
            .thenReturn(Completable.complete())

        val testObserver = splashScreenViewModel.requestGuestLogin().test()

        testObserver
            .assertNoErrors()
            .hasSubscription()
        verify(getLoginGuestUseCase).execute(any())
    }

}
