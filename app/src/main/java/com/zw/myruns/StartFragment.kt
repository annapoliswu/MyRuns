package com.zw.myruns

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceScreen

/*  Fragment for deciding type of entry input: GPS, Automatic, Manual
 *  Exercise entry data are returned from these activities and writes to the database are all handled here
 */
class StartFragment : Fragment() {

    private lateinit var inputTypeSpinner : Spinner
    private lateinit var inputTypes :  Array<String>
    private lateinit var inputTypeAdapter : ArrayAdapter<String>

    private lateinit var activityTypeSpinner : Spinner
    private lateinit var activityTypes :  Array<String>
    private lateinit var activityTypeAdapter : ArrayAdapter<String>

    private lateinit var startButton : Button
    private lateinit var syncButton : Button
    private lateinit var resultLauncher : ActivityResultLauncher<Intent>

    private lateinit var exerciseViewModel : ExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputTypes = resources.getStringArray(R.array.inputTypes)
        activityTypes = resources.getStringArray(R.array.activityTypes)

        //keeping track of last thing added
        val sharedPref = requireContext().getSharedPreferences(
            getString(R.string.last_id_saved_key),
            Context.MODE_PRIVATE
        )

        //database setup
        val database = ExerciseDatabase.getInstance(requireContext())
        val databaseDao = database.exerciseDatabaseDao
        val viewModelFactory = ExerciseViewModelFactory(databaseDao)
        exerciseViewModel = ViewModelProvider(this , viewModelFactory).get(ExerciseViewModel::class.java)


        //takes intent from other activities and posts a ExerciseEntry to database
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent = result.data!!
                val entry = Util.getEntryFromIntent(intent)
                exerciseViewModel.insert(entry)

                var lastid = sharedPref.getLong("last_id_saved_key", 0L).plus(1)
                sharedPref.edit().putLong("last_id_saved_key", lastid).commit()

                val toast = Toast.makeText(context, "Entry #${lastid} added", Toast.LENGTH_SHORT)
                toast.show()
                println("Got result with intent, in HistoryListAdapter")
            }
        }



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_start, container, false)

        //Dropdown setup
        inputTypeSpinner = view.findViewById(R.id.inputTypeSpinner)
        inputTypeAdapter = ArrayAdapter<String>(requireContext() , android.R.layout.simple_spinner_item, inputTypes)
        inputTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        inputTypeSpinner.adapter = inputTypeAdapter

        activityTypeSpinner = view.findViewById(R.id.activityTypeSpinner)
        activityTypeAdapter = ArrayAdapter<String>(requireContext() , android.R.layout.simple_spinner_item, activityTypes)
        activityTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        activityTypeSpinner.adapter = activityTypeAdapter

        //Button setups with listeners
        startButton = view.findViewById(R.id.startButton)
        syncButton = view.findViewById(R.id.syncButton)

        startButton.setOnClickListener(View.OnClickListener {

            val inputType = inputTypeSpinner.getSelectedItem().toString()
            val activityType = activityTypeSpinner.getSelectedItem().toString()
            val intent : Intent

            when(inputType){
                "Manual Entry" -> {
                    intent = Intent(context, ManualActivity::class.java)
                }
                "GPS" ->{
                    intent = Intent(context, MapActivity::class.java)
                }
                "Automatic" -> {
                    intent = Intent(context, MapActivity::class.java)
                }
                else -> {
                    println("not an input type")
                    intent = Intent(context, ManualActivity::class.java)
                }
            }


            intent.putExtra("activity_type", activityType)
            intent.putExtra("input_type", inputType)
            resultLauncher.launch(intent)

        })

        syncButton.setOnClickListener(View.OnClickListener {
            //TODO : Sync button listener
        })



        return view
    }

}