package com.monzo.androidtest.ui.articlelists

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.monzo.androidtest.common.DateHelper
import com.monzo.androidtest.database.ArticleDao
import com.monzo.androidtest.database.model.asDomainModel
import com.monzo.androidtest.domain.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArticlesViewModel @ViewModelInject constructor(
        private val articlesDao: ArticleDao,
        private val repository: ArticlesRepository,
        @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("seachQuery", "")
    val sectionFilter = state.getLiveData("sectionFilter", "")
    val feedStatus = repository.feedStatus
    val detailStatus = repository.detailStatus

    private val articleEventChannel = Channel<ArticleEvent>()
    val articleEvent = articleEventChannel.receiveAsFlow()

    private val articleFlow = combine(
            searchQuery.asFlow(),
            sectionFilter.asFlow()
    ) { query, sectionFilter ->
        Pair(query, sectionFilter)
    }.flatMapLatest { (query, sectionFilter) ->
        articlesDao.getArticlesByQuery(query, false, sectionFilter)
    }

    private val favArticleFlow = combine(
            searchQuery.asFlow(),
            sectionFilter.asFlow()
    ) { query, sectionFilter ->
        Pair(query, sectionFilter)
    }.flatMapLatest { (query, sectionFilter) ->
        articlesDao.getArticlesByQuery(query, true, sectionFilter)
    }
    val favouriteArticles = Transformations.map(favArticleFlow.asLiveData()) { articles ->
        articles.asDomainModel()
    }
    val thisWeeksArticle = Transformations.map(articleFlow.asLiveData()) { articles ->
        articles.asDomainModel().filter {
            DateHelper.articleForThisWeek(it) && it.favourite == false
        }
    }
    val lastWeeksArticles = Transformations.map(articleFlow.asLiveData()) { articles ->
        articles.asDomainModel().filter {
            DateHelper.articleForLastWeek(it) && it.favourite == false
        }
    }
    val olderArticles = Transformations.map(articleFlow.asLiveData()) { articles ->
        articles.asDomainModel().filter {
            DateHelper.oldArticle(it) && it.favourite == false
        }
    }
    val sections = Transformations.map(repository.sections.asLiveData()) {
        it.asDomainModel()
    }

    private var coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + coroutineJob)

    init {
        viewModelScope.launch {
            if (searchQuery.value.isNullOrBlank()) {
                repository.getLatestFintechArticles(null)
            } else {
                repository.getLatestFintechArticles(searchQuery.value)
            }
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            repository.getLatestFintechArticles(searchQuery.value)
        }
    }

    fun onArticleFavourited(article: Article, isFavourited: Boolean) = viewModelScope.launch {
        articlesDao.update(article.copy(favourite = isFavourited).asDatabaseModel())
    }

    fun onArticleClicked(article: Article) = coroutineScope.launch {
            var updatedArticle = repository.getArticleDetails(article)
            if (updatedArticle.body != null) {
                articleEventChannel.send(ArticleEvent.NavigateToArticleDetail(updatedArticle))
            }
        }


    fun onSearchQueryUpdated(query: String) = viewModelScope.launch {
        repository.getLatestFintechArticles(query)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }

    sealed class ArticleEvent {
        data class NavigateToArticleDetail(val article: Article) : ArticleEvent()
    }
}
