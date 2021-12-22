package com.castcle.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentProfileBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.feed.toContentUiModel
import com.castcle.common_model.model.login.domain.CreatePostBundle
import com.castcle.common_model.model.login.domain.ProfileBundle
import com.castcle.common_model.model.setting.ProfileType
import com.castcle.common_model.model.setting.UpLoadType
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.domain.ImagesRequest
import com.castcle.components_android.ui.base.TemplateClicks
import com.castcle.data.staticmodel.TabContentStatic.tabContent
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.networking.api.user.PROFILE_TYPE_PAGE
import com.castcle.ui.base.*
import com.castcle.ui.common.dialog.chooseimage.KEY_CHOOSE_REQUEST
import com.castcle.ui.common.dialog.chooseimage.PhotoSelectedState
import com.castcle.ui.common.dialog.profilechoose.KEY_PROFILE_CHOOSE_REQUEST
import com.castcle.ui.common.dialog.profilechoose.ProfileEditState
import com.castcle.ui.common.dialog.user.KEY_USER_CHOOSE_REQUEST
import com.castcle.ui.common.dialog.user.UserState
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

    private lateinit var profileBundle: ProfileBundle

    private var followState = false

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
            adapter = ContentPageAdapter(requireParentFragment()).also {
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
                    findNavController().popBackStack()
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

        getNavigationResult<UserState>(
            onBoardNavigator,
            R.id.profileFragment,
            KEY_USER_CHOOSE_REQUEST,
            onResult = {
                onHandlerUserChoose(it)
            })


        binding.wtWhatYouMind.clickStatus.subscribe {
            handlerWhatYouMindClick(it)
        }.addToDisposables()
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

    private fun onHandlerUserChoose(state: UserState) {
        when (state) {
            UserState.SHARE -> {
                requireActivity().let {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, "share something")
                    it.startActivity(Intent.createChooser(intent, "share"))
                }
            }

            UserState.BLOCK -> {
                viewModel.blockUser(userProfile.castcleId).subscribeBy(
                    onComplete = {
                        setupBlockedLayout(userProfile)
                    },
                    onError = {
                        displayError(it)
                    }
                ).addToDisposables()
            }

            UserState.REPORT -> {
                viewModel.reportUser(userProfile.castcleId).subscribeBy(
                    onComplete = {
                        onNavigateToReportProfile()
                    },
                    onError = {
                        displayError(it)
                    }
                ).addToDisposables()
            }
        }
    }

    private fun onNavigateToReportProfile() {
        onBoardNavigator.navigateToReportFragment(
            userProfile.castcleId,
            profileTypeState.type,
            userProfile.displayName, false
        )
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
                viewModel.fetchUserProfile()
            },
            onProfileYou = {
                viewModel.getUserViewProfile(profileId)
            },
            onPage = {
                viewModel.getViewPage(profileId)
            })

        viewModel.userProfileData.observe(this, {
            profileType(
                onProfileMe = {
                    onBindProfile(it)
                },
                onProfileYou = {
                    onBindViewProfile(it)
                },
                onPage = {
                    onBindViewProfile(it)
                })
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

        activityViewModel.profileContentLoading.subscribe {
            if (it) {
                startLoadingShimmer()
            } else {
                stopLoadingShimmer()
            }
        }.addToDisposables()
    }

    private fun startLoadingShimmer() {
        with(binding) {
            skeletonLoading.shimmerLayoutLoading.run {
                startShimmer()
                visible()
            }
        }
    }


    private fun stopLoadingShimmer() {
        with(binding) {
            skeletonLoading.shimmerLayoutLoading.run {
                stopShimmer()
                setShimmer(null)
                gone()
            }
        }
    }

    private fun handlerShowLoading(it: Boolean) {
        binding.flCoverLoading.visibleOrGone(it)
    }

    private fun onGuestMode(enable: () -> Unit, disable: () -> Unit) {
        if (viewModel.isGuestMode) {
            disable.invoke()
        } else {
            enable.invoke()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onBindProfile(user: User) {
        profileTypeState = ProfileType.PROFILE_TYPE_ME
        userProfile = user

        onBindWhatYouMind(user)

        with(binding.profileMe) {
            tvFollowingCount.text = getContentCount(user.followingCount)
            tvFollowersCount.text = getContentCount(user.followersCount)
            tvProfileName.text = user.displayName
            tvProfileCastcleId.text = "@${user.castcleId}"
            with(user.overview) {
                tvProfileOverView.visibleOrInvisible(!isEmpty())
                tvProfileOverView.text = this
            }
            ivAddAvatar.subscribeOnClick {
                onGuestMode(enable = {
                    onNavigateToDialogChooseFragment()
                }, disable = {})
            }.addToDisposables()

            ivAvatarProfile.loadCircleImage(user.avatar)

            btViewProfile.subscribeOnClick {
                onGuestMode(enable = {
                    onNavigateToEditProfileOrPage()
                }, disable = {})
            }.addToDisposables()

            ivEditProfile.subscribeOnClick {
                onGuestMode(enable = {
                    onNavigateToChooseProfileEdit(true)
                }, disable = {})
            }.addToDisposables()
        }
        with(binding) {
            ivAddCover.subscribeOnClick {
                onGuestMode(enable = {
                    onNavigateToDialogChooseFragment(
                        PhotoSelectedState.COVER_SELECT
                    )
                }, disable = {})
            }.addToDisposables()

            ivProfileCover.loadImageWithCache(user.cover)
            tbProfile.tvToolbarTitle.text = user.displayName
        }
    }

    private fun onNavigateToDialogChooseFragment() {
        onNavigateToDialogChooseFragment(
            PhotoSelectedState.AVATAR_SELECT
        )
    }

    private fun onNavigateToChooseProfileEdit(isAccount: Boolean = false) {
        onBoardNavigator.navigateToProfileChooseDialogFragment(isAccount)
    }

    private fun onNavigateToChooseUserEdit() {
        onBoardNavigator.navigateToUserChooseDialogFragment(userProfile.displayName)
    }

    private fun onBindViewProfile(user: User) {
        userProfile = user

        when {
            user.blocking -> {
                setupBlockingLayout(user)
            }
            user.blocked -> {
                setupBlockedLayout(user)
            }
            else -> {
                setupUnBlockLayout(user)
            }
        }

        with(binding) {
            ivAddCover.subscribeOnClick {
                onGuestMode(enable = {
                    onNavigateToDialogChooseFragment(
                        PhotoSelectedState.COVER_PAGE_SELECT
                    )
                }, disable = {})
            }.addToDisposables()
            ivProfileCover.loadImageWithCache(user.cover)
            tbProfile.tvToolbarTitle.text = user.castcleId
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupBlockingLayout(user: User) {
        with(binding) {
            profileYouBlocked.clMainContent.gone()
            profileYouBlocking.clMainContent.visible()
            profileYou.clMainContent.gone()
            wtWhatYouMind.gone()
            tabs.gone()
            layoutContent.gone()
            line.gone()
        }
        with(binding.profileYouBlocking) {
            tvProfileName.text = user.displayName
            tvProfileCastcleId.text = "@${user.castcleId}"
            ivAvatarProfile.loadCircleImage(user.avatar)
            tvBlockingDes2.text = getString(R.string.profile_blocking_des2, user.displayName)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUnBlockLayout(user: User) {
        with(binding) {
            profileYou.clMainContent.visible()
            profileYouBlocked.clMainContent.gone()
            wtWhatYouMind.visible()
            tabs.visible()
            layoutContent.visible()
            line.visible()
        }
        with(binding.profileYou) {
            tvFollowingCount.text = getContentCount(user.followingCount)
            tvFollowersCount.text = getContentCount(user.followersCount)
            tvProfileName.text = user.displayName
            tvProfileCastcleId.text = "@${user.castcleId}"
            with(user.overview) {
                tvProfileOverView.visibleOrInvisible(!isBlank())
                tvProfileOverView.text = this
            }
            ivAvatarProfile.loadCircleImage(user.avatar)

            btViewProfile.subscribeOnClick {
                onGuestMode(enable = {
                    onNavigateToEditProfileOrPage()
                }, disable = {})
            }.addToDisposables()

            if (isMe) {
                profileTypeState = ProfileType.PROFILE_TYPE_PAGE
                ivEditProfile.subscribeOnClick {
                    onGuestMode(enable = {
                        onNavigateToChooseProfileEdit()
                    }, disable = {})
                }.addToDisposables()
                onBindEditImagePage()
                onBindWhatYouMind(user)
            } else {
                profileTypeState = ProfileType.PROFILE_TYPE_PEOPLE
                ivEditProfile.subscribeOnClick {
                    onGuestMode(enable = {
                        onNavigateToChooseUserEdit()
                    }, disable = {})
                }.addToDisposables()

                binding.ivAddCover.gone()
                binding.wtWhatYouMind.gone()
                followState = user.followed
                btFollow.subscribeOnClick {
                    handlerFollow(user.castcleId)
                }.addToDisposables()
                onBindStateFollowButton(user.followed)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupBlockedLayout(user: User) {
        with(binding) {
            profileYouBlocked.clMainContent.visible()
            profileYou.clMainContent.gone()
            wtWhatYouMind.gone()
            tabs.gone()
            layoutContent.gone()
            line.gone()
        }
        with(binding.profileYouBlocked) {
            tvProfileName.text = user.displayName
            tvProfileCastcleId.text = "@${user.castcleId}"
            ivAvatarProfile.loadCircleImage(user.avatar)

            btUnblock.subscribeOnClick {
                viewModel.unblockUser(user.castcleId).subscribeBy(
                    onComplete = {
                        setupUnBlockLayout(user)
                    },
                    onError = {
                        displayError(it)
                    }
                ).addToDisposables()
            }.addToDisposables()
        }
    }

    private fun getContentCount(count: Int): String {
        return if (count != 0) {
            count.toCount()
        } else {
            "0"
        }
    }

    private fun onBindStateFollowButton(follow: Boolean) {
        with(binding.profileYou) {
            if (follow) {
                btFollow.text = localizedResources.getString(R.string.profile_unfollow)
            } else {
                btFollow.text = localizedResources.getString(R.string.profile_follow)
            }
        }
    }

    private fun onBindWhatYouMind(user: User) {
        user.toContentUiModel().apply {
            deepLink = makeDeepLinkUrl(
                requireContext(), Input(
                    type = DeepLinkTarget.USER_PROFILE_ME,
                    contentData = user.castcleId
                )
            ).toString()
        }.run(binding.wtWhatYouMind::bindUiModel)
    }

    private fun getCreatePostBundle(): CreatePostBundle {
        return if (profileType == PROFILE_TYPE_ME) {
            CreatePostBundle.CreatePostProfileBundle(
                displayName = userProfile.displayName,
                castcleId = userProfile.castcleId,
                avaterUrl = userProfile.avatar,
                verifyed = userProfile.verified
            )
        } else {
            CreatePostBundle.CreatePostPageBundle(
                displayName = userProfile.displayName,
                castcleId = userProfile.castcleId,
                avaterUrl = userProfile.avatar,
                verifyed = userProfile.verified
            )
        }
    }

    private fun handlerWhatYouMindClick(it: TemplateClicks?) {
        if (it is TemplateClicks.CreatePostClick) {
            val createPostBundle = getCreatePostBundle()
            navigateToCreatePost(createPostBundle)
        }
    }

    private fun navigateToCreatePost(createPostBundle: CreatePostBundle) {
        onBoardNavigator.navigateToCreatePostFragment(createPostBundle, true)
    }

    private fun onBindEditImagePage() {
        binding.ivAddCover.visible()
        with(binding.profileYou) {
            btViewProfile.gone()
            ivAddAvatar.visible()
            btFollow.text = localizedResources.getString(R.string.profile_edit_page)

            btFollow.subscribeOnClick {
                onGuestMode(enable = {
                    onNavigateToEditProfileOrPage()
                }, disable = {})
            }.addToDisposables()

            ivAddAvatar.subscribeOnClick {
                onGuestMode(enable = {
                    onNavigateToDialogChooseFragment(
                        PhotoSelectedState.AVATAR_PAGE_SELECT
                    )
                }, disable = {})
            }.addToDisposables()
        }
    }

    private fun onNavigateToEditProfileOrPage() {
        val profileBundle = getProfileBundle()
        onBoardNavigator.navigateToAboutYouFragment(profileBundle)
    }

    private fun getProfileBundle(): ProfileBundle {
        profileType(onPage = {
            profileBundle = ProfileBundle.PageEdit(
                castcleId = userProfile.castcleId,
                overview = userProfile.overview,
                dob = userProfile.dob,
                facebookLinks = userProfile.facebookLinks,
                twitterLinks = userProfile.twitterLinks,
                youtubeLinks = userProfile.youtubeLinks,
                mediumLinks = userProfile.mediumLinks,
                websiteLinks = userProfile.websiteLinks
            )
        }, onProfileMe = {
            profileBundle = ProfileBundle.ProfileEdit(
                castcleId = userProfile.castcleId,
                overview = userProfile.overview,
                dob = userProfile.dob,
                facebookLinks = userProfile.facebookLinks,
                twitterLinks = userProfile.twitterLinks,
                youtubeLinks = userProfile.youtubeLinks,
                mediumLinks = userProfile.mediumLinks,
                websiteLinks = userProfile.websiteLinks
            )
        }, onProfileYou = {
            profileBundle = ProfileBundle.ViewProfile(
                castcleId = userProfile.castcleId,
                overview = userProfile.overview,
                dob = userProfile.dob,
                facebookLinks = userProfile.facebookLinks,
                twitterLinks = userProfile.twitterLinks,
                youtubeLinks = userProfile.youtubeLinks,
                mediumLinks = userProfile.mediumLinks,
                websiteLinks = userProfile.websiteLinks
            )
        })

        return profileBundle
    }

    private fun onNavigateToDialogChooseFragment(photoSelectedState: PhotoSelectedState) {
        this.photoSelectedState = photoSelectedState
        onBoardNavigator.navigateToDialogChooseFragment()
    }

    private fun handlerFollow(castcleId: String) {
        followState = !followState
        viewModel.putToFollowUser(castcleId).subscribeBy(
            onComplete = {
                onBindStateFollowButton(followState)
            }, onError = {
                handlerShowLoading(false)
                handlerOnShowMessageError(it)
            }
        ).addToDisposables()
    }

    private fun handlerOnShowMessageError(error: Throwable) {
        displayError(error)
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
            else -> {}
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
