package com.monzo.androidtest.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.monzo.androidtest.database.model.DBArticle
import com.monzo.androidtest.database.model.DBSectionType
import com.monzo.androidtest.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ArticleDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ArticleDatabase
    private lateinit var dao: ArticleDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ArticleDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.articleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertArticle() = runBlockingTest {
        val article = DBArticle("test-id",
                "https://imageurl.com",
                "fake-news",
                "Fake News",
                Date().time,
                "This is fake news",
                "https://newsurl.com",
                false,
                null)
        dao.insertAll(article)
        val testArticle = dao.getSingleArticle("test-id").getOrAwaitValue()
        assertThat(testArticle == article)
    }

    @Test
    fun updateArticle() = runBlockingTest {
        val article = DBArticle("test-id",
                "https://imageurl.com",
                "fake-news",
                "Fake News",
                Date().time,
                "This is fake news",
                "https://newsurl.com",
                false,
                null)
        dao.insertAll(article)
        val articleToUpdate = dao.getSingleArticle("test-id").getOrAwaitValue()
        dao.update(articleToUpdate.copy(favourite = true))
        val updatedArticle = dao.getSingleArticle("test-id").getOrAwaitValue()
        if (updatedArticle.id == article.id){
            assertThat(updatedArticle.favourite != article.favourite)
        }
    }

    @Test
    fun getArticlesBySection() = runBlockingTest {
        val politicsArticle = DBArticle("politics-id1",
                "https://imageurl.com",
                "politics",
                "Fake News",
                Date().time,
                "This is fake news",
                "https://newsurl.com",
                false,
                null)
        val politicsArticle2 = DBArticle("politics-id2",
                "https://imageurl.com",
                "politics",
                "Fake News",
                Date().time,
                "This is fake news",
                "https://newsurl.com",
                false,
                null)
        val sportsArticle = DBArticle("sports-id1",
                "https://imageurl.com",
                "sports",
                "Fake News",
                Date().time,
                "This is fake news",
                "https://newsurl.com",
                false,
                null)
        dao.insertAll(politicsArticle, politicsArticle2, sportsArticle)
        val listOfPoliticsArticles = dao.getArticlesByQuery("", false, "politics", 30).asLiveData().getOrAwaitValue()
        assertThat(listOfPoliticsArticles == listOf(politicsArticle, politicsArticle2))
    }

    @Test
    fun getArticlesByFavourite() = runBlockingTest {
        val favourite1 = DBArticle("favourite-id1",
                "https://imageurl.com",
                "fake-news",
                "Fake News",
                Date().time,
                "This is fake news",
                "https://newsurl.com",
                true,
                null)
        val favourite2 = DBArticle("favourite-id2",
                "https://imageurl.com",
                "fake-news",
                "Fake News",
                Date().time,
                "This is fake news",
                "https://newsurl.com",
                true,
                null)
        val nonFavourite1 = DBArticle("nonfavourite-id1",
                "https://imageurl.com",
                "fake-news",
                "Fake News",
                Date().time,
                "This is fake news",
                "https://newsurl.com",
                false,
                null)
        dao.insertAll(favourite1, favourite2, nonFavourite1)
        val listOfPoliticsArticles = dao.getArticlesByQuery("", true, "", 30).asLiveData().getOrAwaitValue()
        assertThat(listOfPoliticsArticles == listOf(favourite1, favourite2))
    }

    @Test
    fun getArticlesByQuery() = runBlockingTest {
        val fakeNews1 = DBArticle("fake-id1",
                "https://imageurl.com",
                "fake-news",
                "Fake News",
                Date().time,
                "This is fake news 1",
                "https://newsurl.com",
                false,
                null)
        val fakeNews2 = DBArticle("fake-id2",
                "https://imageurl.com",
                "fake-news",
                "Fake News",
                Date().time,
                "This is fake news 2",
                "https://newsurl.com",
                false,
                null)
        val realNews1 = DBArticle("real-id1",
                "https://imageurl.com",
                "real-news",
                "Real News",
                Date().time,
                "This is real news",
                "https://newsurl.com",
                false,
                null)
        dao.insertAll(fakeNews1, fakeNews2, realNews1)
        val listOfFakeNews = dao.getArticlesByQuery("fake", false, "", 30).asLiveData().getOrAwaitValue()
        assertThat(listOfFakeNews == listOf(fakeNews1, fakeNews2))
    }

    @Test
    fun insertSections() = runBlockingTest {
        val section1 = DBSectionType("fake-section1", "Fake Section 1")
        val section2 = DBSectionType("fake-section2", "Fake Section 2")
        val section3 = DBSectionType("fake-section3", "Fake Section 3")
        dao.insertAllSections(section1, section2, section3)
        val listOfSections = dao.getSections().asLiveData().getOrAwaitValue()
        assertThat(listOfSections == listOf(section1, section2, section3))
    }

}