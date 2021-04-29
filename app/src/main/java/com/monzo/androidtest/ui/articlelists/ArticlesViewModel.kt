package com.monzo.androidtest.ui.articlelists

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monzo.androidtest.database.ArticleDao
import com.monzo.androidtest.domain.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ArticlesViewModel @ViewModelInject constructor(
        private val articlesDao: ArticleDao,
        private val repository: ArticlesRepository
) : ViewModel() {

    val feedStatus = repository.feedStatus
    val detailStatus = repository.detailStatus

    private val articleEventChannel = Channel<ArticleEvent>()
    val articleEvent = articleEventChannel.receiveAsFlow()

    val favouriteArticles = repository.favourites
    val thisWeeksArticle = repository.thisWeeksArticles
    val lastWeeksArticles = repository.lastWeeksArticles
    val olderArticles = repository.olderArticles

    private var coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    init {
        viewModelScope.launch {
            repository.getLatestFintechArticles()
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            repository.getLatestFintechArticles()
        }
    }

    fun onArticleFavourited(article: Article, isFavourited: Boolean) = viewModelScope.launch {
        articlesDao.update(article.copy(favourite = isFavourited).asDatabaseModel())
    }

    fun onArticleClicked(article: Article) {
        coroutineScope.launch {
            var updatedArticle = repository.getArticleDetails(article)
            if (updatedArticle.body != null) {
                articleEventChannel.send(ArticleEvent.NavigateToArticleDetail(updatedArticle))
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }

    sealed class ArticleEvent {
        data class NavigateToArticleDetail(val article: Article) : ArticleEvent()
    }
}
