package com.castcle.ui.profile

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.databinding.FragmentCropAvatarBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import pl.aprilapps.easyphotopicker.MediaFile
import java.io.*
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
//  Created by sklim on 20/10/2021 AD at 13:32.

class CropAvatarImageFragment : BaseFragment<CropAvatarImageFragmentViewModel>(),
    BaseFragmentCallbacks,
    ViewBindingInflater<FragmentCropAvatarBinding>,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var localizedResources: LocalizedResources

    private val cropAvatarImageFragment: CropAvatarImageFragmentArgs by navArgs()

    private val mediaFile: MediaFile
        get() = cropAvatarImageFragment.mediaFile

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCropAvatarBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCropAvatarBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCropAvatarBinding
        get() = viewBinding as FragmentCropAvatarBinding

    override fun viewModel(): CropAvatarImageFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(CropAvatarImageFragmentViewModel::class.java)

    override fun initViewModel() {
    }

    override fun setupView() {
        setupToolBar()
        handlerCropImageAvatar(mediaFile)
    }

    private fun handlerCropImageAvatar(media: MediaFile) {
        val contentUri: Uri = Uri.fromFile(media.file)
        with(binding) {
            cvCropImage.setImageURI(contentUri)
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.text =
                localizedResources.getString(com.castcle.android.R.string.tool_bar_apply)
            tvToolbarTitleAction.subscribeOnClick {
                applyImageProfile()
            }
            tvToolbarTitle.gone()
            ivToolbarLogoButton
                .subscribeOnClick {
                    findNavController().navigateUp()
                }.addToDisposables()
        }
    }

    private fun applyImageProfile() {
        val image = binding.cvCropImage.croppedBitmap
        saveToStorage(image).run {

            val file = File(context?.cacheDir, "avatar_crop.jpg")
            if (!file.exists()) {
                val inputStream = requireContext().assets.open("avatar_crop.jpg")
                copyInputStreamToFile(inputStream, file)
            }
            setNavigationResult(onBoardNavigator, KEY_CROP_IMAGE_REQUEST, this)
        }
        onBoardNavigator.findNavController().navigateUp()
    }

    private fun saveToStorage(bitmap: Bitmap): File {
        val fileName = "avatar_crop.jpg"
        val file = File(requireContext().filesDir, fileName)
        file.createNewFile()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_QUALITY, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(data)
        fileOutputStream.flush()
        fileOutputStream.close()

        return file
    }


    override fun bindViewEvents() {
    }

    override fun bindViewModel() {
    }
}

const val KEY_CROP_IMAGE_REQUEST: String = "CROP-001"
const val DEFAULT_QUALITY: Int = 100
