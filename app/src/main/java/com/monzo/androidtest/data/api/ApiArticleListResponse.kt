package com.monzo.androidtest.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiArticleListResponse(val response: ApiArticleList)

@JsonClass(generateAdapter = true)
data class ApiArticleList(val results: List<ApiArticle>)


@JsonClass(generateAdapter = true)
data class ApiArticleResponse(val response: ApiArticleSingle)

@JsonClass(generateAdapter = true)
data class ApiArticleSingle(val content: ApiArticle)
