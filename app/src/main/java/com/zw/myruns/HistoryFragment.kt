package com.zw.myruns

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

// History of entries shown via here
class HistoryFragment : Fragment() {

    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var viewModelFactory: ExerciseViewModelFactory
    private lateinit var exerciseViewModel: ExerciseViewModel

    private lateinit var historyListView: ListView
    private lateinit var arrayList: ArrayList<ExerciseEntry>
    private lateinit var arrayAdapter: HistoryListAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        //Database setup
        database = ExerciseDatabase.getInstance(requireActivity()) //dc is initialized and connects with DAO here?
        databaseDao = database.exerciseDatabaseDao
        viewModelFactory = ExerciseViewModelFactory(databaseDao)
        exerciseViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(ExerciseViewModel::class.java)

        historyListView = view.findViewById(R.id.history_list)
        arrayList = ArrayList()
        arrayAdapter = HistoryListAdapter(requireActivity(), arrayList)
        historyListView.adapter = arrayAdapter

        exerciseViewModel.allEntries.observe(requireActivity(), Observer{ changedList ->
            arrayAdapter.replace(changedList)
            arrayAdapter.notifyDataSetChanged()
            Log.d("ViewModel", "Observed Entry Change")
        })


        return view
    }



}