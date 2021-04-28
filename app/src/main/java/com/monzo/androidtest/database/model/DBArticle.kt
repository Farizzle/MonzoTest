package com.monzo.androidtest.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.monzo.androidtest.domain.Article
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "article_table")
@Parcelize
data class DBArticle(
        @PrimaryKey val id: String,
        val thumbnail: String?,
        val sectionId: String,
        val sectionName: String,
        val published: Long,
        val title: String?,
        val url: String,
        val favourite: Boolean?,
        val body: String?
) : Parcelable {

    val createdDateFormatted: Date
        get() {
            return Date(published)
        }

    fun asDomainModel(): Article = Article(
            id = id,
            thumbnail = thumbnail,
            sectionId = sectionId,
            sectionName = sectionName,
            published = createdDateFormatted,
            title = title,
            url = url,
            favourite = favourite,
            body = body
    )

}

fun List<DBArticle>.asDomainModel(): List<Article> {
    return map {
        Article(
                id = it.id,
                thumbnail = it.thumbnail,
                sectionId = it.sectionId,
                sectionName = it.sectionName,
                published = it.createdDateFormatted,
                title = it.title,
                url = it.url,
                favourite = it.favourite,
                body = it.body
        )
    }
}