package com.castcle.components_android.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.text.*
import android.util.AttributeSet
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.InputEditTextBinding
import com.castcle.extensions.*

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
//  Created by sklim on 1/9/2021 AD at 14:25.

class InputEditText(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    val binding: InputEditTextBinding by lazy {
        InputEditTextBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        init(context, attrs)
    }

    var primaryText: String
        get() = binding.etTextInputPrimary.text.toString()
        set(value) {
            binding.etTextInputPrimary.setText(value)
        }

    private var defaultBackgroundRes: Int = 0
    private lateinit var textWatcher: TextWatcher
    var onTextChanged: ((String) -> Unit)? = null
    var onDrawableEndClickListener: ((String) -> Unit)? = null
    var onEditorActionListener: ((actionId: Int, event: KeyEvent?) -> Boolean)? = null
    private var inputType: Int = android.text.InputType.TYPE_CLASS_TEXT

    private fun init(context: Context, attrs: AttributeSet?) {

        View.inflate(context, R.layout.input_edit_text, this)
        val styles = context.obtainStyledAttributes(attrs, R.styleable.TextInput)
        try {
            defaultBackgroundRes =
                context.getDrawableAttribute(R.attr.castcleInputBackground)

            val backgroundRes =
                styles.getResourceId(R.styleable.TextInput_background, defaultBackgroundRes)
            with(binding) {
                clTextInputLayout.setBackgroundResource(backgroundRes)
                tilTextInputLayout.hint = styles.getString(R.styleable.TextInput_hintText)
                setupHintTextEnd(styles)
                inputType = InputType.from(
                    styles.getInt(
                        R.styleable.TextInput_inputType, 0
                    )
                ).get()
                etTextInputPrimary.inputType = inputType
                etTextInputPrimary.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(
                        styles.getInteger(
                            R.styleable.TextInput_maxLength,
                            Integer.MAX_VALUE
                        )
                    )
                )
                etTextInputPrimary.imeOptions = styles.getInt(
                    R.styleable.TextInput_imeOptions, 0
                )
                setupDrawableEnd(styles)

                etTextInputPrimary.apply {
                    setTextStyle(R.style.TextInput_Text)
                    movePrimaryTextUp()

                    setOnFocusChangeListener { _, hasFocus ->
                        binding.root.isActivated = hasFocus
                        when {
                            hasFocus -> {
                                movePrimaryTextDown()
                                context.showSoftKeyboard(this)
                            }
                            text.toString().isEmpty() -> movePrimaryTextUp()
                        }
                    }
                    textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) = Unit

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) = Unit

                        override fun afterTextChanged(s: Editable?) {
                            when {
                                s.toString().isNotEmpty() -> movePrimaryTextDown()
                                !hasFocus() -> movePrimaryTextUp()
                            }

                            onTextChanged?.invoke(s.toString())
                        }
                    }
                    addTextChangedListener(textWatcher)

                    setOnEditorActionListener { view, actionId, keyEvent ->
                        onEditorActionListener?.invoke(actionId, keyEvent) ?: false
                    }

                    setOnLongClickListener {
                        requestFocus()
                        false
                    }
                }
            }
        } finally {
            styles.recycle()
        }
    }

    fun setError(error: String?, isShowErrorWithOutText: Boolean = false) {
        with(binding) {
            when {
                isShowErrorWithOutText -> {
                    binding.tilTextInputLayout.background =
                        context.getDrawableRes(R.drawable.bg_round_corner_input_error)
                }
                error.isNullOrEmpty() -> hideError()
                binding.tvTextInputError.isGone -> {
                    tilTextInputLayout.background =
                        context.getDrawableRes(R.drawable.bg_round_corner_input_error)
                    tvTextInputError.visible()
                    tvTextInputError.text = error
                }
            }
        }
    }

    private fun hideError() {
        if (binding.tvTextInputError.isVisible) {
            val backgroundResId = context.getDrawableAttribute(R.attr.castcleInputBackground)
            binding.tilTextInputLayout.background = context.getDrawableRes(backgroundResId)
            binding.tvTextInputError.gone()
        }
    }

    fun setMaxLength(length: Int) {
        binding.etTextInputPrimary.filters = arrayOf(InputFilter.LengthFilter(length))
    }

    private fun movePrimaryTextDown() {
        binding.etTextInputPrimary.setBottomPaddingRes(R.dimen._3sdp)
        binding.tilTextInputLayout.setTopPaddingRes(R.dimen._6sdp)
    }

    private fun movePrimaryTextUp() {
        binding.etTextInputPrimary.setBottomPaddingRes(R.dimen._12sdp)
        binding.tilTextInputLayout.setTopPadding(0)
    }

    private fun setupDrawableEnd(styles: TypedArray) {
        styles.getDrawable(R.styleable.TextInput_drawableEnd)?.let { drawableEnd ->
            with(binding.ibTextInputDrawableEnd) {
                visible()
                setImageDrawable(drawableEnd)
                setOnClickListener {
                    onDrawableEndClickListener?.invoke(primaryText)
                }
            }
            val params = binding.etTextInputPrimary.layoutParams as MarginLayoutParams
            params.marginEnd = resources.getDimensionPixelSize(R.dimen._25sdp)
        }
    }

    private fun setupHintTextEnd(styles: TypedArray) {
        styles.getString(R.styleable.TextInput_hintTextEnd)?.let { hintTextEnd ->
            with(binding.tvTextInputHintEnd) {
                visible()
                hint = hintTextEnd
            }
            val params = binding.etTextInputPrimary.layoutParams as MarginLayoutParams
            params.marginEnd = resources.getDimensionPixelSize(R.dimen._40sdp)
        }
    }
}