package com.castcle.ui.profile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.*
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.login.ProfileBundle
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.common_model.model.setting.UpLoadType
import com.castcle.common_model.model.userprofile.*
import com.castcle.data.staticmodel.TabContentStatic.tabContent
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.networking.api.user.PROFILE_TYPE_PAGE
import com.castcle.ui.base.*
import com.castcle.ui.common.dialog.chooseimage.KEY_CHOOSE_REQUEST
import com.castcle.ui.common.dialog.chooseimage.PhotoSelectedState
import com.castcle.ui.common.dialog.profilechoose.KEY_PROFILE_CHOOSE_REQUEST
import com.castcle.ui.common.dialog.profilechoose.ProfileEditState
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.castcle.ui.profile.adapter.ContentPageAdapter
import com.google.android.material.tabs.TabLayoutMediator
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
//  Created by sklim on 10/9/2021 AD at 11:38.

class ProfileFragment : BaseFragment<ProfileFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentProfileBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    @Inject lateinit var easyImage: EasyImage

    @Inject lateinit var rxPermissions: PermissionMediator

    private lateinit var contentPageAdapter: ContentPageAdapter

    private val argsBundle: ProfileFragmentArgs by navArgs()

    private val profileType: String
        get() = argsBundle.profileType

    private val profileId: String
        get() = argsBundle.id

    private val isMe: Boolean
        get() = argsBundle.isMe

    private var photoSelectedState: PhotoSelectedState = PhotoSelectedState.NON

    private var profileTypeState: ProfileType = ProfileType.NON

    private lateinit var userProfile: User

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = { inflater, container, attachToRoot ->
            FragmentProfileBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentProfileBinding
        get() = viewBinding as FragmentProfileBinding

    override fun viewModel(): ProfileFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(ProfileFragmentViewModel::class.java)

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun initViewModel() {
        viewModel.checkAvatarUploading().subscribe { it ->
            if (it) {
                activityViewModel.onRefreshProfile()
                displayErrorMessage("Up Load Image Success")
            }
        }.addToDisposables()
    }

    private fun profileType(
        onProfileMe: () -> Unit,
        onProfileYou: () -> Unit,
        onPage: () -> Unit
    ) {
        when (profileType) {
            PROFILE_TYPE_ME -> {
                onProfileMe.invoke()
                activityViewModel.setProfileType(ProfileType.PROFILE_TYPE_ME)
            }
            PROFILE_TYPE_PAGE -> {
                onPage.invoke()
                activityViewModel.setProfileType(ProfileType.PROFILE_TYPE_PAGE)
            }
            else -> {
                onProfileYou.invoke()
                activityViewModel.setProfileType(ProfileType.PROFILE_TYPE_PEOPLE)
            }
        }
        activityViewModel.setContentTypeYouId(profileId)
    }

    override fun setupView() {
        setupToolBar()
        with(binding.vpPageContent) {
            adapter = ContentPageAdapter(this@ProfileFragment).also {
                contentPageAdapter = it
            }
        }

        TabLayoutMediator(
            binding.tabs,
            binding.vpPageContent
        ) { Tab, position ->
            Tab.text = requireContext().getString(tabContent[position].tabNameRes)
        }.attach()

        profileType(
            onPage = {
                binding.profileMe.clMainContent.gone()
                binding.profileYou.clMainContent.visible()
            }, onProfileMe = {
                binding.profileMe.clMainContent.visible()
                binding.profileYou.clMainContent.gone()
            }, onProfileYou = {
                binding.profileMe.clMainContent.gone()
                binding.profileYou.clMainContent.visible()
            })
    }

    private fun setupToolBar() {
        with(binding.tbProfile) {
            ivToolbarLogoButton.visible()
            tvToolbarTitle.setTextColor(
                requireContext().getColorResource(R.color.white)
            )
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    override fun bindViewEvents() {
        getNavigationResult<PhotoSelectedState>(
            onBoardNavigator,
            R.id.profileFragment,
            KEY_CHOOSE_REQUEST,
            onResult = {
                onHandlerStatePhoto(it)
            })

        getNavigationResult<ProfileEditState>(
            onBoardNavigator,
            R.id.profileFragment,
            KEY_PROFILE_CHOOSE_REQUEST,
            onResult = {
                onHandlerProfileChoose(it)
            })
    }

    private fun onHandlerProfileChoose(state: ProfileEditState) {
        when (state) {
            ProfileEditState.SYNC_SOCIAL_MEDIA -> {

            }
            ProfileEditState.DELETE_PAGE -> {
                onNavigateToDeleteProfile()
            }
            ProfileEditState.SHARE -> {

            }
        }
    }

    private fun onNavigateToDeleteProfile() {
        val profileEditBundle = getProfileEditBundle(profileTypeState)
        onBoardNavigator.navigateToProfileDeletePageFragment(profileEditBundle)
    }

    private fun getProfileEditBundle(profileType: ProfileType): ProfileBundle {
        return ProfileBundle.ProfileDelete(
            castcleId = userProfile.castcleId,
            avatar = userProfile.avatar,
            profileType = profileType.type,
            displayName = userProfile.displayName
        )
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

    override fun bindViewModel() {
        viewModel.errorMessage.observe(this, {
            displayErrorMessage(it)
        })

        profileType(
            onProfileMe = {
                viewModel.userProfileRes
                    .subscribeBy(
                        onError = {
                            displayError(it)
                        }
                    ).addToDisposables()
            }, onProfileYou = {
                viewModel.getUserViewProfile(profileId)

            }, onPage = {
                viewModel.getViewPage(profileId)
            })

        viewModel.userProfileData.observe(this, {
            when (photoSelectedState) {
                PhotoSelectedState.COVER_PAGE_SELECT, PhotoSelectedState.AVATAR_PAGE_SELECT -> {
                    onBindViewProfile(it)
                }
                else -> onBindProfile(it)
            }
        })

        viewModel.userProfileYouRes.subscribe {
            onBindViewProfile(it)
        }.addToDisposables()

        viewModel.showLoading.subscribe {
            handlerShowLoading(it)
        }.addToDisposables()

        viewModel.viewPageRes.subscribe {
            onBindViewProfile(it)
        }.addToDisposables()

        activityViewModel.imageResponse.observe(this, {
            handlerUpLoadImage(it)
        })
    }

    private fun handlerShowLoading(it: Boolean) {
        binding.flCoverLoading.visibleOrGone(it)
    }

    private fun onBindProfile(user: User) {
        profileTypeState = ProfileType.PROFILE_TYPE_ME
        userProfile = user
        with(binding.profileMe) {
            tvFollowingCount.text = user.followersCount.toCount()
            tvFollowersCount.text = user.followersCount.toCount()
            tvProfileName.text = user.displayName
            tvProfileCastcleId.text = user.castcleId

            with(user.overview) {
                tvProfileOverView.visibleOrInvisible(!isEmpty())
                tvProfileOverView.text = this
            }
            ivAddAvatar.subscribeOnClick {
                onNavigateToDialogChooseFragment(
                    PhotoSelectedState.AVATAR_SELECT
                )
            }.addToDisposables()

            ivAvatarProfile.loadCircleImage(user.avatar)

            btViewProfile.subscribeOnClick {
                onNavigateToEditProfile()
            }.addToDisposables()

            ivEditProfile.subscribeOnClick {
                onNavigateToChooseProfileEdit()
            }.addToDisposables()
        }
        with(binding) {
            ivAddCover.subscribeOnClick {
                onNavigateToDialogChooseFragment(
                    PhotoSelectedState.COVER_SELECT
                )
            }.addToDisposables()

            ivProfileCover.loadImageWithCache(user.cover)
            tbProfile.tvToolbarTitle.text = user.castcleId
        }
    }

    private fun onNavigateToChooseProfileEdit() {
        onBoardNavigator.navigateToProfileChooseDialogFragment()
    }

    private fun onBindViewProfile(user: User) {
        userProfile = user
        with(binding.profileYou) {
            tvFollowingCount.text = user.followingCount.toCount()
            tvFollowersCount.text = user.followersCount.toCount()
            tvProfileName.text = user.displayName
            tvProfileCastcleId.text = user.castcleId
            with(user.overview) {
                tvProfileOverView.visibleOrInvisible(!isEmpty())
                tvProfileOverView.text = this
            }
            ivAvatarProfile.loadCircleImage(user.avatar)

            ivEditProfile.subscribeOnClick {
                onNavigateToChooseProfileEdit()
            }.addToDisposables()

            if (isMe) {
                profileTypeState = ProfileType.PROFILE_TYPE_PAGE
                onBindEditImagePage()
            } else {
                profileTypeState = ProfileType.PROFILE_TYPE_PEOPLE
                binding.ivAddCover.gone()
                btFollow.visibleOrGone(!user.followed)
                if (btFollow.isVisible) {
                    btFollow.subscribeOnClick {
                        handlerFollow(user.castcleId)
                    }.addToDisposables()
                }
            }
        }
        with(binding) {
            ivAddCover.subscribeOnClick {
                onNavigateToDialogChooseFragment(
                    PhotoSelectedState.COVER_PAGE_SELECT
                )
            }.addToDisposables()
            ivProfileCover.loadImageWithCache(user.cover)
            tbProfile.tvToolbarTitle.text = user.castcleId
        }
    }

    private fun onBindEditImagePage() {
        binding.ivAddCover.visible()
        with(binding.profileYou) {
            btViewProfile.gone()
            ivAddAvatar.visible()
            btFollow.text = localizedResources.getString(R.string.profile_edit_profile)

            btFollow.subscribeOnClick {
                onNavigateToEditPage()
            }.addToDisposables()

            ivAddAvatar.subscribeOnClick {
                onNavigateToDialogChooseFragment(
                    PhotoSelectedState.AVATAR_PAGE_SELECT
                )
            }.addToDisposables()
        }
    }

    private fun onNavigateToEditPage() {
        val profileBundle = getProfileBundle()
        onBoardNavigator.navigateToAboutYouFragment(profileBundle, onEditPage = true)
    }

    private fun onNavigateToEditProfile() {
        val profileBundle = getProfileBundle()
        onBoardNavigator.navigateToAboutYouFragment(profileBundle, onEditProfile = true)
    }

    private fun getProfileBundle(): ProfileBundle {
        return ProfileBundle.ProfileEdit(
            castcleId = userProfile.castcleId,
            overview = userProfile.overview,
            dob = userProfile.dob,
            facebookLinks = userProfile.facebookLinks,
            twitterLinks = userProfile.twitterLinks,
            youtubeLinks = userProfile.youtubeLinks,
            mediumLinks = userProfile.mediumLinks,
            websiteLinks = userProfile.websiteLinks
        )
    }

    private fun onNavigateToDialogChooseFragment(photoSelectedState: PhotoSelectedState) {
        this.photoSelectedState = photoSelectedState
        onBoardNavigator.navigateToDialogChooseFragment()
    }

    private fun handlerFollow(castcleId: String) {
        viewModel.putToFollowUser(castcleId).subscribeBy(
            onComplete = {
                onBindFollowedComplete()
            }, onError = {
                handlerShowLoading(false)
                handlerOnShowMessageError(it)
            }
        ).addToDisposables()
    }

    private fun handlerOnShowMessageError(error: Throwable) {
        displayError(error)
    }

    private fun onBindFollowedComplete() {
        with(binding.profileYou) {
            btFollow.visibleOrGone(false)
        }
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

    private fun handleCropError(data: Throwable?) {
        data?.let { displayError(it) }
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

    private fun handlerUpLoadImage(mediaFile: Uri?) {
        when (photoSelectedState) {
            PhotoSelectedState.AVATAR_SELECT -> {
                if (mediaFile != null) {
                    onHandlerUpLoadAvatar(mediaFile, UpLoadType.UPLOAD_AVATAR)
                    onBindImageProfile(imageAvatar = mediaFile.toString())
                }
            }
            PhotoSelectedState.COVER_SELECT -> {
                if (mediaFile != null) {
                    onHandlerUpLoadAvatar(mediaFile, UpLoadType.UPLOAD_COVER)
                    onBindImageProfile(imageCover = mediaFile.toString())
                }
            }
            PhotoSelectedState.COVER_PAGE_SELECT -> {
                if (mediaFile != null) {
                    onHandlerUpLoadAvatar(mediaFile, UpLoadType.UPLOAD_PAGE_COVER)
                    onBindImageViewProfile(imageCover = mediaFile.toString())
                }
            }
            PhotoSelectedState.AVATAR_PAGE_SELECT -> {
                if (mediaFile != null) {
                    onHandlerUpLoadAvatar(mediaFile, UpLoadType.UPLOAD_PAGE_AVATAR)
                    onBindImageViewProfile(imageAvatar = mediaFile.toString())
                }
            }
        }
    }

    private fun onBindImageViewProfile(imageAvatar: String? = "", imageCover: String? = "") {
        with(binding.profileYou) {
            if (imageAvatar?.isNotBlank() == true) {
                ivAvatarProfile.loadCircleImage(imageAvatar)
            }
            if (imageCover?.isNotBlank() == true) {
                ivProfileCover.loadImageWithoutTransformation(imageCover)
            }
        }
    }

    private fun onBindImageProfile(imageAvatar: String? = "", imageCover: String? = "") {
        with(binding.profileMe) {
            if (imageAvatar?.isNotBlank() == true) {
                ivAvatarProfile.loadCircleImage(imageAvatar)
            }
            if (imageCover?.isNotBlank() == true) {
                ivProfileCover.loadImageWithoutTransformation(imageCover)
            }
        }
    }

    private fun onHandlerUpLoadAvatar(image: Uri, uploadAvatar: UpLoadType) {
        val imageRequest = when (uploadAvatar) {
            UpLoadType.UPLOAD_AVATAR -> {
                ImagesRequest(
                    avatar = image.toString(),
                    upLoadType = UpLoadType.UPLOAD_AVATAR.type
                )
            }
            UpLoadType.UPLOAD_COVER -> {
                ImagesRequest(
                    cover = image.toString(),
                    upLoadType = UpLoadType.UPLOAD_COVER.type
                )
            }
            UpLoadType.UPLOAD_PAGE_COVER -> {
                ImagesRequest(
                    cover = image.toString(),
                    upLoadType = UpLoadType.UPLOAD_PAGE_COVER.type,
                    castcleId = userProfile.castcleId
                )
            }
            UpLoadType.UPLOAD_PAGE_AVATAR -> {
                ImagesRequest(
                    avatar = image.toString(),
                    upLoadType = UpLoadType.UPLOAD_PAGE_AVATAR.type,
                    castcleId = userProfile.castcleId
                )
            }
        }
        viewModel.upLoadAvatar(imageRequest).subscribe().addToDisposables()
    }

    private fun handlerCorpImage(mediaFile: MediaFile) {
        onNavigateToCropAvatarImageFragment(mediaFile.file.toUri())
    }

    private fun onNavigateToCropAvatarImageFragment(imageUri: Uri) {
        val cropRequest = CropRequest.Auto(
            sourceUri = imageUri,
            requestCode = RC_CROP_IMAGE
        )
        Croppy.start(requireActivity(), cropRequest)
    }
}

private const val PROFILE_TYPE_ME = "me"
const val RC_CROP_IMAGE = 191