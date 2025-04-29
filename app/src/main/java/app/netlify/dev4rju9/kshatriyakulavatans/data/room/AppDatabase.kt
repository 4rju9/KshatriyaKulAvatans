package app.netlify.dev4rju9.kshatriyakulavatans.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen.AddSourceUiState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(entities = [AddSourceUiState::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
}

class Converters {

    @TypeConverter
    fun fromList(value: List<String>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}