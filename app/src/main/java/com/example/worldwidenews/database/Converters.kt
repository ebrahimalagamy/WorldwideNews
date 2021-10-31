package com.example.worldwidenews.database

import androidx.room.TypeConverter
import com.example.worldwidenews.api.Source

// we want convert source to string
class Converters {
    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}