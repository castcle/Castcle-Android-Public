package com.castcle.components_android.ui.custom.mention

import android.content.Context
import android.text.*
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
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
//  Created by sklim on 14/9/2021 AD at 14:54.

class MentionEditText(
    context: Context,
    attrs: AttributeSet
) : AppCompatMultiAutoCompleteTextView(context, attrs), MentionView {

    private lateinit var mentionHelper: MentionHelper

    init {
        initMentionEditText(context, attrs)
    }

    private var hashtagAdapter: ArrayAdapter<Any>? = null
    private var mentionAdapter: ArrayAdapter<*>? = null

    private val textWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s) && start < s.length) {
                    when (s[start]) {
                        '#' -> if (adapter != hashtagAdapter) {
//                        setAdapter(hashtagAdapter)
                        }
                        '@' -> if (adapter != mentionAdapter) {
                            setAdapter(mentionAdapter)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        }

    private fun initMentionEditText(context: Context, attrs: AttributeSet) {
        mentionHelper = MentionHelper(context, this, attrs)
        addTextChangedListener(textWatcher)
        setTokenizer(CharTokenizer())
    }

    fun setHashtagAdapter(adapter: ArrayAdapter<Any>) {
        hashtagAdapter = adapter
    }

    fun getHashtagAdapter(): ArrayAdapter<Any>? {
        return hashtagAdapter
    }

    fun setMentionAdapter(adapter: ArrayAdapter<*>) {
        mentionAdapter = adapter
    }

    fun getentionAdapter(): ArrayAdapter<*>? {
        return mentionAdapter
    }

    override fun getHashtagPattern(): Pattern {
        return mentionHelper.getHashtagPattern()
    }

    override fun getMentionPattern(): Pattern {
        return mentionHelper.getMentionPattern()
    }

    override fun getHyperlinkPattern(): Pattern {
        return mentionHelper.getHyperlinkPattern()
    }

    override fun setHashtagPattern(pattern: Pattern) {
        mentionHelper.setHashtagPattern(pattern)
    }

    override fun setMentionPattern(pattern: Pattern) {
        mentionHelper.setMentionPattern(pattern)
    }

    override fun setHyperlinkPattern(pattern: Pattern) {
        mentionHelper.setHyperlinkPattern(pattern)
    }

    override fun isHashtagEnabled(): Boolean {
        return mentionHelper.isHashtagEnabled()
    }

    override fun isMentionEnabled(): Boolean {
        return mentionHelper.isMentionEnabled()
    }

    override fun isHyperlinkEnabled(): Boolean {
        return mentionHelper.isHyperlinkEnabled()
    }

    override fun setHashtagEnabled(enabled: Boolean) {
        mentionHelper.setHashtagEnabled(enabled)
        setTokenizer(CharTokenizer())
    }

    override fun setMentionEnabled(enabled: Boolean) {
        mentionHelper.setMentionEnabled(enabled)
        setTokenizer(CharTokenizer())
    }

    override fun setHyperlinkEnabled(enabled: Boolean) {
        mentionHelper.setHashtagEnabled(enabled)
    }

    override fun getHashtagColors(): Int {
        return mentionHelper.getHashtagColors()
    }

    override fun getMentionColors(): Int {
        return mentionHelper.getMentionColors()
    }

    override fun getHyperlinkColors(): Int {
        return mentionHelper.getHyperlinkColors()
    }

    override fun setHashtagColors(colors: Int) {
        mentionHelper.setHashtagColors(colors)
    }

    override fun setMentionColors(colors: Int) {
        mentionHelper.setMentionColors(colors)
    }

    override fun setHyperlinkColors(colors: Int) {
        mentionHelper.setHyperlinkColors(colors)
    }

    override fun setOnHashtagClickListener(listener: MentionView.OnClickListener) {
        mentionHelper.setOnHashtagClickListener(listener)
    }

    override fun setOnMentionClickListener(listener: MentionView.OnClickListener) {
        mentionHelper.setOnMentionClickListener(listener)
    }

    override fun setOnHyperlinkClickListener(listener: MentionView.OnClickListener) {
        mentionHelper.setOnHyperlinkClickListener(listener)
    }

    override fun setHashtagTextChangedListener(listener: MentionView.OnChangedListener) {
        mentionHelper.setHashtagTextChangedListener(listener)
    }

    override fun setMentionTextChangedListener(listener: MentionView.OnChangedListener) {
        mentionHelper.setMentionTextChangedListener(listener)
    }

    override fun getHashtags(): List<String> {
        return mentionHelper.getHashtags()
    }

    override fun getMentions(): List<String> {
        return mentionHelper.getMentions()
    }

    override fun setMentionSelected(message: String) {
        mentionHelper.setMentionSelected(message)
    }

    override fun getHyperlinks(): List<String> {
        return mentionHelper.getHyperlinks()
    }

    inner class CharTokenizer : Tokenizer {
        private val chars: MutableCollection<Char> = ArrayList()
        override fun findTokenStart(text: CharSequence, cursor: Int): Int {
            var i = cursor
            while (i > 0 && !chars.contains(text[i - 1])) {
                i--
            }
            while (i < cursor && text[i] == ' ') {
                i++
            }

            // imperfect fix for dropdown still showing without symbol found
            if (i == 0 && isPopupShowing) {
                dismissDropDown()
            }
            return i
        }

        override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
            var i = cursor
            val len = text.length
            while (i < len) {
                when {
                    chars.contains(text[i]) -> {
                        return i
                    }
                    else -> {
                        i++
                    }
                }
            }
            return len
        }

        override fun terminateToken(text: CharSequence): CharSequence {
            var i = text.length
            while (i > 0 && text[i - 1] == ' ') {
                i--
            }
            return if (i > 0 && chars.contains(text[i - 1])) {
                text
            } else {
                when (text) {
                    is Spanned -> {
                        val sp: Spannable = SpannableString("$text ")
                        TextUtils.copySpansFrom(
                            text, 0, text.length,
                            Any::class.java, sp, 0
                        )
                        sp
                    }
                    else -> {
                        "$text "
                    }
                }
            }
        }

        init {
            if (isHashtagEnabled()) {
                chars.add('#')
            }
            if (isMentionEnabled()) {
                chars.add('@')
            }
        }
    }
}
