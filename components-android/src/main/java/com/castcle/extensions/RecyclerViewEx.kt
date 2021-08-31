package com.castcle.extensions

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.*
import com.castcle.android.components_android.R
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

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
//  Created by sklim on 30/8/2021 AD at 21:11.

fun RecyclerView.setupHorizontalSnapCarousel(
    visibleItemCount: Int? = null,
    @DimenRes spacing: Int = R.dimen.default_item_space
) {
    val defaultSpacing = resources.getDimensionPixelSize(spacing)
    val defaultPadding = resources.getDimensionPixelSize(R.dimen.default_padding_margin_medium)

    val snapHelper = GravitySnapHelper(Gravity.START).apply {
        snapToPadding = true
        maxFlingSizeFraction = SCROLL_STEP
        scrollMsPerInch = SCROLL_SPEED
    }

    val layoutManager = if (visibleItemCount == null) {
        LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    } else {
        CarouselLayoutManager(
            context,
            displayedItems = visibleItemCount,
            margin = defaultPadding,
            itemSpacing = defaultSpacing
        )
    }
    setupSnapCarousel(defaultSpacing, defaultPadding, layoutManager, snapHelper)
}

fun RecyclerView.setupSnapCarousel(
    itemSpacing: Int,
    padding: Int,
    layout: RecyclerView.LayoutManager,
    snapHelper: SnapHelper
) {
    val halfSpacing = itemSpacing / 2
    val startEndPadding = padding - halfSpacing
    setPaddingRelative(startEndPadding, paddingTop, startEndPadding, paddingBottom)

    snapHelper.attachToRecyclerView(this)

    VerticalSpaceItemDecoration(itemSpacing, halfSpacing).also {
        layoutManager = layout
        addItemDecoration(it)
    }
}

class VerticalSpaceItemDecoration(
    private val itemSpacing: Int = 0,
    private val startEndPadding: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val halfSpace = itemSpacing / 2
        val index = parent.getChildAdapterPosition(view)

        if (index == 0) {
            // First item
            outRect.left = startEndPadding
        } else {
            outRect.left = halfSpace
        }

        if (index == state.itemCount - 1) {
            // Last item
            outRect.right = startEndPadding
        } else {
            outRect.right = halfSpace
        }
    }
}

class CarouselLayoutManager(
    private val context: Context,
    private val displayedItems: Int,
    private val margin: Int,
    private val itemSpacing: Int = 0
) : LinearLayoutManager(context, HORIZONTAL, false) {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return scaledLayoutParams(super.generateDefaultLayoutParams())
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams {
        return scaledLayoutParams(super.generateLayoutParams(lp))
    }

    override fun generateLayoutParams(
        context: Context?,
        attrs: AttributeSet?
    ): RecyclerView.LayoutParams {
        return scaledLayoutParams(super.generateLayoutParams(context, attrs))
    }

    private fun scaledLayoutParams(layoutParams: RecyclerView.LayoutParams): RecyclerView.LayoutParams {
        return layoutParams.apply {
            val screenWidth = context.screenWidth
            width = (screenWidth - 2 * margin - (displayedItems - 1) * itemSpacing) / displayedItems
        }
    }
}

private const val SCROLL_STEP = 1F
private const val SCROLL_SPEED = 50F
