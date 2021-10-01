package com.castcle.components_android.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.os.Parcelable
import android.text.*
import android.util.AttributeSet
import android.util.SparseArray
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.castcle.android.components_android.R
import com.castcle.android.components_android.databinding.InputEditTextBinding
import com.castcle.extensions.*
import com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE

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

    var isEditable: Boolean
        set(isEditable) {
            if (isEditable) {
                binding.etTextInputPrimary.editable(true, inputType)
            } else {
                binding.etTextInputPrimary.editable(false)
            }
        }
        get() = binding.etTextInputPrimary.isEditable()

    private var defaultBackgroundRes: Int = 0
    private lateinit var textWatcher: TextWatcher
    var onTextChanged: ((String) -> Unit)? = null
    var onDrawableEndClickListener: ((String) -> Unit)? = null
    var onEditorActionListener: ((actionId: Int, event: KeyEvent?) -> Boolean)? = null
    private var inputType: Int = android.text.InputType.TYPE_CLASS_TEXT
    private var endableTransform = false

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
                if (inputType == InputType.TEXT_PASSWORD.get()) {
                    tilTextInputLayout.endIconMode = END_ICON_PASSWORD_TOGGLE
                }
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
                setupDrawableStart(styles)
                isEditable = styles.getBoolean(R.styleable.TextInput_editable, true)
            }
        } finally {
            styles.recycle()
        }
        with(binding) {
            etTextInputPrimary.apply {
                setTextStyle(R.style.TextInput_Text)

                setOnFocusChangeListener { _, hasFocus ->
                    binding.root.isActivated = hasFocus
                    when {
                        hasFocus -> {
                            context.showSoftKeyboard(this)
                        }
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
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(TEXT_INPUT_SUPER_STATE_KEY, super.onSaveInstanceState())
            putSparseParcelableArray(TEXT_INPUT_SPARSE_STATE_KEY, saveChildViewStates())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var newState = state
        if (newState is Bundle) {
            val childrenState =
                newState.getSparseParcelableArray<Parcelable>(TEXT_INPUT_SPARSE_STATE_KEY)
            childrenState?.let { restoreChildViewStates(it) }
            newState = newState.getParcelable(TEXT_INPUT_SUPER_STATE_KEY)
        }
        super.onRestoreInstanceState(newState)
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        dispatchThawSelfOnly(container)
    }

    fun showLoading(show: Boolean = false) {
        if (show) {
            binding.ibTextInputLoading.visible()
        } else {
            binding.ibTextInputLoading.gone()
        }
    }

    fun setError(
        error: String?,
        isShowErrorWithOutText: Boolean = false,
        isShowErrorWithBackground: Boolean = false
    ) {
        with(binding) {
            when {
                isShowErrorWithOutText -> {
                    binding.tilTextInputLayout.background =
                        context.getDrawableRes(R.drawable.bg_round_corner_input_error)
                }
                error.isNullOrEmpty() -> hideError()
                isShowErrorWithBackground -> {
                    tvTextInputError.visible()
                    tvTextInputError.text = error
                }
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
        binding.tilTextInputLayout.setTopPaddingRes(R.dimen._4sdp)
    }

    private fun movePrimaryTextUp() {
        binding.etTextInputPrimary.setBottomPaddingRes(R.dimen._9sdp)
        binding.tilTextInputLayout.setTopPadding(0)
    }

    fun clearDrawableEnd() {
        binding.ibTextInputDrawableEnd.gone()
    }

    fun clearOnPasswordDrawableEnd() {
        if (binding.ibTextInputDrawableEnd.isVisible) {
            binding.ibTextInputDrawableEnd.gone()
            binding.tilTextInputLayout.isEndIconVisible = true
        }
    }

    fun onSetupStatusFaildDrawableEnd() {
        if (binding.tilTextInputLayout.isEndIconVisible) {
            binding.tilTextInputLayout.isEndIconVisible = false
        }
        binding.etTextInputPrimary.setTextColor(context.getColorResource(R.color.red_primary))
        with(binding.ibTextInputDrawableEnd) {
            visible()
            setOnClickListener {
                onDrawableEndClickListener?.invoke(primaryText)
            }
            setImageDrawable(
                context?.getDrawableRes(R.drawable.ic_verify_faild)
            )
            val params = binding.ibTextInputDrawableEnd.layoutParams as MarginLayoutParams
            params.marginEnd = resources.getDimensionPixelSize(R.dimen._25sdp)
        }
    }

    fun onSetupStatusDrawableEnd() {
        if (!binding.tilTextInputLayout.isEndIconVisible) {
            binding.ibTextInputDrawableEnd.gone()
            binding.tilTextInputLayout.isEndIconVisible = true
        }
        val colorInputText = context.getColorResource(R.color.white)
        binding.etTextInputPrimary.setTextColor(colorInputText)
        val params = binding.etTextInputPrimary.layoutParams as MarginLayoutParams
        params.marginEnd = resources.getDimensionPixelSize(R.dimen._25sdp)
    }

    fun onSetupEndIconVerifyPass() {
        binding.tilTextInputLayout.isEndIconVisible = false
        with(binding.ibTextInputDrawableEnd) {
            visible()
            setOnClickListener {
                onDrawableEndClickListener?.invoke(primaryText)
            }
        }
        binding.ibTextInputDrawableEnd.setImageDrawable(
            context?.getDrawableRes(R.drawable.ic_password_visible)
        )
    }

    fun onSetupStatusVerifyEmailPass() {
        with(binding.ibTextInputDrawableEnd) {
            visible()
            setImageDrawable(
                context?.getDrawableRes(R.drawable.ic_verify_pass)
            )
        }
        val colorInputText = context.getColorResource(R.color.white)
        binding.etTextInputPrimary.setTextColor(colorInputText)
    }

    fun setTransformationTextPassword() {
        with(binding) {
            etTextInputPrimary.setTransformationPassword(endableTransform)
            endableTransform = !endableTransform
        }
    }

    private fun setupDrawableStart(styles: TypedArray) {
        styles.getDrawable(R.styleable.TextInput_drawableStart)?.let { drawableEnd ->
            with(binding.ibTextInputDrawableStart) {
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

private const val TEXT_INPUT_SPARSE_STATE_KEY = "TEXT_INPUT_SPARSE_STATE_KEY"
private const val TEXT_INPUT_SUPER_STATE_KEY = "TEXT_INPUT_SUPER_STATE_KEY"
