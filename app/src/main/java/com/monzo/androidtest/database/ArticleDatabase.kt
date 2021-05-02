package com.monzo.androidtest.database

import androidx.room.*
import com.monzo.androidtest.database.model.DBArticle
import com.monzo.androidtest.database.model.DBSectionType
import kotlinx.coroutines.flow.Flow

@Database(entities = [DBArticle::class, DBSectionType::class], version = 1, exportSchema = false)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg articles: DBArticle)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSections(vararg sections: DBSectionType)

    @Update
    suspend fun update(article: DBArticle)

    @Query("SELECT * FROM article_table WHERE (title LIKE '%' || :query || '%') AND (favourite == :favourites) AND (sectionId LIKE '%' || :section || '%') ORDER BY published DESC LIMIT 30 * :limit")
    fun getArticlesByQuery(query: String, favourites: Boolean, section: String, limit: Int): Flow<List<DBArticle>>

    @Query("SELECT * FROM article_table WHERE id == :articleId")
    fun getSingleArticle(articleId: String): Flow<DBArticle>

    @Query("SELECT * FROM section_table ORDER BY type ASC")
    fun getSections(): Flow<List<DBSectionType>>
}
