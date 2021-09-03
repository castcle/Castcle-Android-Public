package com.castcle.ui.signin.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.extensions.isPasswordPatten
import com.castcle.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
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
//  Created by sklim on 1/9/2021 AD at 18:14.

abstract class PasswordFragmentViewModel : BaseViewModel() {

    abstract val showLoading: Observable<Boolean>

    abstract val enableContinue: LiveData<Boolean>

    abstract val verifyPassword: Observable<Boolean>

    abstract val verifyReTypePassword: Observable<Boolean>

    abstract val passwordError: LiveData<VerifyPassState>

    abstract val input: Input

    interface Input {
        fun password(password: String)
        fun retypePassword(retypePassword: String)
    }
}

class PasswordFragmentViewModelImpl @Inject constructor(
) : PasswordFragmentViewModel(), PasswordFragmentViewModel.Input {

    override val verifyPassword: Observable<Boolean>
        get() = _verifyPassword

    override val verifyReTypePassword: Observable<Boolean>
        get() = _varifyReTypePassword

    private val _verifyPassword = BehaviorSubject.create<Boolean>()

    private val _varifyReTypePassword = BehaviorSubject.create<Boolean>()

    private var _enableContinue = MutableLiveData<Boolean>()

    private var _passwordVerifyState = MutableLiveData<VerifyPassState>()
    override val passwordError: LiveData<VerifyPassState>
        get() = _passwordVerifyState

    override val showLoading: Observable<Boolean>
        get() = _showLoading
    private val _showLoading = PublishSubject.create<Boolean>()

    override val enableContinue: LiveData<Boolean>
        get() = _enableContinue

    override val input: Input
        get() = this

    private var _password = MutableLiveData<String>()

    private var _retypePassword = MutableLiveData<String>()

    override fun password(password: String) {
        _password.value = password
        when {
            password.isPasswordPatten() && password.length >= DEFAULT_CHECK_LENGTH -> {
                _passwordVerifyState.postValue(VerifyPassState.PASSWORD_PASS)
                checkMatchingPassword()
            }
            password.length > DEFAULT_CHECK_LENGTH -> {
                _passwordVerifyState.postValue(VerifyPassState.PASSWORD_PATTRN)
            }
        }
    }

    override fun retypePassword(retypePassword: String) {
        _retypePassword.value = retypePassword
        when {
            retypePassword.isPasswordPatten() && retypePassword.length >= DEFAULT_CHECK_LENGTH -> {
                _passwordVerifyState.postValue(VerifyPassState.RETYPE_PASSWORD_PASS)
                checkMatchingPassword()
            }
            retypePassword.length > DEFAULT_CHECK_LENGTH -> {
                _passwordVerifyState.postValue(VerifyPassState.RETYPE_PASSWORD_PATTRN)
            }
        }
    }

    private fun checkMatchingPassword() {
        when {
            _retypePassword.value.isNullOrEmpty() && _password.value?.isNotBlank() == true -> {
                _passwordVerifyState.postValue(VerifyPassState.PASSWORD_LENGTH)
            }
            _retypePassword.value?.isNotBlank() == true && _password.value.isNullOrBlank() -> {
                _passwordVerifyState.postValue(VerifyPassState.RETYPE_PASSWORD_LENGTH)
            }
            _retypePassword.value.equals(_password.value) -> {
                setEnableContinue(true)
                _passwordVerifyState.postValue(VerifyPassState.PASSWORD_MATCH_PASS)
            }
            else -> {
                setEnableContinue(false)
                _passwordVerifyState.postValue(VerifyPassState.PASSWORD_NOT_MATCH)
            }
        }
    }

    private fun setEnableContinue(enable: Boolean) {
        _enableContinue.value = enable
    }
}

private const val DEFAULT_CHECK_LENGTH = 6
