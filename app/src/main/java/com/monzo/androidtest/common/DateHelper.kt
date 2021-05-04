package com.monzo.androidtest.common

import com.monzo.androidtest.data.domain.Article
import java.util.*

object DateHelper {
    private val oneWeek: Long by lazy {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        calendar.time.time
    }

    private val twoWeeks: Long by lazy {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -14)
        calendar.time.time
    }

    fun articleForThisWeek(article: Article): Boolean {
        if (article.publishedDateForDB <= Date().time && article.publishedDateForDB >= oneWeek) {
            return true
        }
        return false
    }

    fun articleForLastWeek(article: Article): Boolean {
        if (article.publishedDateForDB in twoWeeks..oneWeek) {
            return true
        }
        return false
    }

    fun oldArticle(article: Article): Boolean {
        if (article.publishedDateForDB <= twoWeeks) {
            return true
        }
        return false
    }
}