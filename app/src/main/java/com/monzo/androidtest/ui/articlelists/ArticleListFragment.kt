package com.monzo.androidtest.ui.articlelists

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.monzo.androidtest.R
import com.monzo.androidtest.api.GuardianApiStatus
import com.monzo.androidtest.databinding.FragmentArticleListBinding
import com.monzo.androidtest.domain.Article
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ArticleListFragment : Fragment(R.layout.fragment_article_list), ArticleAdapter.ArticleOnClickListener {
    private val viewModel: ArticlesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding(view)
        setHasOptionsMenu(true)
    }

    private fun setupBinding(view: View) {
        val binding = FragmentArticleListBinding.bind(view)
        val favouritesAdapter = ArticleAdapter(this)
        val thisWeekAdapter = ArticleAdapter(this)
        val lastWeeksAdapter = ArticleAdapter(this)
        val olderAdapter = ArticleAdapter(this)
        binding.apply {
            lifecycleOwner = this@ArticleListFragment
            articlesViewModel = viewModel
            favouritesRv.adapter = favouritesAdapter
            thisWeekRv.adapter = thisWeekAdapter
            lastWeekRv.adapter = lastWeeksAdapter
            olderRv.adapter = olderAdapter
            articlesSwiperefreshlayout.setOnRefreshListener {
                viewModel.onRefresh()
            }
        }
        viewModel.feedStatus.observe(viewLifecycleOwner, Observer { status ->
            handleApiStatus(status, binding)
            viewModel.feedStatus.postValue(null)
        })
        viewModel.detailStatus.observe(viewLifecycleOwner, Observer { status ->
            handleApiStatus(status, binding)
            viewModel.detailStatus.postValue(null)
        })
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.articleEvent.collect { event ->
                when (event) {
                    is ArticlesViewModel.ArticleEvent.NavigateToArticleDetail -> {
                        findNavController().navigate(ArticleListFragmentDirections.actionArticleListFragmentToArticleDetailFragment(event.article))
                    }
                }
            }
        }
    }

    private fun handleApiStatus(status: GuardianApiStatus?, binding: FragmentArticleListBinding) {
        when (status) {
            GuardianApiStatus.LOADING -> binding.articlesSwiperefreshlayout.isRefreshing = true
            GuardianApiStatus.SUCCESS -> binding.articlesSwiperefreshlayout.isRefreshing = false
            GuardianApiStatus.ERROR -> {
                binding.articlesSwiperefreshlayout.isRefreshing = false
                Snackbar.make(requireView(), "Oops! Something went wrong, pleasue check internet connection", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun articleFavourited(article: Article, isFavourite: Boolean) {
        viewModel.onArticleFavourited(article, isFavourite)
    }

    override fun articleClicked(article: Article) {
        viewModel.onArticleClicked(article)
    }
    
}