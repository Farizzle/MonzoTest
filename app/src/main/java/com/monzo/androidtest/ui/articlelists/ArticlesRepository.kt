package com.monzo.androidtest.ui.articlelists

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.monzo.androidtest.api.GuardianApiStatus
import com.monzo.androidtest.api.GuardianService
import com.monzo.androidtest.api.model.asDatabaseModel
import com.monzo.androidtest.database.ArticleDatabase
import com.monzo.androidtest.database.model.DBSectionType
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

    val sections = database.articleDao().getSections()

    suspend fun getLatestArticlesList(searchTerm: String?, section: String?, currentPage: Int = 1) {
        _feedStatus.postValue(GuardianApiStatus.LOADING)
        Log.e("FOORIS", "CURRENT PAGE - $currentPage")
        var sectionId = section
        var searchQuery = searchTerm
        if (section.isNullOrBlank()) {
            sectionId = null
        }
        if (searchTerm.isNullOrBlank()) {
            searchQuery = null
        }
        withContext(Dispatchers.IO) {
            try {
                val articleResponse = guardianService.searchArticlesAsync(searchQuery, sectionId, currentPage).await()
                withContext(Dispatchers.Main) {
                    Log.e("FOORIS", "PAYLOAD SIZE - ${articleResponse.response.results.size}")
                    _feedStatus.postValue(GuardianApiStatus.SUCCESS)
                }
                var articleSections = mutableSetOf<DBSectionType>()
                for (article in articleResponse.response.results) {
                    articleSections.add(DBSectionType(article.sectionId, article.sectionName))
                }
                database.articleDao().insertAllSections(*articleSections.toTypedArray())
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
