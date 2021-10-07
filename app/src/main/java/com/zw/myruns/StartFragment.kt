package com.zw.myruns

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceScreen

// Fragment for deciding type of entry input: GPS, Automatic, Manual
class StartFragment : Fragment() {

    private lateinit var inputTypeSpinner : Spinner
    private lateinit var inputTypes :  Array<String>
    private lateinit var inputTypeAdapter : ArrayAdapter<String>

    private lateinit var activityTypeSpinner : Spinner
    private lateinit var activityTypes :  Array<String>
    private lateinit var activityTypeAdapter : ArrayAdapter<String>

    private lateinit var startButton : Button
    private lateinit var syncButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputTypes = resources.getStringArray(R.array.inputTypes)
        activityTypes = resources.getStringArray(R.array.activityTypes)
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

            var inputType = inputTypeSpinner.getSelectedItem().toString()
            var activityType = activityTypeSpinner.getSelectedItem().toString()
            var intent : Intent

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
            intent.putExtra("ACTIVITY_TYPE", activityType)
            startActivity(intent)

        })

        syncButton.setOnClickListener(View.OnClickListener {
            //TODO : Sync button listener
        })



        return view
    }

}