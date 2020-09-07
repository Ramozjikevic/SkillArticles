package ru.skillbranch.skillarticles.ui.articles

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.ui.custom.ArticleItemView

class ArticlesAdapter(
    private val itemClickListener: (ArticleItemData) -> Unit,
    private val toggleBookmarkListener: (String, Boolean) -> Unit
) :
    PagedListAdapter<ArticleItemData, ArticleVH>(ArticleDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val containerView = ArticleItemView(parent.context)
        return ArticleVH(containerView)
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        holder.bind(getItem(position), itemClickListener, toggleBookmarkListener)
    }
}

class ArticleDiffCallback : DiffUtil.ItemCallback<ArticleItemData>() {
    override fun areItemsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData) =
        oldItem == newItem
}

class ArticleVH(override val containerView: ArticleItemView) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    @SuppressLint("SetTextI18n")
    fun bind(
        item: ArticleItemData?,
        itemClickListener: (ArticleItemData) -> Unit,
        toggleBookmarkListener: (String, Boolean) -> Unit
    ) {
        item?.let { item ->
            itemView.setOnClickListener { itemClickListener(item) }
            containerView.bind(item, toggleBookmarkListener)
        }
    }
}