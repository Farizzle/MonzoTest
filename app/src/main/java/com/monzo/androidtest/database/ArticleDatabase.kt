package com.monzo.androidtest.database

import androidx.room.*
import com.monzo.androidtest.database.model.DBArticle
import kotlinx.coroutines.flow.Flow

@Database(entities = [DBArticle::class], version = 1, exportSchema = false)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg articles: DBArticle)

    @Update
    suspend fun update(article: DBArticle)

    @Query("SELECT * FROM article_table WHERE (title LIKE '%' || :query || '%') AND (favourite == :favourites) ORDER BY published DESC LIMIT 30")
    fun getArticlesByQuery(query: String, favourites: Boolean): Flow<List<DBArticle>>

    @Query("SELECT * FROM article_table ORDER BY published DESC")
    fun getArticles(): Flow<List<DBArticle>>

    @Query("SELECT * FROM article_table WHERE (title LIKE '%' || :query || '%') AND (favourite == 1) ORDER BY published DESC LIMIT 5")
    fun getFavouriteArticles(query: String?): Flow<List<DBArticle>>

    @Query("SELECT * FROM article_table WHERE id == :articleId")
    fun getSingleArticle(articleId: String): Flow<DBArticle>
}
