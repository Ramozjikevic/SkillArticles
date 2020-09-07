package ru.skillbranch.skillarticles.data.repositories

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import java.lang.Thread.sleep

object ArticlesRepository {

    private val local = LocalDataHolder
    private val network = NetworkDataHolder

    fun allArticles(): ArticlesDataFactory =
        ArticlesDataFactory(ArticleStrategy.AllArticles(::findArticlesByRange))

    fun searchArticles(searchQuery: String) =
        ArticlesDataFactory(ArticleStrategy.SearchArticle(::searchArticlesByTitle, searchQuery))

    fun findBookmarkArticles() =
        ArticlesDataFactory(ArticleStrategy.BookmarkArticles(::findArticlesByBookmark))

    fun searchBookmarkArticles(searchQuery: String) =
        ArticlesDataFactory(ArticleStrategy.SearchBookmark(::searchArticlesByBookmark, searchQuery))

    private fun findArticlesByRange(start: Int, size: Int)
            = local.localArticleItems.takeByRange(start, size)

    private fun searchArticlesByTitle(start: Int, size: Int, queryTitle: String) =
        local.localArticleItems.filterBy { it.title.contains(queryTitle, true) }
            .takeByRange(start, size)

    private fun findArticlesByBookmark(start: Int, size: Int) =
        local.localArticleItems.filterBy { it.isBookmark }.takeByRange(start, size)

    private fun searchArticlesByBookmark(start: Int, size: Int, queryTitle: String) =
        local.localArticleItems.filterBy {
            it.isBookmark && it.title.contains(queryTitle, true)
        }.takeByRange(start, size)


    fun loadArticlesFromNetwork(start: Int, size: Int): List<ArticleItemData> =
        network.networkArticleItems
            .takeByRange(start, size)
            .apply { sleep(500) }

    fun insertArticlesToDb(articles: List<ArticleItemData>) {
        local.localArticleItems.addAll(articles)
            .apply { sleep(500) }
    }

    fun updateBookmark(articleId: String, isBookmark: Boolean) {
        local.localArticleItems
            .indexOfFirst { it.id == articleId }
            .takeUnless { it == -1 }
            ?.let { idx ->
                LocalDataHolder.localArticleItems[idx] =
                    LocalDataHolder.localArticleItems[idx].copy(isBookmark = isBookmark)
            }
    }

}

class ArticlesDataFactory(val strategy: ArticleStrategy) :
    DataSource.Factory<Int, ArticleItemData>() {
    override fun create(): DataSource<Int, ArticleItemData> = ArticleDataSource(strategy)
}

class ArticleDataSource(private val strategy: ArticleStrategy) :
    PositionalDataSource<ArticleItemData>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<ArticleItemData>
    ) {
        val result = strategy.getItems(params.requestedStartPosition, params.requestedLoadSize)
        callback.onResult(result, params.requestedStartPosition)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ArticleItemData>) {
        val result = strategy.getItems(params.startPosition, params.loadSize)
        callback.onResult(result)
    }
}

sealed class ArticleStrategy() {
    abstract fun getItems(start: Int, size: Int): List<ArticleItemData>

    class AllArticles(
        private val itemProvider: (Int, Int) -> List<ArticleItemData>
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItemData> =
            itemProvider(start, size)
    }

    class SearchArticle(
        private val itemProvider: (Int, Int, String) -> List<ArticleItemData>,
        private val query: String
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItemData> =
            itemProvider(start, size, query)
    }

    class BookmarkArticles(
        private val itemProvider: (Int, Int) -> List<ArticleItemData>
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItemData> =
            itemProvider(start, size)
    }

    class SearchBookmark(
        private val itemProvider: (Int, Int, String) -> List<ArticleItemData>,
        private val query: String
    ) : ArticleStrategy() {
        override fun getItems(start: Int, size: Int): List<ArticleItemData> =
            itemProvider(start, size, query)
    }
}

fun List<ArticleItemData>.filterBy(
    by: (ArticleItemData) -> Boolean
): List<ArticleItemData> {
    return this.asSequence()
        .filter { by(it) }
        .toList()
}

fun List<ArticleItemData>.takeByRange(
    start: Int,
    size: Int
): List<ArticleItemData> {
    return this.drop(start).take(size)
}