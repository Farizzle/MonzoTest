package com.monzo.androidtest.domain

import android.os.Parcelable
import com.monzo.androidtest.database.model.DBArticle
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Article(
        val id: String,
        val thumbnail: String?,
        val sectionId: String,
        val sectionName: String,
        val published: Date,
        val title: String?,
        val url: String,
        val favourite: Boolean?,
        val body: String?
) : Parcelable {

    val publishedDateForDB: Long
        get() = published.time

    val publishedDateFormatted: String
        get() {
            val date = Date(publishedDateForDB)
            val format = SimpleDateFormat("dd-MM-yyyy HH:mm")
            return format.format(date)
        }

    fun asDatabaseModel(): DBArticle {
        return DBArticle(
                id = id,
                thumbnail = thumbnail,
                sectionId = sectionId,
                sectionName = sectionName,
                published = publishedDateForDB,
                title = title,
                url = url,
                favourite = favourite,
                body = body
        )
    }
}
