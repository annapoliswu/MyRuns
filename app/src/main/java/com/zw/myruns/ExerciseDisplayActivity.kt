package com.zw.myruns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlin.properties.Delegates

class ExerciseDisplayActivity : AppCompatActivity() {

    lateinit var inputTypeTV: TextView
    lateinit var activityTypeTV: TextView
    lateinit var dateTimeTV: TextView
    lateinit var durationTV: TextView
    lateinit var distanceTV: TextView
    lateinit var caloriesTV: TextView
    lateinit var heartRateTV: TextView
    lateinit var commentTV: TextView

    lateinit var exerciseViewModel : ExerciseViewModel
    var id by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_display)

        inputTypeTV = findViewById(R.id.inputTypeTV)
        activityTypeTV = findViewById(R.id.activityTypeTV)
        dateTimeTV = findViewById(R.id.dateTimeTV)
        durationTV = findViewById(R.id.durationTV)
        distanceTV = findViewById(R.id.distanceTV)
        caloriesTV = findViewById(R.id.caloriesTV)
        heartRateTV = findViewById(R.id.heartRateTV)
        commentTV = findViewById(R.id.commentTV)

        val database = ExerciseDatabase.getInstance(this) //dc is initialized and connects with DAO here?
        val databaseDao = database.exerciseDatabaseDao
        val viewModelFactory = ExerciseViewModelFactory(databaseDao)
        exerciseViewModel = ViewModelProvider(this, viewModelFactory).get(ExerciseViewModel::class.java)

        val extras = intent.extras
        if(extras != null){
            id = extras.getLong("entry_id")
            exerciseViewModel.allEntries.observe(this, Observer{ changedList ->  //NOTE: HAVE to access list through observe
                val entry = changedList.find { ee -> ee.id == id }
                if (entry != null) {
                    inputTypeTV.text = entry.inputType
                    activityTypeTV.text = entry.activityType
                    dateTimeTV.text = entry.dateTime
                    (entry.duration.toString() + " minutes").also { durationTV.text = it } //TODO: minute fraction
                    (entry.distance.toString() + " miles").also { distanceTV.text = it } //TODO: miles/km conversion
                    (entry.calories.toString() + " cals").also { caloriesTV.text = it }
                    (entry.heartRate.toString() + " bpm").also { heartRateTV.text = it }
                    commentTV.text = entry.comment
                }
            })
        }


    }

    fun onDeleteClicked(view: View){
        exerciseViewModel.delete(id)
        val toast = Toast.makeText(this, "Deleted entry #${id}", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    fun onBackClicked(view: View){
        finish()
    }



}