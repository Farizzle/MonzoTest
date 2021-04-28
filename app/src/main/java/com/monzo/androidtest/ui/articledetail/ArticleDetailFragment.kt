package com.monzo.androidtest.ui.articledetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.monzo.androidtest.R
import com.monzo.androidtest.databinding.FragmentArticleDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleDetailFragment : Fragment(R.layout.fragment_article_detail) {

    //Todo
    // Inject VM into Fragment
    private val viewModel: ArticleDetailViewModel by lazy {
        arguments?.let { bundle ->
            val article = ArticleDetailFragmentArgs.fromBundle(bundle).article
            val factory = ArticleDetailViewModelFactory(article)
            ViewModelProvider(this, factory).get(ArticleDetailViewModel::class.java)
        }!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupBinding(view)
    }

    private fun setupBinding(view: View) {
        val binding = FragmentArticleDetailBinding.bind(view)
        binding.apply {
            articleDetailViewModel = viewModel
            executePendingBindings()
        }
    }
}