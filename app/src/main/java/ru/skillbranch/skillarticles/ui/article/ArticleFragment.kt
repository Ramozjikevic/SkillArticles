package ru.skillbranch.skillarticles.ui.article

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.search_view_layout.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState
import ru.skillbranch.skillarticles.viewmodels.article.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory

class ArticleFragment : BaseFragment<ArticleViewModel>(), IArticleView {

    private val args: ArticleFragmentArgs by navArgs()

    override val viewModel: ArticleViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = args.articleId
        )
    }

    override val layout: Int = R.layout.fragment_article

    override val binding: ArticleBinding by lazy { ArticleBinding() }

     override fun setupViews() {
         setupBottomBar()
         setupSubmenu()
     }

     override fun showSearchBar() {
      /*   bottombar.setSearchState(true)
         scroll.setMarginOptionally(bottom = dpToIntPx(56))*/
     }

     override fun hideSearchBar() {
    /*     bottombar.setSearchState(false)
         scroll.setMarginOptionally(bottom = dpToIntPx(0))*/
     }

      override fun onPrepareOptionsMenu(menu: Menu) {
          super.onPrepareOptionsMenu(menu)
          /*   menuInflater.inflate(R.menu.menu_search, menu)
             val searchItem = menu?.findItem(R.id.action_search)
             val searchView = searchItem?.actionView as? SearchView
             searchView?.queryHint = getString(R.string.article_search_placeholder)

             if (binding.isSearch) {
                 searchItem?.expandActionView()
                 searchView?.setQuery(binding.searchQuery, false)

                 if (binding.isFocusedSearch) searchView?.requestFocus()
                 else searchView?.clearFocus()
             }

             searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                 override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                     viewModel.handleSearchMode(true)
                     return true
                 }

                 override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                     viewModel.handleSearchMode(false)
                     return true
                 }
             })

             searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                 override fun onQueryTextSubmit(text: String?): Boolean {
                     viewModel.handleSearch(text)
                     return true
                 }

                 override fun onQueryTextChange(text: String?): Boolean {
                     viewModel.handleSearch(text)
                     return true
                 }
             })*/
      }

    private fun setupSubmenu() {
/*        btn_text_up.setOnClickListener { viewModel.handleUpText() }
        btn_text_down.setOnClickListener { viewModel.handleDownText() }
        switch_mode.setOnClickListener { viewModel.handleNightMode() }*/
    }

    private fun setupBottomBar() {
/*        btn_like.setOnClickListener { viewModel.handleLike() }
        btn_bookmark.setOnClickListener { viewModel.handleBookmark() }
        btn_share.setOnClickListener { viewModel.handleShare() }
        btn_settings.setOnClickListener { viewModel.handleToggleMenu() }

        btn_result_up.setOnClickListener {
            if (search_view.hasFocus()) search_view.clearFocus()
            root.hideKeyBoard(btn_result_up)
            viewModel.handleUpResult()
        }
        btn_result_down.setOnClickListener {
            if(!tv_text_content.hasFocus()) tv_text_content.requestFocus()
            root.hideKeyBoard(btn_result_down)
            viewModel.handleDownResult()
        }

        btn_search_close.setOnClickListener {
            viewModel.handleSearchMode(false)
            root.invalidateOptionsMenu()
        }*/
    }

    private fun setupCopyListener() {
        tv_text_content.setCopyListener { copy ->
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied code", copy)
            clipboard.setPrimaryClip(clip)
            viewModel.handleCopyCode()
        }
    }

    inner class ArticleBinding : Binding() {

        var isFocusedSearch: Boolean = false
        var searchQuery: String? = null

        private var isLoadingContent by RenderProp(true)

/*        private var isLike: Boolean by RenderProp(false) { btn_like.isChecked = it }
        private var isBookmark: Boolean by RenderProp(false) { btn_bookmark.isChecked = it }
        private var isShowMenu: Boolean by RenderProp(false) {
            btn_settings.isChecked = it
           // if (it) submenu.open() else submenu.close()
        }

        private var isBigText: Boolean by RenderProp(false) {
            if (it) {
                tv_text_content.textSize = 18f
                btn_text_up.isChecked = true
                btn_text_down.isChecked = false
            } else {
                tv_text_content.textSize = 14f
                btn_text_up.isChecked = false
                btn_text_down.isChecked = true
            }
        }*/

/*        private var isDarkMode: Boolean by RenderProp(false, false) {
            switch_mode.isChecked = it
            root.delegate.localNightMode = if (it) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        }*/

        var isSearch: Boolean by RenderProp(false) {
/*            if (it){
                with(toolbar) {
                    (layoutParams as AppBarLayout.LayoutParams).scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
                }
                showSearchBar()
            } else {
                with(toolbar) {
                    (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
                }
                hideSearchBar()
            }*/
        }

        private var searcResults: List<Pair<Int, Int>> by RenderProp(emptyList())
        private var searchPosition: Int by RenderProp(0)

        private var content: List<MarkdownElement> by RenderProp(emptyList()) {
            tv_text_content.isLoading = it.isEmpty()
            tv_text_content.setContent(it)
            if(it.isNotEmpty()) setupCopyListener()

        }

        override val afterInflated: (() -> Unit) = {
            dependsOn<Boolean, Boolean, List<Pair<Int, Int>>, Int>(
                ::isLoadingContent,
                ::isSearch,
                ::searcResults,
                ::searchPosition
            ) { ilc, iss, sr, sp ->
                if (!ilc && iss) {
                    tv_text_content.renderSearchResult(sr)
                    tv_text_content.renderSearchPosition(sr.getOrNull(sp))
                }
                if (!ilc && !iss) {
                    tv_text_content.clearSearchResult()
                }

         //       bottombar.bindSearchInfo(sr.size, sp)
            }
        }

        override fun bind(data: IViewModelState) {
            data as ArticleState

/*            isLike = data.isLike
            isBookmark = data.isBookmark
            isShowMenu = data.isShowMenu
            isBigText = data.isBigText
            isDarkMode = data.isDarkMode*/
            content = data.content

            isLoadingContent = data.isLoadingContent
            isSearch = data.isSearch
            searchQuery = data.searchQuery
            searchPosition = data.searchPosition
            searcResults = data.searchResults
        }

        override fun saveUi(outState: Bundle) {
            outState.putBoolean(::isFocusedSearch.name, search_view?.hasFocus() ?: false)
        }

        override fun restoreUi(savedState: Bundle?) {
            isFocusedSearch = savedState?.getBoolean(::isFocusedSearch.name) ?: false
        }
    }
}