package com.castcle.ui.signin.profilechooseimage

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCreateProfileChooseBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.login.domain.toCreatePage
import com.castcle.common_model.model.setting.domain.CreatePageRequest
import com.castcle.common_model.model.setting.domain.ImagesPageRequest
import com.castcle.common_model.model.userprofile.domain.ImagesRequest
import com.castcle.common_model.model.userprofile.domain.UserUpdateRequest
import com.castcle.extensions.*
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.usecase.createblog.setConfig
import com.permissionx.guolindev.PermissionMediator
import io.reactivex.rxkotlin.subscribeBy
import me.shouheng.compress.Compress
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
class ProfileChooseFragment : BaseFragment<ProfileChooseFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentCreateProfileChooseBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var easyImage: EasyImage

    @Inject lateinit var rxPermissions: PermissionMediator

    private val authBundle: ProfileChooseFragmentArgs by navArgs()

    private val emailBundle: ProfileBundle
        get() = authBundle.profileBundle

    private val isCreatePage: Boolean
        get() = authBundle.isCreatePage

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateProfileChooseBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCreateProfileChooseBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCreateProfileChooseBinding
        get() = viewBinding as FragmentCreateProfileChooseBinding

    override fun viewModel(): ProfileChooseFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ProfileChooseFragmentViewModel::class.java)

    override fun initViewModel() = Unit

    override fun setupView() {
        setupToolBar()
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.run {
                visible()
                text = context.getString(R.string.tool_bar_skip)
                subscribeOnClick {
                    handlerSkipAction()
                }.addToDisposables()
            }
            tvToolbarTitle.gone()
            ivToolbarLogoButton
                .subscribeOnClick {
                    handleNavigateBack()
                }.addToDisposables()
        }
    }

    private fun handleNavigateBack() {
        with(binding) {
            if (cvCropImage.isVisible) {
                cvCropImage.gone()
                group.visible()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun handlerSkipAction() {
        if (isCreatePage) {
            val profileBundle = emailBundle as ProfileBundle.ProfileWithEmail
            navigateToAboutMe(profileBundle)
        } else {
            navigateToVerifyEmail()
        }
    }

    private fun navigateToVerifyEmail() {
        onBoardNavigator.naivgetToProfileVerifyEmailFragment(emailBundle)
    }

    override fun bindViewEvents() {
        with(binding) {
            btTakePhoto.subscribeOnClick {
                requestCameraPermission(action = {
                    openCamera()
                })
            }
            btChoosePhoto.subscribeOnClick {
                requestGalleryPermission(action = {
                    openGallery()
                })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        easyImage.handleActivityResult(requestCode, resultCode, data, requireActivity(),
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    imageFiles
                        .firstOrNull()?.let(::handlerCropImageAvatar)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                }
            })
    }

    private fun handlerCropImageAvatar(media: MediaFile) {
        toolBarApplyAction()
        val contentUri: Uri = Uri.fromFile(media.file)
        with(binding) {
            cvCropImage.visible()
            group.gone()
            cvCropImage.setImageURI(contentUri)
        }
    }

    private fun toolBarApplyAction() {
        with(toolbarBinding.tvToolbarTitleAction) {
            text = context.getString(R.string.tool_bar_apply)
            subscribeOnClick {
                applyImageProfile()
            }
        }
    }

    private fun applyImageProfile() {
        val profileBundle = emailBundle as ProfileBundle.ProfileWithEmail
        val image = Compress.with(
            requireContext(),
            binding.cvCropImage.croppedBitmap
        ).setConfig().get().toBase64String()

        if (isCreatePage) {
            updatePageAvatar(image, profileBundle)
        } else {
            updateProfileAvatar(image, profileBundle)
        }
    }

    private fun updatePageAvatar(image: String, profileBundle: ProfileBundle.ProfileWithEmail) {
        viewModel.requestUpdatePage(
            CreatePageRequest(
                images = ImagesPageRequest(
                    avatar = image
                ),
                displayName = profileBundle.displayName ?: "",
                castcleId = profileBundle.castcleId
            )
        ).subscribeBy(
            onSuccess = {
                profileBundle.apply {
                    imageAvatar = it.images.avatar?.original ?: ""
                }.run(::navigateToAboutMe)
            },
            onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun navigateToAboutMe(profileBundle: ProfileBundle.ProfileWithEmail) {
        onBoardNavigator.navigateToAboutYouFragment(profileBundle.toCreatePage(), true)
    }

    private fun updateProfileAvatar(image: String, profileBundle: ProfileBundle.ProfileWithEmail) {
        viewModel.requestUpdateProfile(
            UserUpdateRequest(
                images = ImagesRequest(avatar = image)
            )
        ).subscribeBy(onComplete = {
            navigateToVerifyEmailFragment(image, profileBundle)
        }).addToDisposables()
    }

    private fun navigateToVerifyEmailFragment(
        image: String,
        bundle: ProfileBundle.ProfileWithEmail
    ) {
        onBoardNavigator.naivgetToProfileVerifyEmailFragment(
            ProfileBundle.ProfileWithEmail(
                email = bundle.email,
                imageAvatar = image,
                castcleId = bundle.castcleId,
                displayName = bundle.displayName
            )
        )
    }

    override fun bindViewModel() {
        viewModel.showLoading.subscribe {
            onBindShowLoading(it)
        }.addToDisposables()
    }

    private fun onBindShowLoading(show: Boolean) {
        binding.pbLoading.visibleOrGone(show)
    }
}