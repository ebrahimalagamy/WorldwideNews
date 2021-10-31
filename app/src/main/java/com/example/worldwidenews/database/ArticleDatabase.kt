package com.example.worldwidenews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.worldwidenews.api.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {
        // other threads can immediately see when the thread changes this instance
        @Volatile
        // create instance from our db which will be our singleton
        // so will only have a single  instance of that database
        private var instance: ArticleDatabase? = null

        // use this to synchronize setting this instance that we really sure that is only a single instance
        // of our db at once
        private val LOCK = Any()

        // that is called whenever we create an instance of our db
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            // ant this happen here can't be accessed by other threads
            instance ?: createDatabase(context).also {
                instance = it
            }


        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}