package com.castcle.ui.login

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.castcle.common_model.model.login.domain.LoginRequest
import com.castcle.common_model.model.userprofile.*
import com.castcle.common_model.model.userprofile.domain.PageResponse
import com.castcle.common_model.model.userprofile.domain.UserProfileResponse
import com.castcle.data.model.dao.user.UserDao
import com.castcle.usecase.auth.RegisterFireBaseTokenCompleteUseCase
import com.castcle.usecase.login.AuthenticationLoginWithEmailCompletableUseCase
import com.castcle.usecase.login.AuthenticationRefreshTokenCompletableUseCase
import com.castcle.usecase.userprofile.*
import io.reactivex.Completable
import io.reactivex.Observable
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
    AuthenticationLoginWithEmailCompletableUseCase,
    private val authenticationRefreshTokenCompletableUseCase:
    AuthenticationRefreshTokenCompletableUseCase,
    private val updateProfileDataCompletableUseCase: UpdateProfileDataCompletableUseCase,
    private val updateUserPageDataCompletableUseCase: UpdateUserPageDataCompletableUseCase,
    private val registerFireBaseTokenCompletaUseCase: RegisterFireBaseTokenCompleteUseCase
) : LoginFragmentViewModel(), LoginFragmentViewModel.Input {

    override val userResponse: LiveData<User>
        get() = _userResponse
    private var _userResponse = MutableLiveData<User>()

    private var _userEmail = MutableLiveData<String>()

    private var _password = MutableLiveData<String>()

    private val _enableLogin = MutableLiveData<Boolean>()

    override val showLoading: Observable<Boolean>
        get() = _showLoading
    private val _showLoading = PublishSubject.create<Boolean>()

    private val _error = PublishSubject.create<Throwable>()

    override val enableLogin: LiveData<Boolean>
        get() = _enableLogin

    override val input: Input
        get() = this

    @SuppressLint("CheckResult")
    override fun getAuthLoginWithEmail(): Completable {
        return authenticationLoginWithEmailCompletableUseCase.execute(
            LoginRequest(_userEmail.value!!, _password.value!!)
        ).doOnSubscribe { _showLoading.onNext(true) }
            .doOnSuccess {
                updateUserProfile(it.userProfileResponse)
                updateUserPage(it.pageResponse)
            }
            .flatMapCompletable {
                onUpdateFireBaseToken()
            }.doOnError {
                _showLoading.onNext(false)
            }.doFinally { _showLoading.onNext(false) }
    }

    private fun onUpdateFireBaseToken(): Completable {
        return registerFireBaseTokenCompletaUseCase.execute(Unit)
    }

    override fun refreshToken(): Completable {
        return authenticationRefreshTokenCompletableUseCase
            .execute(Unit)
    }

    private fun updateUserProfile(userProfileResponse: UserProfileResponse): Completable {
        val userProfile = userProfileResponse.toUserProfile()
        setUserProfile(userProfile)
        updateProfileDataCompletableUseCase.execute(userProfile).subscribe().addToDisposables()
        return Completable.complete()
    }

    private fun updateUserPage(userProfileResponse: List<PageResponse>): Completable {
        val userPageList = userProfileResponse.toUserPageDao()
        updateUserPageDataCompletableUseCase.execute(userPageList).subscribe().addToDisposables()
        return Completable.complete()
    }

    private fun setUserProfile(user: User) {
        _userResponse.value = user
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
        return _userEmail.value?.isNotBlank() == true
    }
}
