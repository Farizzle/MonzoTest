package com.monzo.androidtest.api

import com.monzo.androidtest.api.model.ApiArticleListResponse
import com.monzo.androidtest.api.model.ApiArticleResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

//Todo
// Only to combat the initial delay on first time launch, as now we have a local cached repository
// I would have an View which is either visible/hidden based on the status so the user is aware
// that some network request is occurring. Or in a worst case scenario they are alerted if there is
// a HTTPException
enum class GuardianApiStatus {
    LOADING, SUCCESS, ERROR
}

interface GuardianService {
    //Todo
    // Add pagination to this method + filters
    @GET("search?show-fields=headline,thumbnail&page-size=50")
    fun searchArticlesAsync(@Query("q") searchTerm: String?): Deferred<ApiArticleListResponse>

    @GET
    fun getArticleAsync(
            @Url articleUrl: String,
            @Query("show-fields") fields: String
    ): Deferred<ApiArticleResponse>
}
