package com.monzo.androidtest.ui.articledetail

import androidx.lifecycle.ViewModel
import com.monzo.androidtest.domain.Article

//Todo
// Not quiet sure how to use Hilt to generate a ViewModel that requires a factory
class ArticleDetailViewModel (val article: Article) : ViewModel()