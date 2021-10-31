package com.example.worldwidenews.database

import com.example.worldwidenews.api.RetrofitInstance
import com.example.worldwidenews.database.ArticleDatabase

// purpose from repository to get the data from database and our remote data source from retrofit
class NewsRepository(
    val db: ArticleDatabase
) {
    // this fun to get data from API
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    // call the API search fun
    suspend fun searchNews(searchQuery:String,pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

}