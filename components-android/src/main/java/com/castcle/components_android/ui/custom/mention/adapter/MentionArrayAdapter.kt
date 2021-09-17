package com.castcle.components_android.ui.custom.mention.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Filter
import com.castcle.android.components_android.databinding.ItemMentionUserBinding
import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.extensions.loadCircleImage
import java.util.*
import kotlin.collections.ArrayList


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
//  Created by sklim on 15/9/2021 AD at 11:12.

class MentionArrayAdapter(
    context: Context,
    var items: List<ContentUiModel>
) : ArrayAdapter<ContentUiModel>(context, 0, items) {

    var filtered = ArrayList<ContentUiModel>()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewBinding = ItemMentionUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        if (filtered.size > 0) {
            onBindHolder(filtered[position], viewBinding)
        }
        return viewBinding.root
    }

    override fun getItem(position: Int): ContentUiModel {
        return filtered[position]
    }

    override fun getCount(): Int {
        return filtered.size
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewBinding = ItemMentionUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, true
        )
        if (filtered.size > 0) {
            onBindHolder(filtered[position], viewBinding)
        }
        return viewBinding.root
    }

    override fun addAll(collection: MutableCollection<out ContentUiModel>) {
        super.addAll(collection)
    }

    override fun getFilter(): Filter = filter

    private val filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint != null && constraint.isNotEmpty()) {
                val resultFilter = autocomplete(constraint.toString())
                results.apply {
                    values = resultFilter
                    count = constraint.length
                }
            }
            return results
        }

        private fun autocomplete(input: String): ArrayList<ContentUiModel> {
            val results = arrayListOf<ContentUiModel>()

            for (item in items) {
                if (item.payLoadUiModel.author.displayName
                        .toLowerCase(Locale.ROOT).contains(input)
                ) {
                    results.add(item)
                }
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.count!! > 0) {
                filtered = results.values as ArrayList<ContentUiModel>
                notifyDataSetInvalidated()
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as ContentUiModel).payLoadUiModel.author.displayName
        }

    }

    private fun onBindHolder(
        itemMention: ContentUiModel,
        viewBinding: ItemMentionUserBinding
    ) {
        with(viewBinding) {
            ivAvatar.loadCircleImage(itemMention.payLoadUiModel.author.avatar)
            tvFilterName.text = itemMention.payLoadUiModel.author.displayName
        }
    }
}