package com.castcle.ui.createbloc.adapter

import android.annotation.SuppressLint
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.databinding.ItemImagePreviewBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.createblog.MediaItem
import com.castcle.components_android.ui.CORNER
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.extensions.loadGranularRoundedCornersContentImage
import com.castcle.extensions.loadRoundedCornersImageUri
import com.castcle.ui.common.events.*
import com.google.android.flexbox.AlignSelf
import com.google.android.flexbox.FlexboxLayoutManager
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
//  Created by sklim on 16/9/2021 AD at 13:41.

class ImageFloxBoxAdapter : RecyclerView.Adapter<ImageFloxBoxAdapter.FilterViewHolder>(),
    ItemClickable<ImageItemClick> by ItemClickableImpl(), DiffUpdateAdapter {

    private var selectedPosition = 0

    var items: List<MediaItem> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val viewBinding = ItemImagePreviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FilterViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.onBindImage(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class FilterViewHolder(val binding: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root), LayoutContainer {

        override val containerView: View
            get() = itemView

        fun onBindImage(item: MediaItem) {
            when (item) {
                is MediaItem.ImageMediaItem -> item.run(::bindOpenImage)
                else -> {
                }
            }
            bindOnClick(item)
        }

        private fun bindOnClick(itemImage: MediaItem) {
            binding.ivCancelImage.subscribeOnClick {
                notifyItemClick(
                    ImageItemClick.RemoveImageClick(
                        bindingAdapterPosition,
                        itemImage
                    )
                )
            }
        }

        @SuppressLint("WrongConstant")
        private fun bindOpenImage(mediaItemCamera: MediaItem.ImageMediaItem) {
            with(binding.ivImage) {
                when (items.size) {
                    1 -> {
                        loadRoundedCornersImageUri(mediaItemCamera.uri)
                    }
                    2 -> {
                        when (bindingAdapterPosition) {
                            0 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    topLeft = CORNER,
                                    bottomLeft = CORNER
                                )
                            }
                            1 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    topRight = CORNER,
                                    bottomRight = CORNER
                                )
                            }
                        }
                    }
                    3 -> {
                        when (bindingAdapterPosition) {
                            0 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    topLeft = CORNER
                                )
                            }
                            1 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    topRight = CORNER
                                )
                            }
                            2 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    bottomLeft = CORNER,
                                    bottomRight = CORNER
                                )
                            }
                        }
                    }
                    4 -> {
                        when (bindingAdapterPosition) {
                            0 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    topLeft = CORNER,
                                )
                            }
                            1 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    topRight = CORNER,
                                )
                            }
                            2 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    bottomLeft = CORNER
                                )
                            }
                            3 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera.uri,
                                    bottomRight = CORNER
                                )
                            }
                        }
                    }
                }
            }

            val lp = binding.root.layoutParams

            if (lp is FlexboxLayoutManager.LayoutParams) {
                with(lp) {
                    flexBasisPercent = if (items.size >= 2) {
                        0.4f
                    } else {
                        100f
                    }
                    flexGrow = 1f
                    alignSelf = AlignSelf.FLEX_END
                }
            }
        }
    }
}