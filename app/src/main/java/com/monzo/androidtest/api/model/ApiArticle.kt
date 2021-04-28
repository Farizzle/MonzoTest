package com.monzo.androidtest.api.model

import com.monzo.androidtest.database.model.DBArticle
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class ApiArticle(
        val id: String,
        val sectionId: String,
        val sectionName: String,
        val webPublicationDate: Date,
        val webTitle: String,
        val webUrl: String,
        val apiUrl: String,
        val fields: ApiArticleFields?
) {
    val publishedDateFormatted: Long
        get() = webPublicationDate.time

    fun asDatabaseModel(): DBArticle {
        return DBArticle(
                id = id,
                thumbnail = fields?.thumbnail,
                sectionId = sectionId,
                sectionName = sectionName,
                published = publishedDateFormatted,
                title = fields?.headline,
                url = apiUrl,
                favourite = false,
                body = fields?.body
        )
    }
}

@JsonClass(generateAdapter = true)
data class ApiArticleFields(
        val headline: String?,
        val main: String?,
        val body: String?,
        val thumbnail: String?
)

fun List<ApiArticle>.asDatabaseModel(): Array<DBArticle> {
    return map {
        DBArticle(
                id = it.id,
                thumbnail = it.fields?.thumbnail,
                sectionId = it.sectionId,
                sectionName = it.sectionName,
                published = it.publishedDateFormatted,
                title = it.fields?.headline,
                url = it.apiUrl,
                favourite = false,
                body = it.fields?.body
        )
    }.toTypedArray()
}
