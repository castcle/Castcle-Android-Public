package com.castcle.ui.createbloc.adapter

import android.annotation.SuppressLint
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.databinding.ItemImageGalleryBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.createblog.MediaItem
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.extensions.*
import com.castcle.ui.common.events.*
import kotlinx.android.extensions.LayoutContainer

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
//  Created by sklim on 16/9/2021 AD at 10:54.

class ImageGalleryAdapter : RecyclerView.Adapter<ImageGalleryAdapter.FilterViewHolder>(),
    ItemClickable<ImageItemClick> by ItemClickableImpl(), DiffUpdateAdapter {

    private var selectedPosition = 0

    var items: List<MediaItem> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val viewBinding = ItemImageGalleryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FilterViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.onBindImage(items[position])
    }

    fun selectedFilter(itemImage: MediaItem) {
        val adapterPosition = items.indexOf(itemImage)
        notifyItemChanged(selectedPosition)
        notifyItemChanged(adapterPosition)
    }

    override fun getItemCount(): Int = items.size

    inner class FilterViewHolder(val binding: ItemImageGalleryBinding) :
        RecyclerView.ViewHolder(binding.root), LayoutContainer {

        override val containerView: View
            get() = itemView

        fun onBindImage(item: MediaItem) {
            when (item) {
                is MediaItem.OpenCamera -> item.run(::bindOpenCamera)
                is MediaItem.ImageMediaItem -> item.run(::bindOpenImage)
                else -> {}
            }
            bindOnClick(item)
        }

        private fun bindOnClick(itemImage: MediaItem) {
            itemView.subscribeOnClick {
                notifyItemClick(
                    ImageItemClick.AddImageClick(
                        bindingAdapterPosition,
                        itemImage
                    )
                )
            }
        }

        private fun bindOpenImage(mediaItemCamera: MediaItem.ImageMediaItem) {
            binding.clBackground.isActivated = mediaItemCamera.isSelected
            binding.ivImage.visible()
            binding.ivImageAction.gone()
            binding.ivImage.loadRoundedCornersImageUri(mediaItemCamera.uri, mediaItemCamera.uri)
        }

        private fun bindOpenCamera(itemCamera: MediaItem.OpenCamera) {
            binding.ivImageAction.visible()
            binding.ivImage.invisible()
            binding.clBackground.isActivated = false
        }
    }
}
