package app.netlify.dev4rju9.kshatriyakulavatans.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen.AddSourceUiState
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {

    @Query("SELECT * FROM sources ORDER BY timestamp DESC")
    fun getAllSources(): Flow<List<AddSourceUiState>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: AddSourceUiState)

    @Update
    suspend fun updateSource(source: AddSourceUiState)

    @Delete
    suspend fun deleteSource(source: AddSourceUiState)

    @Query("DELETE FROM sources")
    suspend fun deleteAllSources()

}