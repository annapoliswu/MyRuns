package com.zw.myruns

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Defines how the application will interact with the database
 * Skipping repo, connection straight from dao
 */
class ExerciseViewModel(private val exerciseDatabaseDao: ExerciseDatabaseDao) : ViewModel() {

    val allEntries: LiveData<List<ExerciseEntry>> = exerciseDatabaseDao.getAllEntries().asLiveData()

    fun insert(exercise: ExerciseEntry){
        CoroutineScope(Dispatchers.IO).launch{
            exerciseDatabaseDao.insertEntry(exercise)
        }
    }

    fun delete(id: Long){
        val entriesList = allEntries.value
        if(entriesList != null && entriesList.isNotEmpty() ) {
            CoroutineScope(Dispatchers.IO).launch {
                exerciseDatabaseDao.deleteEntry(id)
            }
        }
    }

    fun deleteAll(){
        CoroutineScope(Dispatchers.IO).launch {
            exerciseDatabaseDao.deleteAllEntries()
        }
    }

}


/**
 * Need factory to create instance of custom Viewmodel class?
 */
class ExerciseViewModelFactory (private val dao: ExerciseDatabaseDao) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(ExerciseViewModel::class.java))
            return ExerciseViewModel(dao) as T //T is Generic
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
