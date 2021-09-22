package com.zw.myruns

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

        inputTypeSpinner = view.findViewById(R.id.inputTypeSpinner)
        activityTypeSpinner = view.findViewById(R.id.activityTypeSpinner)

        inputTypeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                Log.d("Input", parent.getItemAtPosition(position) as String)
                // TODO input spinner item select
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // TODO input spinner nothing select
            }
        })

        inputTypeAdapter = ArrayAdapter<String>(requireContext() , android.R.layout.simple_spinner_item, inputTypes)
        inputTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        inputTypeSpinner.adapter = inputTypeAdapter



        activityTypeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                Log.d("Activity", parent.getItemAtPosition(position) as String)
                // TODO activity spinner item select
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // TODO activity spinner nothing select
            }
        })

        activityTypeAdapter = ArrayAdapter<String>(requireContext() , android.R.layout.simple_spinner_item, activityTypes)
        activityTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        activityTypeSpinner.adapter = activityTypeAdapter

        startButton = view.findViewById(R.id.startButton)
        syncButton = view.findViewById(R.id.syncButton)
        //TODO: start/sync button listeners

        return view
    }

    fun onStartClicked(view: View){
        Log.d("Button", "Start pushed")
    }

    fun onSyncClicked(view: View){
        Log.d("Button", "Sync pushed")
    }

}