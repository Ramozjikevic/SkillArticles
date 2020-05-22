package ru.skillbranch.skillarticles

import org.junit.Test

import org.junit.Assert.*
import org.junit.Ignore
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.markdown.Element
import ru.skillbranch.skillarticles.markdown.MarkdownParser

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun parse_list_item() {
        val result = MarkdownParser.parse(unorderedListString)
        val actual = prepare<Element.UnorderedListItem>(result.elements)
        assertEquals(expectedUnorderedList, actual)
    }

    @Test
    fun parse_header() {
        val result = MarkdownParser.parse(headerString)
        val actual = prepare<Element.Header>(result.elements)
        val actualLevels = result.elements.spread()
            .filterIsInstance<Element.Header>()
            .map{it.level}
        assertEquals(expectedHeader, actual)
        assertEquals(listOf(1,2,3,4,5,6), actualLevels)
    }

    @Test
    fun parse_quote() {
        val result = MarkdownParser.parse(quoteString)
        val actual = prepare<Element.Quote>(result.elements)
        assertEquals(expectedQuote, actual)
    }

    @Test
    fun parse_italic() {
        val result = MarkdownParser.parse(italicString)
        val actual = prepare<Element.Italic>(result.elements)
        assertEquals(expectedItalic, actual)
    }

    @Test
    fun parse_bold() {
        val result = MarkdownParser.parse(boldString)
        val actual = prepare<Element.Bold>(result.elements)
        assertEquals(expectedBold, actual)
    }

    @Test
    fun parse_strike() {
        val result = MarkdownParser.parse(strikeString)
        val actual = prepare<Element.Strike>(result.elements)
        assertEquals(expectedStrike, actual)
    }

    @Test
    fun parse_combine() {
        val result = MarkdownParser.parse(combineEmphasisString)
        val actualItalic= prepare<Element.Italic>(result.elements)
        val actualBold= prepare<Element.Bold>(result.elements)
        val actualStrike= prepare<Element.Strike>(result.elements)
        assertEquals(expectedCombine["italic"], actualItalic)
        assertEquals(expectedCombine["bold"], actualBold)
        assertEquals(expectedCombine["strike"], actualStrike)
    }

    @Test
    fun parse_rule() {
        val result = MarkdownParser.parse(ruleString)
        val actual = prepare<Element.Rule>(result.elements)
        assertEquals(3, actual.size)
    }

    @Test
    fun parse_inline_code() {
        val result = MarkdownParser.parse(inlineString)
        val actual = prepare<Element.InlineCode>(result.elements)
        assertEquals(expectedInline, actual)
    }

    @Test
    fun parse_link() {
        val result = MarkdownParser.parse(linkString)
        val actual = prepare<Element.Link>(result.elements)
        val actualLink = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map{it.link}

        assertEquals(expectedLink["titles"], actual)
        assertEquals(expectedLink["links"], actualLink)
    }

    @Test
    fun parse_all() {
        val result = MarkdownParser.parse(markdownString)
        val actualUnorderedList = prepare<Element.UnorderedListItem>(result.elements)
        val actualHeaders = prepare<Element.Header>(result.elements)
        val actualQuotes = prepare<Element.Quote>(result.elements)
        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)
        val actualRule = prepare<Element.Rule>(result.elements)
        val actualInline = prepare<Element.InlineCode>(result.elements)
        val actualLinkTitles = prepare<Element.Link>(result.elements)
        val actualLinks = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map { it.link }

        assertEquals(expectedMarkdown["unorderedList"], actualUnorderedList)
        assertEquals(expectedMarkdown["header"], actualHeaders)
        assertEquals(expectedMarkdown["quote"], actualQuotes)
        assertEquals(expectedMarkdown["italic"], actualItalic)
        assertEquals(expectedMarkdown["bold"], actualBold)
        assertEquals(expectedMarkdown["strike"], actualStrike)
        assertEquals(3, actualRule.size)
        assertEquals(expectedMarkdown["inline"], actualInline)
        assertEquals(expectedMarkdown["linkTitles"], actualLinkTitles)
        assertEquals(expectedMarkdown["links"], actualLinks)
    }

    @Test
    fun clear_all() {
        val result = MarkdownParser.clear(markdownString)
        assertEquals(markdownClearString,  result)
    }

    //optionally (delete @Ignore fo run)
    @Test
    fun clear_all_with_optionally() {
        val result = MarkdownParser.clear(markdownString)
        assertEquals(markdownOptionallyClearString,  result)
    }

    //optionally (delete @Ignore fo run)
    @Test
    fun parse_all_with_optionally() {
        val result = MarkdownParser.parse(markdownString)
        val actualUnorderedList = prepare<Element.UnorderedListItem>(result.elements)
        val actualHeaders = prepare<Element.Header>(result.elements)
        val actualQuotes = prepare<Element.Quote>(result.elements)
        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)
        val actualRule = prepare<Element.Rule>(result.elements)
        val actualInline = prepare<Element.InlineCode>(result.elements)
        val actualLinkTitles = prepare<Element.Link>(result.elements)
        val actualLinks = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map { it.link }
        val actualBlockCode = prepare<Element.BlockCode>(result.elements) //optionally
        val actualOrderedList = prepare<Element.OrderedListItem>(result.elements) //optionally

        assertEquals(expectedMarkdown["unorderedList"], actualUnorderedList)
        assertEquals(expectedMarkdown["header"], actualHeaders)
        assertEquals(expectedMarkdown["quote"], actualQuotes)
        assertEquals(expectedMarkdown["italic"], actualItalic)
        assertEquals(expectedMarkdown["bold"], actualBold)
        assertEquals(expectedMarkdown["strike"], actualStrike)
        assertEquals(3, actualRule.size)
        assertEquals(expectedMarkdown["inline"], actualInline)
        assertEquals(expectedMarkdown["linkTitles"], actualLinkTitles)
        assertEquals(expectedMarkdown["links"], actualLinks)
        assertEquals(expectedMarkdown["multiline"], actualBlockCode) //optionally
        assertEquals(expectedMarkdown["orderedList"], actualOrderedList) //optionally
    }

    private fun printResults(list:List<String>){
        val iterator = list.iterator()
        while (iterator.hasNext()){
            println("find >> ${iterator.next()}")
        }
    }

    private fun printElements(list:List<Element>){
        val iterator = list.iterator()
        while (iterator.hasNext()){
            println("element >> ${iterator.next()}")
        }
    }

    private fun Element.spread():List<Element>{
        val elements = mutableListOf<Element>()
        elements.add(this)
        elements.addAll(this.elements.spread())
        return elements
    }

    private fun List<Element>.spread():List<Element>{
        val elements = mutableListOf<Element>()
        if(this.isNotEmpty()) elements.addAll(
            this.fold(mutableListOf()){acc, el -> acc.also { it.addAll(el.spread()) }}
        )
        return elements
    }

    private inline fun <reified T:Element> prepare(list:List<Element>) : List<String>{
        return list
            .fold(mutableListOf<Element>()){ acc, el -> //spread inner elements
                acc.also { it.addAll(el.spread()) }
            }
            .filterIsInstance<T>() //filter only expected instance
            .map { it.text.toString() } //transform to element text
    }
}

