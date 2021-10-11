package com.zw.myruns

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlin.properties.Delegates

/**
 * Activity that pops up onClick for every ExerciseEntry in the History list
 * Shows all information for ExerciseEntry in textviews
 */
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

        //replace toolbar
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.exercise_display_toolbar)
        setSupportActionBar(toolbar)

        //get text views
        inputTypeTV = findViewById(R.id.inputTypeTV)
        activityTypeTV = findViewById(R.id.activityTypeTV)
        dateTimeTV = findViewById(R.id.dateTimeTV)
        durationTV = findViewById(R.id.durationTV)
        distanceTV = findViewById(R.id.distanceTV)
        caloriesTV = findViewById(R.id.caloriesTV)
        heartRateTV = findViewById(R.id.heartRateTV)
        commentTV = findViewById(R.id.commentTV)

        //get database instance
        val database = ExerciseDatabase.getInstance(this) //dc is initialized and connects with DAO here?
        val databaseDao = database.exerciseDatabaseDao
        val viewModelFactory = ExerciseViewModelFactory(databaseDao)
        exerciseViewModel = ViewModelProvider(this, viewModelFactory).get(ExerciseViewModel::class.java)

        //format data to display and set textViews
        val extras = intent.extras
        if(extras != null){
            id = extras.getLong("entry_id")
            exerciseViewModel.allEntries.observe(this, Observer{ changedList ->  //NOTE: HAVE to access list through observe
                val entry = changedList.find { ee -> ee.id == id }

                val sharedPref = getSharedPreferences(
                    getString(R.string.settings_preference_key),
                    Context.MODE_PRIVATE
                )
                val units = sharedPref.getString("units_key", getString(R.string.default_units))

                if (entry != null) {
                    val distanceInUnitsPreferred = when {
                        units.equals("Miles") -> {
                            entry.distance
                        }
                        else -> {
                            Util.milesToKm(entry.distance)
                        }
                    }

                    inputTypeTV.text = entry.inputType
                    activityTypeTV.text = entry.activityType
                    dateTimeTV.text = entry.dateTime
                    durationTV.text = "${Util.getMinutes(entry.duration)} min, ${Util.getSeconds(entry.duration)} sec"
                    distanceTV.text = "${String.format("%.2f", distanceInUnitsPreferred)} ${units!!.lowercase()}"
                    caloriesTV.text = "${entry.calories} cals"
                    heartRateTV.text = "${entry.heartRate} bpm"
                    commentTV.text = entry.comment
                }
            })
        }


    }

    fun onDeleteClicked(view: View){
        exerciseViewModel.delete(id)
        val toast = Toast.makeText(this, "Entry #${id} deleted", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    fun onBackClicked(view: View){
        finish()
    }


}