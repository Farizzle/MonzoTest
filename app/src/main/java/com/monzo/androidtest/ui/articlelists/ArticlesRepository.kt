package com.monzo.androidtest.ui.articlelists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.monzo.androidtest.api.GuardianApiStatus
import com.monzo.androidtest.api.GuardianService
import com.monzo.androidtest.api.model.asDatabaseModel
import com.monzo.androidtest.common.DateHelper.articleForLastWeek
import com.monzo.androidtest.common.DateHelper.articleForThisWeek
import com.monzo.androidtest.common.DateHelper.oldArticle
import com.monzo.androidtest.database.ArticleDatabase
import com.monzo.androidtest.database.model.asDomainModel
import com.monzo.androidtest.domain.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ArticlesRepository @Inject constructor(
        private val database: ArticleDatabase,
        private val guardianService: GuardianService,
) {

    private val _feedStatus = MutableLiveData<GuardianApiStatus>()
    val feedStatus get() = _feedStatus

    private val _detailStatus = MutableLiveData<GuardianApiStatus>()
    val detailStatus get() = _detailStatus

//    val articles = database.articleDao().getArticlesByQuery()
//    val favouriteArticles = database.articleDao().getFavouriteArticles()
//    val favourites = Transformations.map(articles.asLiveData()) { articles ->
//        articles.asDomainModel().filter { it.favourite == true }
//    }
//    val thisWeeksArticles = Transformations.map(articles.asLiveData()) { articles ->
//        articles.asDomainModel().filter {
//            articleForThisWeek(it) && it.favourite == false
//        }
//    }
//    val lastWeeksArticles = Transformations.map(articles.asLiveData()) { articles ->
//        articles.asDomainModel().filter {
//            articleForLastWeek(it) && it.favourite == false
//        }
//    }
//    val olderArticles = Transformations.map(articles.asLiveData()) { articles ->
//        articles.asDomainModel().filter {
//            oldArticle(it) && it.favourite == false
//        }
//    }

    suspend fun getLatestFintechArticles(searchTerm: String?) {
        _feedStatus.postValue(GuardianApiStatus.LOADING)
        withContext(Dispatchers.IO) {
            try {
                val articleResponse = guardianService.searchArticlesAsync(searchTerm).await()
                withContext(Dispatchers.Main) {
                    _feedStatus.postValue(GuardianApiStatus.SUCCESS)
                }
                database.articleDao().insertAll(*articleResponse.response.results.asDatabaseModel())
            } catch (exception: Exception) {
                withContext(Dispatchers.Main) {
                    _feedStatus.postValue(GuardianApiStatus.ERROR)
                }
                exception.printStackTrace()
            }
        }
    }

    suspend fun getArticleDetails(article: Article): Article {
        _detailStatus.postValue(GuardianApiStatus.LOADING)
        try {
            val articleDetailResponse = guardianService.getArticleAsync(article.url, "headline,thumbnail,body").await()
            val apiArticle = articleDetailResponse.response.content
            val updatedArticle = article.copy(body = apiArticle.fields?.body)
            database.articleDao().update(updatedArticle.asDatabaseModel())
            withContext(Dispatchers.Main) {
                _detailStatus.postValue(GuardianApiStatus.SUCCESS)
            }
            return updatedArticle
        } catch (exception: Exception) {
            withContext(Dispatchers.Main) {
                _detailStatus.postValue(GuardianApiStatus.ERROR)
            }
            exception.printStackTrace()
        }
        return article
    }
}
