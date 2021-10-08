package com.zw.myruns

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Data access object for database that maps SQL commands to functions
 */
@Dao
interface ExerciseDatabaseDao {

    @Insert
    suspend fun insertEntry(exerciseEntry: ExerciseEntry)

    @Query("SELECT * FROM exercise_table")
    fun getAllEntries(): Flow<List<ExerciseEntry>>
    //Flow object will be updated automatically as soon as changed, return update auto updated

    @Query("DELETE FROM exercise_table")
    suspend fun deleteAllEntries()

    @Query("DELETE FROM exercise_table WHERE id = :key")
    suspend fun deleteEntry(key: Long)

}