package com.castcle.components_android.ui.adapter

import android.annotation.SuppressLint
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.components_android.databinding.ItemImageTemplatePreviewBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.components_android.ui.CORNER
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.extensions.loadGranularRoundedCornersContentImage
import com.castcle.extensions.loadRoundedCornersImageUri
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

class ImageTemplateFloxBoxAdapter :
    RecyclerView.Adapter<ImageTemplateFloxBoxAdapter.FilterViewHolder>(),
    DiffUpdateAdapter {

    private var selectedPosition = 0

    var items: List<String> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val viewBinding = ItemImageTemplatePreviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FilterViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.onBindImage(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class FilterViewHolder(val binding: ItemImageTemplatePreviewBinding) :
        RecyclerView.ViewHolder(binding.root), LayoutContainer {

        override val containerView: View
            get() = itemView

        fun onBindImage(item: String) {
            item.run(::bindOpenImage)
            bindOnClick()
        }

        private fun bindOnClick() {
            binding.ivCancelImage.subscribeOnClick {
            }
        }

        @SuppressLint("WrongConstant")
        private fun bindOpenImage(mediaItemCamera: String) {
            with(binding.ivImage) {
                when (items.size) {
                    1 -> {
                        loadRoundedCornersImageUri(mediaItemCamera)
                    }
                    2 -> {
                        when (adapterPosition) {
                            0 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
                                    topLeft = CORNER,
                                    bottomLeft = CORNER
                                )
                            }
                            1 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
                                    topRight = CORNER,
                                    bottomRight = CORNER
                                )
                            }
                        }
                    }
                    3 -> {
                        when (adapterPosition) {
                            0 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
                                    topLeft = CORNER
                                )
                            }
                            1 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
                                    topRight = CORNER
                                )
                            }
                            2 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
                                    bottomLeft = CORNER,
                                    bottomRight = CORNER
                                )
                            }
                        }
                    }
                    4 -> {
                        when (adapterPosition) {
                            0 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
                                    topLeft = CORNER,
                                )
                            }
                            1 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
                                    topRight = CORNER,
                                )
                            }
                            2 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
                                    bottomLeft = CORNER
                                )
                            }
                            3 -> {
                                loadGranularRoundedCornersContentImage(
                                    mediaItemCamera,
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