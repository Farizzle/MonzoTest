package com.monzo.androidtest.ui.articledetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monzo.androidtest.domain.Article

@Suppress("UNCHECKED_CAST")
class ArticleDetailViewModelFactory(private val article: Article) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleDetailViewModel::class.java)) {
            return ArticleDetailViewModel(article) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }

}