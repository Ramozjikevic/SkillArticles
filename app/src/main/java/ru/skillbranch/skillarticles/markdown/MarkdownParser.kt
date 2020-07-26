package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = "\n"

    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?\\*(?!\\*)|(?<!_)_[^_].*?_(?!_))"
    private const val BOLD_GROUP = "((?<!\\*)\\*{2}[^*].*?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?~{2}(?!~))"
    private const val RULE_GROUP = "(^[*]{3}|[-]{3}|[_]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?\\]\\(.+?\\)|^\\[.*?]\\(.*?\\))"
    private const val BLOCK_CODE_GROUP = "(^```[\\s\\S]+?```\$)"
    private const val ORDER_LIST_GROUP = "(^\\d{1,2}\\. .+$)"

    private const val MARKDOWN_GROUPS =
        "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP|$ITALIC_GROUP|$BOLD_GROUP" +
                "|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP|$BLOCK_CODE_GROUP|$ORDER_LIST_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(
            findElements(
                string
            )
        )
        return MarkdownText(elements)
    }

    fun clear(string: String?): String? {
        string ?: return null
        return findElements(string).toClearString()
    }

    private fun List<Element>.toClearString(): String {
        return buildString {
            this@toClearString.forEach { el ->
                if (el.elements.isEmpty()) {
                    append(el.text)
                } else append(el.elements.toClearString())
            }
        }
    }

    private fun findElements(string: CharSequence): List<Element> {

        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)

        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            if (lastStartIndex < startIndex) {
                parents.add(
                    Element.Text(
                        string.subSequence(lastStartIndex, startIndex)
                    )
                )
            }

            var text: CharSequence

            val groups = 1..11

            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                // NOT FOUND -> BREAK
                -1 -> break@loop

                // UNORDERED LIST
                1 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // HEADER
                2 -> {
                    val reg = "^#{1,6}".toRegex().find(
                        string.subSequence(startIndex, endIndex)
                    )
                    val level = reg!!.value.length

                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)

                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex

                }

                // QUOTE
                3 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    val subs = findElements(text)
                    val element = Element.Quote(text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // ITALIC
                4 -> {
                    // text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())

                    val subs = findElements(text)
                    val element = Element.Italic(text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // BOLD
                5 -> {
                    // text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))

                    val subs = findElements(text)
                    val element = Element.Bold(text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // STRIKE
                6 -> {
                    // text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))

                    val subs = findElements(text)
                    val element = Element.Strike(text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // RULE
                7 -> {
                    // text without "***" insert empty character
                    val element = Element.Rule()
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // INLINE CODE
                8 -> {
                    // text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())

                    val element = Element.InlineCode(text)
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // LINK
                9 -> {
                    // full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title: String, link: String) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured

                    val element = Element.Link(link, title)
                    parents.add(element)

                    lastStartIndex = endIndex
                }
                // 10 -> BLOCK CODE - optionally
                10 -> {
                    // text without "```{}```"
                    text = string.subSequence(startIndex.plus(3), endIndex.plus(-3))

                    if (text.contains(LINE_SEPARATOR)) {
                        for ((ind, line) in text.lines().withIndex()) {

                            val element = when (ind) {
                                0 -> Element.BlockCode(Element.BlockCode.Type.START, line + LINE_SEPARATOR)
                                text.lines().lastIndex -> Element.BlockCode(Element.BlockCode.Type.END, line)
                                else -> Element.BlockCode(Element.BlockCode.Type.MIDDLE, line + LINE_SEPARATOR)
                            }
                            parents.add(element)

                        }
                    }

                    lastStartIndex = endIndex
                }

                // 11 -> NUMERIC LIST
                11 -> {
                    val reg =
                        "^\\d{1,2}\\.".toRegex().find(string.subSequence(startIndex, endIndex))
                    val order = reg!!.value

                    text = string.subSequence(startIndex.plus(order.length.inc()), endIndex)

                    val subs = findElements(text)
                    val element = Element.OrderedListItem(order, text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }
            }
        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }

        return parents
    }

}

data class MarkdownText(val elements: List<Element>)

sealed class Element {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ", // for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence, // for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence, // for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }
}
