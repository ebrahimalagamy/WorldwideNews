package com.example.worldwidenews.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.worldwidenews.api.Article


// define fun that access our local database (save articles, delete, read..)
@Dao
interface ArticleDao {
    // first fun to insert or update article
    // inConflict ==> determine what happens if that article that we want to inset in db
    // is already exists in or db is this case we want to replace this article
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    // this fun for update or insert
    // long is the id that was inserted
    suspend fun upsert(article: Article):Long

    // we need query that should return all available articles in our db
    @Query("SELECT * from articles")
    // this fun will return livedata object and this not working in suspend fun
    // so we use normal fun
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}