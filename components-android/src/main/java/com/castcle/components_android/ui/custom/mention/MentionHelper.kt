package com.castcle.components_android.ui.custom.mention

import android.content.Context
import android.os.Build
import android.text.*
import android.text.method.MovementMethod
import android.text.style.*
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.util.PatternsCompat
import com.castcle.android.components_android.R
import com.castcle.extensions.getColorResource
import java.util.function.*
import java.util.regex.*


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
//  Created by sklim on 15/9/2021 AD at 08:20.

class MentionHelper(context: Context, textView: TextView, attrs: AttributeSet?) : MentionView {


    private var hashtagPattern: Pattern? = null


    private var mentionPattern: Pattern? = null


    private var hyperlinkPattern: Pattern? = null

    private var flags = 0


    private var hashtagColors: Int = 0


    private var mentionColors: Int = 0


    private var hyperlinkColors: Int = 0


    private var hashtagClickListener: MentionView.OnClickListener? = null


    private var mentionClickListener: MentionView.OnClickListener? = null


    private var hyperlinkClickListener: MentionView.OnClickListener? = null


    private var hashtagChangedListener: MentionView.OnChangedListener? = null


    private var mentionChangedListener: MentionView.OnChangedListener? = null
    private var hashtagEditing = false
    private var mentionEditing = false

    private lateinit var textView: TextView
    private var initialMovementMethod: MovementMethod? = null

    init {
        initMentionHelper(context, textView, attrs)
    }

    private val textWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (count > 0 && start > 0) {
                    val c = s[start - 1]
                    when (c) {
                        '#' -> {
                            hashtagEditing = true
                            mentionEditing = false
                        }
                        '@' -> {
                            hashtagEditing = false
                            mentionEditing = true
                        }
                        else -> if (!Character.isLetterOrDigit(c)) {
                            hashtagEditing = false
                            mentionEditing = false
                        } else if (hashtagChangedListener != null && hashtagEditing) {
                            hashtagChangedListener!!.onChanged(
                                this@MentionHelper, s.subSequence(
                                    indexOfPreviousNonLetterDigit(s, 0, start - 1) + 1, start
                                )
                            )
                        } else if (mentionChangedListener != null && mentionEditing) {
                            mentionChangedListener!!.onChanged(
                                this@MentionHelper, s.subSequence(
                                    indexOfPreviousNonLetterDigit(s, 0, start - 1) + 1, start
                                )
                            )
                        }
                    }
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // triggered when text is added
                if (s.isEmpty()) {
                    return
                }
                recolorize()
                if (start < s.length) {
                    val index = start + count - 1
                    if (index < 0) {
                        return
                    }
                    when (s[index]) {
                        '#' -> {
                            hashtagEditing = true
                            mentionEditing = false
                        }
                        '@' -> {
                            hashtagEditing = false
                            mentionEditing = true
                        }
                        else -> if (!Character.isLetterOrDigit(s[start])) {
                            hashtagEditing = false
                            mentionEditing = false
                        } else if (hashtagChangedListener != null && hashtagEditing) {
                            hashtagChangedListener!!.onChanged(
                                this@MentionHelper, s.subSequence(
                                    indexOfPreviousNonLetterDigit(s, 0, start) + 1, start + count
                                )
                            )
                        } else if (mentionChangedListener != null && mentionEditing) {
                            mentionChangedListener!!.onChanged(
                                this@MentionHelper, s.subSequence(
                                    indexOfPreviousNonLetterDigit(s, 0, start) + 1, start + count
                                )
                            )
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        }

    private fun initMentionHelper(context: Context, textView: TextView, attrs: AttributeSet?) {
        this.textView = textView
        initialMovementMethod = textView.movementMethod
        val styles = context.obtainStyledAttributes(attrs, R.styleable.MentionsInput)
        try {
            mentionColors = styles.getColor(
                R.styleable.MentionsInput_mentionTextView_mentionColor,
                context.getColorResource(R.color.blue)
            )
            hashtagColors = styles.getColor(
                R.styleable.MentionsInput_mentionTextView_hashtagColor,
                context.getColorResource(R.color.blue)
            )
            flags = styles.getInteger(
                R.styleable.MentionsInput_linkType,
                FLAG_HASHTAG or FLAG_MENTION or FLAG_HYPERLINK
            )
            textView.addTextChangedListener(textWatcher)
        } finally {
            styles.recycle()
        }
        recolorize()
    }

    override fun getHashtagPattern(): Pattern {
        return hashtagPattern ?: Pattern.compile("#(\\w+)")
    }

    override fun getMentionPattern(): Pattern {
        return mentionPattern ?: Pattern.compile("@(\\w+)")
    }

    override fun getHyperlinkPattern(): Pattern {
        return hyperlinkPattern ?: PatternsCompat.WEB_URL
    }

    override fun setHashtagPattern(pattern: Pattern) {
        hashtagPattern = pattern
    }

    override fun setMentionPattern(pattern: Pattern) {
        mentionPattern = pattern
    }

    override fun setHyperlinkPattern(pattern: Pattern) {
        hyperlinkPattern = pattern
    }

    override fun isHashtagEnabled(): Boolean {
        return flags or FLAG_HASHTAG == flags
    }

    override fun isMentionEnabled(): Boolean {
        return flags or FLAG_MENTION == flags
    }

    override fun isHyperlinkEnabled(): Boolean {
        return flags or FLAG_HYPERLINK == flags
    }

    override fun setHashtagEnabled(enabled: Boolean) {
        if (enabled && this.isHashtagEnabled()) {
            flags = if (enabled) flags or FLAG_HASHTAG else flags and FLAG_HASHTAG.inv()
            recolorize()
        }
    }

    override fun setMentionEnabled(enabled: Boolean) {
        if (enabled && this.isMentionEnabled()) {
            flags = if (enabled) flags or FLAG_MENTION else flags and FLAG_MENTION.inv()
            recolorize()
        }
    }

    override fun setHyperlinkEnabled(enabled: Boolean) {
        if (enabled && this.isMentionEnabled()) {
            flags = if (enabled) flags or FLAG_HYPERLINK else flags and FLAG_HYPERLINK.inv()
            recolorize()
        }
    }

    override fun getHashtagColors() = hashtagColors

    override fun getMentionColors() = mentionColors

    override fun getHyperlinkColors() = hyperlinkColors

    override fun setHashtagColors(colors: Int) {

    }

    override fun setMentionColors(colors: Int) {

    }

    override fun setHyperlinkColors(colors: Int) {

    }

    override fun setOnHashtagClickListener(listener: MentionView.OnClickListener) {
        hashtagClickListener = listener
    }

    override fun setOnMentionClickListener(listener: MentionView.OnClickListener) {
        mentionClickListener = listener
    }

    override fun setOnHyperlinkClickListener(listener: MentionView.OnClickListener) {
        hyperlinkClickListener = listener
    }

    override fun setHashtagTextChangedListener(listener: MentionView.OnChangedListener) {
        hashtagChangedListener = listener
    }

    override fun setMentionTextChangedListener(listener: MentionView.OnChangedListener) {
        mentionChangedListener = listener
    }

    override fun getHashtags(): List<String> {
        return listOf(textView.text.toString())
    }

    override fun getMentions(): List<String> {
        return listOf(textView.text.toString())
    }

    override fun getHyperlinks(): List<String> {
        return listOf(textView.text.toString())
    }

    private fun recolorize() {
        val text: CharSequence = textView.text
        check(text is Spannable) {
            "Attached text is not a Spannable," +
                "add TextView.BufferType.SPANNABLE when setting text to this TextView."
        }
        for (span in text.getSpans(0, text.length, CharacterStyle::class.java)) {
            text.removeSpan(span)
        }
        if (isHashtagEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                spanAll(text, getHashtagPattern()) {
                    if (hashtagClickListener != null) SocialClickableSpan(
                        hashtagClickListener!!,
                        hashtagColors,
                        false
                    ) else ForegroundColorSpan(hashtagColors)
                }
            }
        }
        if (isMentionEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                spanAll(text, getMentionPattern()) {
                    if (mentionClickListener != null) SocialClickableSpan(
                        mentionClickListener!!,
                        mentionColors,
                        false
                    ) else ForegroundColorSpan(mentionColors)
                }
            }
        }
        if (isHyperlinkEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                spanAll(text, getHyperlinkPattern()) {
                    if (hyperlinkClickListener != null) {
                        SocialClickableSpan(
                            hyperlinkClickListener!!,
                            hyperlinkColors,
                            true
                        )
                    } else {
                        SocialURLSpan(text, hyperlinkColors)
                    }
                }
            }
        }
    }

    override fun setMentionSelected(message: String) {
        recolorize()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun spanAll(
        spannable: Spannable,
        pattern: Pattern,
        styleSupplier: Supplier<CharacterStyle>
    ) {
        val matcher = pattern.matcher(spannable)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val span: Any = styleSupplier.get()
            spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (span is SocialClickableSpan) {
                span.text = spannable.subSequence(start, end)
            }
        }
    }


    private fun indexOfNextNonLetterDigit(text: CharSequence, start: Int): Int {
        for (i in start + 1 until text.length) {
            if (!Character.isLetterOrDigit(text[i])) {
                return i
            }
        }
        return text.length
    }

    private fun indexOfPreviousNonLetterDigit(text: CharSequence, start: Int, end: Int): Int {
        for (i in end downTo start + 1) {
            if (!Character.isLetterOrDigit(text[i])) {
                return i
            }
        }
        return start
    }

    class SocialClickableSpan(
        private val listener: MentionView.OnClickListener,
        colors: Int,
        private val isHyperlink: Boolean
    ) : ClickableSpan() {
        private val color: Int = colors
        var text: CharSequence = ""

        override fun onClick(widget: View) {
            check(widget is MentionView) { "Clicked widget is not an instance of SocialView." }
            listener.onClick(
                widget as MentionView,
                if (!isHyperlink) {
                    text.subSequence(1, text.length)
                } else {
                    text
                }
            )
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = color
            ds.isUnderlineText = isHyperlink
        }
    }

    private class SocialURLSpan(url: CharSequence, colors: Int) :
        URLSpan(url.toString()) {
        private val color: Int = colors
        override fun updateDrawState(ds: TextPaint) {
            ds.color = color
            ds.isUnderlineText = true
        }

    }
}

private const val FLAG_HASHTAG = 1
private const val FLAG_MENTION = 2
private const val FLAG_HYPERLINK = 4