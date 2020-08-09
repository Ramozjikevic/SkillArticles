package ru.skillbranch.skillarticles.ui.articles

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_articles.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.ArticleItemData
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesState
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

class ArticlesFragment : BaseFragment<ArticlesViewModel>() {
    override val viewModel: ArticlesViewModel by viewModels()
    override val layout: Int = R.layout.fragment_articles
    override val binding: ArticlesBinding by lazy { ArticlesBinding() }

    private val articlesAdapter = ArticlesAdapter { item ->
        Log.e("ArticlesFragment", "click on article: ${item.id}")
        val action = ArticlesFragmentDirections.actionNavArticlesToPageArticle(
            item.id,
            item.author,
            item.authorAvatar,
            item.category,
            item.categoryIcon,
            item.date,
            item.poster,
            item.title
        )

        findNavController().navigate(action)
    }

    override fun setupViews() {
        with(rv_articles) {
            layoutManager = LinearLayoutManager(context)
            adapter = articlesAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    inner class ArticlesBinding : Binding() {
        private var articles: List<ArticleItemData> by RenderProp(emptyList()) {
            articlesAdapter.submitList(it)
        }

        override fun bind(data: IViewModelState) {
            data as ArticlesState
            articles = data.articles
        }
    }
}