package com.monzo.androidtest.ui.articledetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monzo.androidtest.database.ArticleDao
import com.monzo.androidtest.data.domain.Article
import kotlinx.coroutines.launch

//Todo
// Not quiet sure how to use Hilt to generate a ViewModel that requires a factory
class ArticleDetailViewModel @ViewModelInject constructor(
        private val articlesDao: ArticleDao,
) : ViewModel() {

    lateinit var article: Article

    private val _favouriteSelected = MutableLiveData<Boolean>()
    val favouriteSelected: LiveData<Boolean>
        get() = _favouriteSelected

    fun setup(article: Article) {
        this.article = article
        _favouriteSelected.postValue(article.favourite ?: false)
    }

    fun onArticleFavourited(isFavourite: Boolean) = viewModelScope.launch {
        _favouriteSelected.postValue(isFavourite)
        article.let { article ->
            articlesDao.update(article.copy(favourite = isFavourite).asDatabaseModel())
        }
    }

}
