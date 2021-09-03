package com.castcle.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common_model.model.login.LoginRequest
import com.castcle.extensions.isEmail
import com.castcle.usecase.login.AuthenticationLoginWithEmailCompletableUseCase
import io.reactivex.Completable
import io.reactivex.subjects.PublishSubject
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
//  Created by sklim on 31/8/2021 AD at 09:08.

class LoginFragmentViewModelImpl @Inject constructor(
    private val authenticationLoginWithEmailCompletableUseCase:
    AuthenticationLoginWithEmailCompletableUseCase
) : LoginFragmentViewModel(), LoginFragmentViewModel.Input {

    private var _userEmail = MutableLiveData<String>()

    private var _password = MutableLiveData<String>()

    private val _enableLogin = MutableLiveData<Boolean>()

    private val _showLoading = PublishSubject.create<Boolean>()

    override val enableLogin: LiveData<Boolean>
        get() = _enableLogin

    override val input: Input
        get() = this

    override fun getAuthLoginWithEmail(): Completable {
        return authenticationLoginWithEmailCompletableUseCase.execute(
            LoginRequest(_userEmail.value!!, _password.value!!)
        ).doOnSubscribe { _showLoading.onNext(true) }
            .doOnError { _showLoading.onNext(false) }
    }

    override fun userEmail(email: String) {
        _userEmail.value = email
    }

    override fun password(password: String) {
        _password.value = password
        validateEnableLogin()
    }

    private fun validateEnableLogin() {
        _enableLogin.value = validateEmail() && _password.value?.isNotBlank() == true
    }

    private fun validateEmail(): Boolean {
        return _userEmail.value?.isEmail() == true
    }
}
