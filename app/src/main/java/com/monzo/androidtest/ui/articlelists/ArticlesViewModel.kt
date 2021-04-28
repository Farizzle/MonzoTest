package com.monzo.androidtest.ui.articlelists

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.monzo.androidtest.api.GuardianApiStatus
import com.monzo.androidtest.database.ArticleDao
import com.monzo.androidtest.database.PreferencesManager
import com.monzo.androidtest.database.model.asDomainModel
import com.monzo.androidtest.domain.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ArticlesViewModel @ViewModelInject constructor(
        private val articlesDao: ArticleDao,
        private val preferencesManager: PreferencesManager,
        private val repository: ArticlesRepository
) : ViewModel() {

    val feedStatus = repository.feedStatus
    val detailStatus = repository.detailStatus

    private val _showFavourites = MutableLiveData<Boolean>()
    val showFavourites: LiveData<Boolean>
        get() = _showFavourites

    val preferencesFlow = preferencesManager.preferencesFlow

    private val articleEventChannel = Channel<ArticleEvent>()
    val articleEvent = articleEventChannel.receiveAsFlow()

    //Todo
    // Using combines with other flows will allow further filtering if I was able to add search/tag/date filters
    private val articlesFlow = preferencesFlow.flatMapLatest { showOnlyFavourites ->
        _showFavourites.postValue(showOnlyFavourites.showFavourites)
        articlesDao.getArticles(showOnlyFavourites.showFavourites)
    }

    val articles = articlesFlow.map {
        it.asDomainModel()
    }.asLiveData()

    private var coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    init {
        viewModelScope.launch {
            repository.getLatestFintechArticles()
        }
        _showFavourites.postValue(false)
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


    fun showFavourites(showFavourites: Boolean) = viewModelScope.launch {
        preferencesManager.updateFavouritesSelected(showFavourites)
        _showFavourites.postValue(showFavourites)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }

    sealed class ArticleEvent {
        data class NavigateToArticleDetail(val article: Article) : ArticleEvent()
    }
}
