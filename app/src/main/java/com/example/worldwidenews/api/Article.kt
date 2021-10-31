package com.example.worldwidenews.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


//setup this class to be ready use our database ,local database

// to tell android this article class is a table in our database
@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
):Serializable