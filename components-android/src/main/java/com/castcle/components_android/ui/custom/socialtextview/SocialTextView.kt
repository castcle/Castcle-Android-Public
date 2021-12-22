package com.castcle.components_android.ui.custom.socialtextview

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.*
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.doOnAttach
import androidx.core.view.isInvisible
import com.castcle.android.components_android.R
import com.castcle.components_android.ui.custom.socialtextview.extensions.*
import com.castcle.components_android.ui.custom.socialtextview.model.LinkItem
import com.castcle.components_android.ui.custom.socialtextview.model.LinkedType
import com.castcle.components_android.ui.custom.socialtextview.model.LinkedType.*
import com.castcle.extensions.getColorResource
import java.util.*
import java.util.regex.*


class SocialTextView : AppCompatTextView {

    private var hashtagColor = Color.BLUE
    private var mentionColor = Color.YELLOW
    private var emailColor = Color.CYAN
    private var urlColor = Color.RED
    private var phoneColor = Color.GREEN
    private var normalTextColor = Color.BLACK
    private var selectedColor = Color.GRAY
    private var isUnderline = false
    private var linkedType: Int = 0
    private var linkedMentions: List<String> = ArrayList()
    private var linkedHashtag: List<String> = ArrayList()
    private var highlightText: List<String> = ArrayList()
    private var isLinkedMention = false
    private var highlightRadius: Int = 8
    private var highlightBackgroundColor = Color.YELLOW
    private var highlightTextColor = Color.BLACK


    private var patternHashtag: Pattern? = null
    private var patternMention: Pattern? = null
    private var patternText: Pattern? = null
    private var patternLink: Pattern? = null

    private var onLinkClickListener: LinkClickListener? = null

    var state: State = State.COLLAPSED
        private set(value) {
            field = value
            text = when (value) {
                State.EXPANDED -> originalText
                State.COLLAPSED -> collapseText
                State.NON_EXPANDED -> text
            }
            changeListener?.onStateChange(value)
        }

    var changeListener: ChangeListener? = null

    val isExpanded
        get() = state == State.EXPANDED

    val isCollapsed
        get() = state == State.COLLAPSED

    private var originalText: CharSequence = ""
    private var collapseText: CharSequence = ""

    private val readMoreText = context.getString(R.string.social_text_see_more)

    interface LinkClickListener {
        fun onLinkClicked(linkType: LinkedType, matchedText: String)
    }

    fun setLinkClickListener(onLinkClickListener: LinkClickListener) {
        this.onLinkClickListener = onLinkClickListener
    }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        movementMethod = LinkedMovement.getInstance()

        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SocialTextView, defStyleAttr, defStyleAttr
        )

        hashtagColor = typedArray.getColor(
            R.styleable.SocialTextView_hashtagColor,
            Color.parseColor("#82B1FF")
        )
        mentionColor = typedArray.getColor(
            R.styleable.SocialTextView_mentionColor,
            Color.parseColor("#BCBCCF")
        )
        emailColor =
            typedArray.getColor(R.styleable.SocialTextView_emailColor, Color.parseColor("#FF9E80"))
        urlColor =
            typedArray.getColor(
                R.styleable.SocialTextView_urlColor,
                context.getColorResource(R.color.blue)
            )
        phoneColor =
            typedArray.getColor(R.styleable.SocialTextView_phoneColor, Color.parseColor("#03A9F4"))
        normalTextColor =
            typedArray.getColor(R.styleable.SocialTextView_normalTextColor, Color.WHITE)
        selectedColor = typedArray.getColor(R.styleable.SocialTextView_selectedColor, Color.GRAY)
        isUnderline = typedArray.getBoolean(R.styleable.SocialTextView_underLine, false)

        //highlight attributes
        highlightRadius = typedArray.getInt(R.styleable.SocialTextView_highlightRadius, 8)
        highlightBackgroundColor =
            typedArray.getColor(R.styleable.SocialTextView_highlightColor, Color.YELLOW)
        highlightTextColor =
            typedArray.getColor(R.styleable.SocialTextView_highlightTextColor, Color.BLACK)


        linkedType = (typedArray.getInt(R.styleable.SocialTextView_linkTypes, TEXT.value))

        if (typedArray.hasValue(R.styleable.SocialTextView_android_text)) {
            setLinkText(typedArray.getText(R.styleable.SocialTextView_android_text))
        }

        typedArray.recycle()
    }

    fun toggle() {
        when (state) {
            State.EXPANDED -> collapse()
            State.COLLAPSED -> expand()
            else -> {}
        }
    }

    private fun collapse() {
        if (isCollapsed || collapseText.isEmpty()) {
            return
        }
        state = State.COLLAPSED
    }

    private fun expand() {
        if (isExpanded || originalText.isEmpty()) {
            return
        }
        state = State.EXPANDED
    }

    fun setTextReadMore(message: CharSequence?) {
        text = ""
        text = message
        doOnAttach {
            post { setupReadMore() }
        }
    }

    private fun needSkipSetupReadMore(): Boolean =
        isInvisible || lineCount <= DEFAULT_MAX_LINE || isExpanded || text == null || text == collapseText

    private fun setupReadMore() {
        if (needSkipSetupReadMore()) {
            state = State.NON_EXPANDED
            appendLinkText(text.toString())
            return
        }
        originalText = addSocialMediaSpan(text)

        val adjustCutCount = getAdjustCutCount(DEFAULT_MAX_LINE, readMoreText)
        val maxTextIndex = layout.getLineVisibleEnd(DEFAULT_MAX_LINE - 1)
        val originalSubText = text.substring(0, maxTextIndex - 1 - adjustCutCount)

        collapseText = buildSpannedString {
            append(addSocialMediaSpan(originalSubText))
            color(urlColor) { append(readMoreText) }
        }

        text = collapseText
    }

    override fun performLongClick(): Boolean {
        return try {
            super.performLongClick()
        } catch (e: NullPointerException) {
            true
        }
    }

    override fun performLongClick(x: Float, y: Float): Boolean {
        return try {
            super.performLongClick(x, y)
        } catch (e: NullPointerException) {
            true
        }
    }

    private fun setLinkText(text: CharSequence?) {
        setText(addSocialMediaSpan(text))
    }

    override fun setHighlightColor(@ColorInt color: Int) {
        super.setHighlightColor(Color.TRANSPARENT)
    }

    private fun getAdjustCutCount(maxLine: Int, readMoreText: String): Int {
        val lastLineStartIndex = layout.getLineVisibleEnd(maxLine - 2) + 1
        val lastLineEndIndex = layout.getLineVisibleEnd(maxLine - 1)
        val lastLineText = text.substring(lastLineStartIndex, lastLineEndIndex)

        val bounds = Rect()
        paint.getTextBounds(lastLineText, 0, lastLineText.length, bounds)

        var adjustCutCount = -1
        do {
            adjustCutCount++
            val subText = lastLineText.substring(0, lastLineText.length - adjustCutCount)
            val replacedText = subText + readMoreText
            paint.getTextBounds(replacedText, 0, replacedText.length, bounds)
            val replacedTextWidth = bounds.width()
        } while (replacedTextWidth > width)

        return adjustCutCount
    }

    enum class State {
        EXPANDED, COLLAPSED, NON_EXPANDED
    }

    fun setHighlightText(highlightText: List<String>) {
        this.highlightText = highlightText
        val temp = text.toString()
        text = ""
        text = addSocialMediaSpan(temp)
    }

    fun setLinkedMention(linkedMentions: List<String>) {
        this.linkedMentions = linkedMentions
        isLinkedMention = true
        val temp = text.toString()
        text = ""
        text = addSocialMediaSpan(temp)
    }

    fun setLinkedHashtag(linkedHashtag: List<String>) {
        this.linkedHashtag = linkedHashtag
        val temp = text.toString()
        text = ""
        text = addSocialMediaSpan(temp)
    }

    fun appendLinkText(text: String) {
        this.text = ""
        append(addSocialMediaSpan(text))
    }

    private fun addSocialMediaSpan(text: CharSequence?): SpannableString {

        val items = collectLinkItemsFromText(text.toString())
        val textSpan = SpannableString(text)
        for (item in items) {


            if (item.mode == HIGHLITH.value) {
                textSpan.setSpan(
                    RoundedHighlightSpan(
                        highlightRadius,
                        highlightBackgroundColor,
                        highlightTextColor
                    ),
                    item.start,
                    item.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                textSpan.setSpan(object :
                    TouchableSpan(
                        getColorByMode(LinkedType.getType(item.mode)),
                        selectedColor,
                        isUnderline
                    ) {
                    override fun onClick(view: View) {
                        //super.onClick(view)
                        spanTextCanClick(item.mode, enableClick = {
                            onLinkClickListener?.onLinkClicked(
                                LinkedType.getType(item.mode),
                                item.matched
                            )
                        })
                    }
                }, item.start, item.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return textSpan
    }

    private fun getColorByMode(type: LinkedType): Int = when (type) {
        HASHTAG -> hashtagColor
        MENTION -> mentionColor
        PHONE -> phoneColor
        URL -> urlColor
        EMAIL -> emailColor
        TEXT -> normalTextColor

        else -> throw IllegalArgumentException("Invalid Linked Type!")
    }

    private fun spanTextCanClick(itemMode: Int, enableClick: () -> Unit) {
        if (itemMode and EMAIL.value == EMAIL.value) {
            enableClick.invoke()
            return
        }

        if (itemMode and URL.value == URL.value) {
            enableClick.invoke()
            return
        }

        if (itemMode and HASHTAG.value == HASHTAG.value) {
            enableClick.invoke()
            return
        }
        if (itemMode and MENTION.value == MENTION.value) {
            enableClick.invoke()
            return
        }

        if (itemMode and PHONE.value == PHONE.value) {
            enableClick.invoke()
            return
        }

        if (itemMode and HIGHLITH.value == HIGHLITH.value) {
            enableClick.invoke()
            return
        }
    }

    private fun collectLinkItemsFromText(text: String): Set<LinkItem> {
        val items = HashSet<LinkItem>()
        var linkedText: String = text

        if (linkedType and EMAIL.value == EMAIL.value) {
            linkedText = collectLinkItems(
                EMAIL.value,
                items,
                Patterns.EMAIL_ADDRESS.matcher(linkedText),
                linkedText
            )
        }

        if (linkedType and URL.value == URL.value) {
            linkedText =
                collectLinkItems(URL.value, items, linkPattern!!.matcher(linkedText), linkedText)
        }

        if (linkedType and HASHTAG.value == HASHTAG.value) {
            linkedText = collectLinkItems(
                HASHTAG.value,
                items,
                hashtagPattern.matcher(linkedText),
                linkedText
            )
        }
        if (linkedType and MENTION.value == MENTION.value) {
            linkedText = collectLinkItems(
                MENTION.value,
                items,
                mentionPattern!!.matcher(linkedText),
                linkedText
            )
        }

        if (linkedType and PHONE.value == PHONE.value) {
            linkedText =
                collectLinkItems(PHONE.value, items, Patterns.PHONE.matcher(linkedText), linkedText)
        }
        if (linkedType and HIGHLITH.value == HIGHLITH.value) {
            for (item in highlightText) {
                val start = linkedText.indexOf(item)
                items.add(
                    LinkItem(item, start, start + item.length, HIGHLITH.value)
                )
            }
        }

        collectLinkItems(TEXT.value, items, standartText!!.matcher(linkedText), linkedText)


        return items
    }

    private val standartText: Pattern?
        get() {
            if (patternText == null) {
                patternText = Pattern.compile("(?u)(?<![@])#?\\b\\w\\w+\\b")
            }
            return patternText
        }

    private val linkPattern: Pattern?
        get() {
            if (patternLink == null) {
                patternLink = Pattern.compile(
                    "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+(?:(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])|(?:biz|b[abdefghijmnorstvwyz])|(?:cat|com|coop|c[acdfghiklmnoruvxyz])|d[ejkmoz]|(?:edu|e[cegrstu])|f[ijkmor]|(?:gov|g[abdefghilmnpqrstuwy])|h[kmnrtu]|(?:info|int|i[delmnoqrst])|(?:jobs|j[emop])|k[eghimnrwyz]|l[abcikrstuvy]|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])|(?:name|net|n[acefgilopruz])|(?:org|om)|(?:pro|p[aefghklmnrstwy])|qa|r[eouw]|s[abcdeghijklmnortuvyz]|(?:tel|travel|t[cdfghjklmnoprtvwz])|u[agkmsyz]|v[aceginu]|w[fs]|y[etu]|z[amw]))|(?:(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])))(?:\\:\\d{1,5})?)(\\/(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)"
                )
            }
            return patternLink
        }

    private val mentionPattern: Pattern?
        get() {
            if (patternMention == null) {
                patternMention = Pattern.compile("(?:^|\\s|$|[.])@[\\p{L}0-9_]*")
            }
            return patternMention
        }

    private val hashtagPattern: Pattern
        get() {
            if (patternHashtag == null) {
                patternHashtag = Pattern.compile("(?:^|\\s|$)#[\\p{L}0-9_]*")
            }
            return patternHashtag!!
        }

    private fun collectLinkItems(
        mode: Int, items: MutableCollection<LinkItem>,
        matcher: Matcher,
        text: String
    ): String {
        var text = text
        while (matcher.find()) {
            var matcherStart = matcher.start()
            var matchedText = matcher.group()

            if (matchedText.startsWith(" ")) {
                matcherStart += 1
                matchedText = matchedText.substring(1)
            }

            if (mode == HASHTAG.value && linkedHashtag.isNotEmpty()) {
                if (linkedHashtag.contains(matchedText)) {
                    items.add(
                        LinkItem(matchedText, matcherStart, matcher.end(), mode)
                    )
                }
            } else if (mode == MENTION.value && linkedMentions.isNotEmpty()) {
                if (linkedMentions.contains(matchedText)) {
                    items.add(
                        LinkItem(matchedText, matcherStart, matcher.end(), mode)
                    )
                }
            } else {
                items.add(
                    LinkItem(matchedText, matcherStart, matcher.end(), mode)
                )
            }
            text = text.replace(matchedText, addSpace(matchedText.length - 1))
        }
        return text
    }

    private fun addSpace(count: Int): String {
        var addSpace = " "
        for (i in 0 until count) {
            addSpace = "$addSpace "
        }
        return addSpace
    }

    interface ChangeListener {
        fun onStateChange(state: State)
    }
}

private const val DEFAULT_MAX_LINE = 5
