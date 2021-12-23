package com.castcle.ui.signin.createaccount

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCreateAccountBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.login.domain.RegisterBundle
import com.castcle.common_model.model.signin.AuthVerifyBaseUiModel.DisplayNameVerifyUiModel
import com.castcle.common_model.model.signin.domain.RegisterWithSocialPayLoad
import com.castcle.common_model.model.signin.domain.RegisterWithSocialRequest
import com.castcle.common_model.model.userprofile.domain.ImagesRequest
import com.castcle.common_model.model.userprofile.domain.UserUpdateRequest
import com.castcle.data.error.RegisterErrorError
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.dialog.chooseimage.KEY_CHOOSE_REQUEST
import com.castcle.ui.common.dialog.chooseimage.PhotoSelectedState
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.profile.RC_CROP_IMAGE
import com.castcle.ui.signin.createdisplayname.VerifyProfileState
import com.lyrebirdstudio.croppylib.Croppy
import com.lyrebirdstudio.croppylib.main.CropRequest
import com.permissionx.guolindev.PermissionMediator
import io.reactivex.rxkotlin.subscribeBy
import pl.aprilapps.easyphotopicker.*
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
//  Created by sklim on 1/9/2021 AD at 18:12.

class CreateAccountFragment : BaseFragment<CreateAccountFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentCreateAccountBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    @Inject lateinit var rxPermissions: PermissionMediator

    @Inject lateinit var easyImage: EasyImage

    private val authBundle: CreateAccountFragmentArgs by navArgs()

    private val registerBundle: RegisterBundle
        get() = authBundle.registerBundle

    private val isRegisterPass = MutableLiveData<Boolean>()

    private var imageAvatar: String = ""

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateAccountBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCreateAccountBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCreateAccountBinding
        get() = viewBinding as FragmentCreateAccountBinding

    override fun viewModel(): CreateAccountFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(CreateAccountFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.invisible()
            tvToolbarTitle.gone()
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        getNavigationResult<PhotoSelectedState>(
            onBoardNavigator,
            R.id.createAccountFragment,
            KEY_CHOOSE_REQUEST,
            onResult = {
                onHandlerStatePhoto(it)
            })

        with(binding) {
            val registerWithSocial = registerBundle as RegisterBundle.RegisterWithSocial
            imageAvatar = registerWithSocial.userAvatar
            onBindAvatar(registerWithSocial.userAvatar)

            ivAddAvatar.subscribeOnClick {
                onNavigateToChooseProfileEdit()
            }.addToDisposables()

            itDisplatName.setRequestFocus()
            with(itDisplatName) {
                primaryText = registerWithSocial.userName
                if (registerWithSocial.userName.isNotBlank()) {
                    viewModel.input.displayName(registerWithSocial.userName)
                }
                onTextChanged = {
                    viewModel.input.displayName(it)
                }
                onEditorActionNext = {
                    itCastcleId.setRequestFocus()
                }
            }
            with(itCastcleId) {
                onTextChanged = {
                    viewModel.input.checkCastcleId(it)
                }
                onEditorActionListener = { actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btNext.callOnClick()
                        true
                    }
                    false
                }
            }
            btNext.subscribeOnClick {
                onRegister()
            }.addToDisposables()
        }
    }

    private fun onNavigateToChooseProfileEdit() {
        onBoardNavigator.navigateToDialogChooseFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        easyImage.handleActivityResult(requestCode, resultCode, data, requireActivity(),
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    imageFiles
                        .firstOrNull()?.let(::handlerCorpImage)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                }
            })
    }

    private fun handlerCorpImage(mediaFile: MediaFile) {
        onNavigateToCropAvatarImageFragment(mediaFile.file.toUri())
    }

    private fun onNavigateToCropAvatarImageFragment(imageUri: Uri) {
        val cropRequest = CropRequest.Auto(
            sourceUri = imageUri,
            requestCode = RC_CROP_IMAGE_ACCOUNT
        )
        Croppy.start(requireActivity(), cropRequest)
    }

    private fun onHandlerStatePhoto(state: PhotoSelectedState) {
        when (state) {
            PhotoSelectedState.SELECT_TAKE_CAMERA -> {
                requestCameraPermission(action = {
                    openCamera()
                })
            }
            PhotoSelectedState.SELECT_CHOOSE -> {
                requestGalleryPermission(action = {
                    openGallery()
                })
            }
            else -> {
            }
        }
    }

    private fun openCamera() {
        easyImage.openCameraForImage(this)
    }

    private fun openGallery() {
        easyImage.openGallery(this)
    }

    private fun requestCameraPermission(action: () -> Unit) {
        rxPermissions.permissions(Manifest.permission.CAMERA)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK", "Cancel"
                )
            }.request { allGranted, _, deniedList ->
                if (allGranted) {
                    action.invoke()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun requestGalleryPermission(action: () -> Unit) {
        rxPermissions.permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK", "Cancel"
                )
            }.request { allGranted, _, deniedList ->
                if (allGranted) {
                    action.invoke()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun onBindAvatar(avatarUrl: String) {
        binding.ivAvatar.loadCircleImage(avatarUrl)
    }

    private fun onRegister() {
        val registerBundle = registerBundle as RegisterBundle.RegisterWithSocial
        with(binding) {
            val registerRequest = RegisterWithSocialRequest(
                provider = registerBundle.provider,
                payload = RegisterWithSocialPayLoad(
                    authToken = registerBundle.authToken
                )
            )

            viewModel.input.authRegisterWithSocial(
                registerRequest
            ).subscribeBy(
                onComplete = {

                },
                onError = {
                    handlerError(it)
                }
            ).addToDisposables()
        }
    }

    private fun onUpDateProfile() {
        with(binding) {
            itDisplatName.primaryText
            itCastcleId.primaryText

            viewModel.requestUpdateProfile(
                UserUpdateRequest(
                    images = ImagesRequest(
                        avatar = imageAvatar
                    )
                )
            ).subscribeBy(onComplete = {

            }).addToDisposables()
        }
    }

    private fun handlerError(error: Throwable) {
        if (error is RegisterErrorError && error.hasAuthenticationTokenExprierd()) {
            binding.itCastcleId.setError(
                error = error.readableMessage,
                isShowErrorWithBackground = true
            )
        }
        if (error is RegisterErrorError && error.hasAuthenticationCastcleIdInSystem()) {
            binding.itCastcleId.setError(
                error = error.readableMessage,
                isShowErrorWithBackground = true
            )
        }
        if (error is RegisterErrorError && error.hasAuthenticationUserInSystem()) {
            binding.itCastcleId.setError(
                error = error.readableMessage,
                isShowErrorWithBackground = true
            )
        }
        if (error is RegisterErrorError && error.hasAuthenticationEmailNotFound()) {
            binding.itCastcleId.setError(
                error = error.readableMessage,
                isShowErrorWithBackground = true
            )
        }
    }

    private fun navigateToChooseProfile(isCreatePage: Boolean = false) {
        with(binding) {
            isRegisterPass.value = true
            val registerBundle = registerBundle as RegisterBundle.RegisterWithEmail
            onBoardNavigator.naivgetToProfileChooseImageFragment(
                ProfileBundle.ProfileWithEmail(
                    email = registerBundle.email,
                    displayName = itDisplatName.primaryText,
                    castcleId = itCastcleId.primaryText,
                ), isCreatePage
            )
        }
    }

    override fun bindViewModel() {
        viewModel.verifyProfileState
            .subscribe(::handlerUiState)
            .addToDisposables()

        viewModel.showLoading
            .subscribe {
                showLoading(it)
            }.addToDisposables()

        viewModel.error
            .subscribe {
                displayError(it)
            }.addToDisposables()

        viewModel.responseCastcleIdSuggest
            .subscribe(::fieldInCastcleSuggest)
            .addToDisposables()

        viewModel.responseCastcleIdExsit.subscribe().addToDisposables()

        activityViewModel.imageResponse.observe(viewLifecycleOwner, {
            onBindAvatar(it.toString())
        })
    }

    private fun showLoading(isLoad: Boolean) {
        with(binding) {
            btNext.visibleOrInvisible(!isLoad)
            pbLoading.visibleOrGone(isLoad)
        }
    }

    private fun fieldInCastcleSuggest(displayNameVerifyUiModel: DisplayNameVerifyUiModel) {
        binding.itCastcleId.primaryText = displayNameVerifyUiModel.castcleIdSuggestions
    }

    private fun handlerUiState(verifyProfileState: VerifyProfileState) {
        when (verifyProfileState) {
            VerifyProfileState.CASTCLE_ID_ERROR -> {
                endableButtomNext(false)
                binding.itCastcleId.setError(
                    localizedResources.getString(R.string.register_warning_message)
                )
            }
            VerifyProfileState.CASTCLE_ID_PASS -> {
                endableButtomNext(true)
            }
            else -> {
                endableButtomNext(false)
            }
        }
    }

    private fun endableButtomNext(enable: Boolean) {
        with(binding.btNext) {
            isActivated = enable
            isEnabled = enable
        }
    }
}

const val RC_CROP_IMAGE_ACCOUNT = 192
