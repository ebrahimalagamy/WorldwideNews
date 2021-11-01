package com.example.worldwidenews.api

import com.example.worldwidenews.api.Article

data class NewsResponse(

    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)