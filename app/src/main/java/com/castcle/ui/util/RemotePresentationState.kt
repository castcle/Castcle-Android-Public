package com.castcle.ui.util

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

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
//  Created by sklim on 9/12/2021 AD at 21:22.

enum class RemotePresentationState {
    INITIAL, REMOTE_LOADING, SOURCE_LOADING, PRESENTED
}

@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<CombinedLoadStates>.asRemotePresentationState(): Flow<RemotePresentationState> =
    scan(RemotePresentationState.INITIAL) { state, loadState ->
        when (state) {
            RemotePresentationState.PRESENTED -> when (loadState.mediator?.refresh) {
                is LoadState.Loading -> RemotePresentationState.REMOTE_LOADING
                else -> state
            }
            RemotePresentationState.INITIAL -> when (loadState.mediator?.refresh) {
                is LoadState.Loading -> RemotePresentationState.REMOTE_LOADING
                else -> state
            }
            RemotePresentationState.REMOTE_LOADING -> when (loadState.source.refresh) {
                is LoadState.Loading -> RemotePresentationState.SOURCE_LOADING
                else -> state
            }
            RemotePresentationState.SOURCE_LOADING -> when (loadState.source.refresh) {
                is LoadState.NotLoading -> RemotePresentationState.PRESENTED
                else -> state
            }
        }
    }.distinctUntilChanged()
