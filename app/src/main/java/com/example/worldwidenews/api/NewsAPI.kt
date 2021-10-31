package com.example.worldwidenews.api

import com.example.worldwidenews.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// use to define our single request that we can execute from code
interface NewsAPI {

    // get all the breakingNews from API inside (we need specify URL that we want to get data from)
    @GET("v2/top-headlines")

    // after this we need create fun that gets our breakingNews
    // this is a network call fun we want execute that fun asynchronously
    // best way to do this using coroutine
    suspend fun getBreakingNews(
        // we want specify from which country we went to get breakingNews
        // and the country is a parameter from our request
        @Query("country")
        countryCode: String = "us",
        // the query will late helpful to paginate our request because we don't get all
        // the breakingNews at ones because that would to much data at ones
        // instead we only get 20 articles at ones and if we want to get the next 20
        // we will request the page 2
        @Query("page")
        pageNumber: Int = 1,
        // we need to include API key in that request
        @Query("apiKey")
        apiKey: String = API_KEY

    ): Response<NewsResponse>

    // search for all articles available
    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

}