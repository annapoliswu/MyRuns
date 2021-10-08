package com.zw.myruns
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *  For Room to create instance of the database
 */
@Database(entities = [ExerciseEntry::class], version = 1)
abstract class ExerciseDatabase : RoomDatabase() {

    abstract val exerciseDatabaseDao: ExerciseDatabaseDao
    companion object{
        @Volatile
        private var INSTANCE: ExerciseDatabase? = null

        //fun to make instance of database
        fun getInstance(context: Context) : ExerciseDatabase{
            synchronized(this){     //ensure only 1 thread access and can create instance of database at time
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        ExerciseDatabase::class.java, "exercise_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }

}