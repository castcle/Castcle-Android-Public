package com.castcle.ui.setting.deleteaccount

import com.castcle.common_model.model.userprofile.DeletePageRequest
import com.castcle.ui.base.BaseViewModel
import com.castcle.usecase.userprofile.DeleteAccountCompletableUseCase
import com.castcle.usecase.userprofile.DeletePageCompletableUseCase
import io.reactivex.Completable
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
//  Created by sklim on 1/9/2021 AD at 18:14.

abstract class DeletePageFragmentViewModel : BaseViewModel() {

    abstract fun onDeleteAccount(deletePageRequest: DeletePageRequest): Completable

    abstract fun onDeletePage(deletePageRequest: DeletePageRequest): Completable
}

class DeletePageFragmentViewModelImpl @Inject constructor(
    private val deleteAccountCompletableUseCase: DeleteAccountCompletableUseCase,
    private val deletePageCompletableUseCase: DeletePageCompletableUseCase
) : DeletePageFragmentViewModel() {

    override fun onDeleteAccount(deletePageRequest: DeletePageRequest): Completable {
        return deleteAccountCompletableUseCase.execute(deletePageRequest)
    }

    override fun onDeletePage(deletePageRequest: DeletePageRequest): Completable {
        return deletePageCompletableUseCase.execute(deletePageRequest)
    }
}
