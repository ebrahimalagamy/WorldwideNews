package com.example.worldwidenews.api

import com.example.worldwidenews.api.Article

data class NewsResponse(

    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)