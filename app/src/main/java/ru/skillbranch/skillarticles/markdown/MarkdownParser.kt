package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("Line.separator") ?: "\n"
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP = "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!\\~)\\~{2}[^~].*?[^~]\\~{2}(?!\\~))"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"
    private const val ORDERED_GROUP = "(^\\d\\. .+$)"
    private const val BLOCK_GROUP = "(^`{3}[\\s\\S]+?`{3}$)"

    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
            "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP|$ORDERED_GROUP|$BLOCK_GROUP"

    private val elementsPatter by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    fun clear(string: String?): String? {
        string ?: return null
        var clearString = ""
        val matcher = elementsPatter.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()
            if (lastStartIndex < startIndex) {
                clearString += string.substring(lastStartIndex, startIndex)
            }
            var text: String
            val groups = 1..11
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                -1 -> break@loop
                1 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex).toString()
                    val subs = clear(text)
                    clearString += if (subs.isNullOrEmpty()) text else subs

                    lastStartIndex = endIndex
                }

                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.substring(startIndex, endIndex))
                    val level = reg!!.value.length

                    clearString += string.substring(startIndex + level.inc(), endIndex)
                    lastStartIndex = endIndex
                }

                3 -> {
                    text = string.substring(startIndex.plus(2), endIndex)
                    val subs = clear(text)
                    clearString += if (subs.isNullOrEmpty()) text else subs
                    lastStartIndex = endIndex
                }

                4 -> {
                    text = string.substring(startIndex.inc(), endIndex.dec())
                    val subs = clear(text)
                    clearString += if (subs.isNullOrEmpty()) text else subs
                    lastStartIndex = endIndex
                }

                5 -> {
                    text = string.substring(startIndex.plus(2), endIndex.plus(-2))
                    val subs = clear(text)
                    clearString += if (subs.isNullOrEmpty()) text else subs
                    lastStartIndex = endIndex
                }

                6 -> {
                    text = string.substring(startIndex.plus(2), endIndex.plus(-2))
                    val subs = clear(text)
                    clearString += if (subs.isNullOrEmpty()) text else subs
                    lastStartIndex = endIndex
                }

                7 -> {
                    clearString += " "
                    lastStartIndex = endIndex
                }

                8 -> {
                    text = string.substring(startIndex.inc(), endIndex.dec())
                    clearString += text
                    lastStartIndex = endIndex
                }

                9 -> {
                    text = string.substring(startIndex, endIndex)
                    val (title: String, _) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    clearString += title
                    lastStartIndex = endIndex
                }

                10 -> {
                    val reg = "(^\\d+)".toRegex().find(string.substring(startIndex, endIndex))
                    val order = reg!!.value
                    text = string.subSequence(startIndex.plus(order.length + 2), endIndex).toString()
                    val subs = clear(text)
                    clearString += if (subs.isNullOrEmpty()) text else subs
                    lastStartIndex = endIndex
                }

                11 -> {
                    text = string.subSequence(startIndex.plus(3), endIndex.plus(-3)).toString()
                    clearString += text
                    lastStartIndex = endIndex
                }
            }
        }

        // check if there's any more text left
        if (lastStartIndex < string.length) {
            clearString += string.substring(lastStartIndex, string.length)
        }

        return clearString
    }

    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPatter.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            if (lastStartIndex < startIndex) {
                parents.add(Element.Text(string.subSequence(lastStartIndex, startIndex)))
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
                -1 -> break@loop

                //UNORDERED LIST
                1 -> {
                    //text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                //HEADER
                2 -> {
                    //text without "{#} "
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)

                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //QUOTE
                3 -> {
                    //text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subelements = findElements(text)

                    val element = Element.Quote(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //ITALIC
                4 -> {
                    //text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements = findElements(text)
                    val element = Element.Italic(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //BOLD
                5 -> {
                    //text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements = findElements(text)
                    val element = Element.Bold(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //STRIKE
                6 -> {
                    //text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements = findElements(text)
                    val element = Element.Strike(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //RULE
                7 -> {
                    //text without "***" insert empty character
                    val element = Element.Rule()
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //INLINE CODE
                8 -> {
                    //text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val element = Element.InlineCode(text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //LINK
                9 -> {
                    text = string.subSequence(startIndex, endIndex)
                    val (title: String, link: String) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    val element = Element.Link(link, title)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //ORDERED LIST
                10 -> {
                    val order = string.subSequence(startIndex.plus(0),startIndex.plus(1)).toString()
                    text = string.subSequence(startIndex.plus(3), endIndex)
                    val subs = findElements(text)
                    val element = Element.OrderedListItem(order, text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }
                //BLOCK CODE
                11 -> {
                    text = string.subSequence(startIndex.plus(3), endIndex.minus(3)).toString()

                    if (text.contains(LINE_SEPARATOR)) {
                        for ((index, line) in text.lines().withIndex()) {
                            when (index) {
                                text.lines().lastIndex -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.END,
                                        line
                                    )
                                )
                                0 -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.START,
                                        line + LINE_SEPARATOR
                                    )
                                )
                                else -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.MIDDLE,
                                        line + LINE_SEPARATOR
                                    )
                                )
                            }
                        }
                    } else parents.add(Element.BlockCode(Element.BlockCode.Type.SINGLE, text))

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
        override val text: CharSequence = " ",
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link (
        val link: String,
        override val text: CharSequence,
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

