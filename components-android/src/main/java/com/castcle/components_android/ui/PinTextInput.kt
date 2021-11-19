package com.castcle.components_android.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.castcle.android.components_android.R
import com.castcle.components_android.ui.custom.InputType.NUMBER_PASSWORD
import com.castcle.extensions.getColorResource

// Adaptation of https://github.com/alphamu/PinEntryEditText
class PinTextInput : AppCompatEditText {

    var onPinEnteredListener: ((String) -> Unit)? = null
    var onPinChangedListener: ((CharSequence) -> Unit)? = null

    private lateinit var characterPaint: Paint
    private lateinit var characterPaintLast: Paint

    private var masked: Boolean = DEFAULT_MASKED
    private var characterMasked = DEFAULT_CHARACTER_MASKED
    var characterAmount = DEFAULT_CHARACTER_AMOUNT
        private set

    var defaultCharacterColor = context.getColorResource(R.color.white)

    private var characterTextPaddingBottom = resources.getDimensionPixelSize(
        DEFAULT_CHARACTER_TEXT_PADDING_BOTTOM
    )

    private var characterBoxHorizontalSpacing = resources.getDimensionPixelSize(
        DEFAULT_CHARACTER_BOX_HORIZONTAL_SPACING
    )

    private var defaultCharacterBoxBackground = ResourcesCompat.getDrawable(
        resources, DEFAULT_CHARACTER_BOX_BACKGROUND, null
    )

    private var activeCharacterBoxBackground = ResourcesCompat.getDrawable(
        resources, ACTIVE_CHARACTER_BOX_BACKGROUND, null
    )

    private var errorCharacterBoxBackground = ResourcesCompat.getDrawable(
        resources, ERROR_CHARACTER_BOX_BACKGROUND, null
    )

    private var characterBoxBackground = defaultCharacterBoxBackground

    private var characterCoordinates = arrayListOf<RectF>()
    private var characterBoxSize = 0F

    private val textMasked: CharSequence
        get() = StringBuilder().apply {
            while (length != textLength) {
                if (length < textLength) {
                    val char = if (masked) characterMasked else text.toString()[length]
                    append(char)
                } else {
                    deleteCharAt(length - 1)
                }
            }
        }

    private val textLength: Int
        get() = text.toString().length

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet?) {
        setBackgroundResource(0)
        setTextColor()
        inputType = NUMBER_PASSWORD.get()
        isCursorVisible = false
        isLongClickable = false

        val styles = context.obtainStyledAttributes(attrs, R.styleable.PinTextInput)
        try {
            masked = styles.getBoolean(
                R.styleable.PinTextInput_pinMasked,
                DEFAULT_MASKED
            )

            characterAmount = styles.getInteger(
                R.styleable.PinTextInput_pinCharacterAmount,
                DEFAULT_CHARACTER_AMOUNT
            )
            filters += InputFilter.LengthFilter(characterAmount)

            characterMasked = styles.getString(
                R.styleable.PinTextInput_pinCharacterMasked
            ) ?: DEFAULT_CHARACTER_MASKED

            characterTextPaddingBottom = styles.getDimensionPixelOffset(
                R.styleable.PinTextInput_pinTextPaddingBottom,
                characterTextPaddingBottom
            )

            characterBoxHorizontalSpacing = styles.getDimensionPixelSize(
                R.styleable.PinTextInput_pinCharacterBoxHorizontalSpacing,
                characterBoxHorizontalSpacing
            )

            defaultCharacterBoxBackground = styles.getDrawable(
                R.styleable.PinTextInput_pinCharacterBoxBackground
            ) ?: defaultCharacterBoxBackground

            errorCharacterBoxBackground = styles.getDrawable(
                R.styleable.PinTextInput_pinErrorCharacterBoxBackground
            ) ?: errorCharacterBoxBackground

            activeCharacterBoxBackground = styles.getDrawable(
                R.styleable.PinTextInput_pinActiveCharacterBoxBackground
            ) ?: activeCharacterBoxBackground

            defaultCharacterColor = styles.getColor(
                R.styleable.PinTextInput_characterColor,
                defaultCharacterColor
            )
            characterBoxBackground = defaultCharacterBoxBackground
        } finally {
            styles.recycle()
        }
    }

    private fun setTextColor() {
        characterPaint = Paint(paint).apply {
            color = defaultCharacterColor
        }
        characterPaintLast = Paint(paint).apply {
            color = defaultCharacterColor
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val availableWidth = width - paddingEnd - paddingStart
        characterBoxSize = ((availableWidth - characterBoxHorizontalSpacing
            * (characterAmount - 1)) / characterAmount).toFloat()

        characterCoordinates.clear()
        var start = paddingStart
        val bottom = height - paddingBottom
        var i = 0
        while (i < characterAmount) {
            val bounds = RectF(
                start.toFloat(), 0F, start + characterBoxSize, bottom.toFloat()
            )
            characterCoordinates.add(bounds)
            start += (characterBoxSize + characterBoxHorizontalSpacing).toInt()
            i++
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val measuredWidth: Int
        val measuredHeight: Int
        when {
            widthMode == MeasureSpec.EXACTLY -> {
                measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
                measuredHeight = (measuredWidth -
                    (characterAmount - 1 * characterBoxHorizontalSpacing)) / characterAmount
            }
            heightMode == MeasureSpec.EXACTLY -> {
                measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
                measuredWidth = measuredHeight * characterAmount +
                    (characterBoxHorizontalSpacing * characterAmount - 1)
            }
            widthMode == MeasureSpec.AT_MOST -> {
                measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
                measuredHeight = (measuredWidth -
                    (characterAmount - 1 * characterBoxHorizontalSpacing)) / characterAmount
            }
            heightMode == MeasureSpec.AT_MOST -> {
                measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
                measuredWidth = measuredHeight * characterAmount +
                    (characterBoxHorizontalSpacing * characterAmount - 1)
            }
            else -> {
                measuredWidth = paddingLeft + paddingRight + suggestedMinimumWidth
                measuredHeight = measuredWidth -
                    (characterAmount - 1 * characterBoxHorizontalSpacing) / characterAmount
            }
        }
        setMeasuredDimension(
            View.resolveSizeAndState(measuredWidth, widthMeasureSpec, 1),
            View.resolveSizeAndState(measuredHeight, heightMeasureSpec, 0)
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val textWidths = FloatArray(textMasked.length)
        paint.getTextWidths(textMasked, 0, textLength, textWidths)

        var i = 0
        while (i < characterAmount) {
            val bounds = characterCoordinates[i]
            bounds.run {
                val boxBackground =
                    if (i == text.toString().length) {
                        activeCharacterBoxBackground
                    } else {
                        characterBoxBackground
                    }
                boxBackground?.setBounds(
                    left.toInt(),
                    top.toInt(),
                    right.toInt(),
                    bottom.toInt()
                )
                boxBackground?.draw(canvas)
                if (textLength > i) {
                    val middle = left + characterBoxSize / 2
                    val xTextCoordinate = middle - textWidths[i] / 2
                    val yTextCoordinate = bottom - characterTextPaddingBottom
                    val paint = if (i != textLength - 1) characterPaint else characterPaintLast
                    canvas.drawText(textMasked, i, i + 1, xTextCoordinate, yTextCoordinate, paint)
                }
            }
            i++
        }
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        onPinChangedListener?.invoke(text)
        if (lengthAfter > lengthBefore) {
            animatePopIn()
        }
        if (text.length == characterAmount) {
            onPinEnteredListener?.invoke(text.toString())
        }
    }

    private fun animatePopIn() {
        val animator = ValueAnimator.ofFloat(ANIMATION_VALUE, paint.textSize)
        animator.duration = ANIMATION_DURATION
        animator.interpolator = OvershootInterpolator()
        animator.addUpdateListener { animation ->
            characterPaintLast.textSize = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    fun enableMasking(isEnabled: Boolean) {
        masked = isEnabled
        invalidate()
    }

    fun setError(hasError: Boolean) {
        characterBoxBackground = if (hasError) {
            errorCharacterBoxBackground
        } else {
            defaultCharacterBoxBackground
        }
        invalidate()
    }
}

const val DEFAULT_CHARACTER_AMOUNT = 6

private const val DEFAULT_MASKED = true
private const val DEFAULT_CHARACTER_MASKED = "•"
private var DEFAULT_CHARACTER_TEXT_PADDING_BOTTOM = R.dimen._16sdp
private var DEFAULT_CHARACTER_BOX_HORIZONTAL_SPACING = R.dimen._6sdp
private const val ANIMATION_DURATION = 200L
private const val ANIMATION_VALUE = 1F
private val DEFAULT_CHARACTER_BOX_BACKGROUND = R.drawable.bg_pin_default_character_box
private val ERROR_CHARACTER_BOX_BACKGROUND = R.drawable.bg_pin_default_character_box_error
private val ACTIVE_CHARACTER_BOX_BACKGROUND = R.drawable.bg_pin_default_character_box
