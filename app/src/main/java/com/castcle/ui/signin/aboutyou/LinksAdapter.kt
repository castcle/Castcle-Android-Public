package com.castcle.ui.signin.aboutyou

import android.annotation.SuppressLint
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.castcle.android.databinding.ItemLinksBinding
import com.castcle.components_android.ui.base.DiffUpdateAdapter
import com.castcle.ui.common.events.ItemClickable
import com.castcle.ui.common.events.ItemClickableImpl
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
//  Created by sklim on 18/10/2021 AD at 15:20.

class LinksAdapter : RecyclerView.Adapter<LinksAdapter.ItemLinkViewHolder>(),
    ItemClickable<String> by ItemClickableImpl(), DiffUpdateAdapter {

    private var selectedPosition = 0

    var items: List<String> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemLinkViewHolder {
        val viewBinding = ItemLinksBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemLinkViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ItemLinkViewHolder, position: Int) {
        holder.onBindItemLinks(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemLinkViewHolder(val binding: ItemLinksBinding) :
        RecyclerView.ViewHolder(binding.root), LayoutContainer {

        override val containerView: View
            get() = itemView

        fun onBindItemLinks(itemLinks: String) {
            binding.itInputLink.run {
                setHintText(itemLinks)
                primaryText = itemLinks
            }
            bindOnClick(itemLinks)
        }

        private fun bindOnClick(itemLinks: String) {
            itemView.setOnClickListener {
                if (selectedPosition != bindingAdapterPosition) {
                    notifyItemClick(itemLinks)
                }
            }
        }
    }
}
