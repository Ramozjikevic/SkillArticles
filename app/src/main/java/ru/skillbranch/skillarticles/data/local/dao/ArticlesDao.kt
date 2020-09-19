package ru.skillbranch.skillarticles.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.local.entities.ArticleFull
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem

@Dao
interface ArticlesDao : BaseDao<Article> {

    // Менее производительно, но безопаснее, чем добавление
    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    // Если есть связи через внешнии ключи то произойдет удалине связанных таблиц

    @Transaction
    fun upsert(list: List<Article>) {
        insert(list)
            .mapIndexed { index, recordResult ->
                if(recordResult == -1L) list[index] else null
            }
            .filterNotNull()
            .also { if(it.isNotEmpty()) update(it)}
    }

    @Query("""
       SELECT * FROM articles 
    """)
    fun findArticles(): List<Article>

    @Query("""
        SELECT * FROM articles
        WHERE id = :id
    """)
    fun findArticleById(id: String): Article

    @Query("""
       SELECT * FROM ArticleItem 
    """)
    fun findArticleItems(id: String): List<ArticleItem>

    @Delete
    fun delete(article: Article)

    @Query("""
        SELECT * FROM ArticleItem
        WHERE category_id IN (:categoryIds)
    """)
    fun findArticlesItemsByCategoryIds(categoryIds: List<String>): List<ArticleItem>

    @Query("""
        SELECT * FROM ArticleItem
        INNER JOIN article_tag_x_ref AS refs ON refs.a_id = id
        WHERE refs.t_id = :tag
    """)
    fun findArticlesByTagId(tag: String): List<ArticleItem>

    @RawQuery(observedEntities = [ArticleItem::class])
    fun findArticlesByRaw(simpleSQLiteQuery: SimpleSQLiteQuery): DataSource.Factory<Int, ArticleItem>

    @Query("""
        SELECT * FROM ArticleFull
        WHERE id = :articleId
    """)
    fun findFullArticle(articleId: String): LiveData<ArticleFull>
}