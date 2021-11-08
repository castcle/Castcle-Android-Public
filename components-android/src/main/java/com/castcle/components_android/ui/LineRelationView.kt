package com.castcle.components_android.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.castcle.android.components_android.R


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
//  Created by sklim on 4/11/2021 AD at 12:35.

@SuppressLint("Recycle", "CustomViewStyleable")
class LineRelationView : View {

    private val paint = Paint()

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        setup(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setup(attrs)
    }

    @Suppress("unused")
    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setup(attrs)
    }

    @SuppressLint("ResourceAsColor")
    @ColorInt
    private var lineColor = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }

    private var dotCount = 10
        set(value) {
            field = value
            invalidate()
        }

    private var isMirror = false
        set(value) {
            field = value
            invalidate()
        }

    private fun setup(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RelationLineView)
        lineColor = typedArray.getColor(R.styleable.RelationLineView_rlv_lineColor, Color.WHITE)
        setupCanvasComponent()
        typedArray.recycle()
    }

    private fun setupCanvasComponent() {
        paint.run {
            color = lineColor
            isAntiAlias = true
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 8f
            paint.isAntiAlias = true
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(lineColor)
        val offset = 50
        canvas?.drawLine(
            offset.toFloat(),
            (height / 2).toFloat(),
            (width - offset).toFloat(), (height / 2).toFloat(), paint
        )
    }
}