package com.zw.myruns

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import java.util.*


//TODO: Add more comments

class ManualActivity : AppCompatActivity() {
    private lateinit var listView : ListView
    private val ENTRY_ITEMS = arrayOf(
        "Date",
        "Time",
        "Duration",
        "Distance",
        "Calories",
        "Heart Rate",
        "Comment"
    )
    private lateinit var durationEditText: EditText
    private var duration = 0.0
    private var distance = 0.0
    private var calories = 0
    private var heartRate = 0
    private var comment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)
        listView = findViewById(R.id.manualListView)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val dateDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, dateYear, monthOfYear, dayOfMonth ->
                println(
                    "${monthOfYear + 1}/$dayOfMonth/$dateYear"
                )
            },
            year,
            month,
            day
        )


        //TODO: some crash with this, ask TA??
        val timeDialog: TimePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                println(
                    "${hour}:${minute}"
                )
            },
            hour,
            minute,
            false
        )


        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            ENTRY_ITEMS
        )
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener(){ parent: AdapterView<*>, view: View, position: Int, id: Long ->
            when(position){
                0 -> { dateDialog.show() }
                1 -> { timeDialog.show() }
                2 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL) { str -> duration = str.toDouble() } }
                3 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL) { str -> distance = str.toDouble() } }
                4 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER) { str -> calories = str.toInt() } }
                5 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER) { str -> heartRate = str.toInt() } }
                6 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_TEXT, "Note how your activity went") { str -> comment = str } }
            }
        }

    }


    /**
     * for ease of making multiple input dialogs
     */
    private fun makeDialog (title:String, inputType : Int, hint : String, func : (input: String) -> Unit){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        val input = EditText(this)
        input.setPadding(60)
        input.inputType = inputType
        input.setHint(hint)
        builder.setView(input)

        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which -> func(input.text.toString()) })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }
    private fun makeDialog (title:String, inputType : Int, func : (input: String) -> Unit) {
        makeDialog(title, inputType, "", func)
    }


    fun onSaveClicked(view: View){
        println("duration: $duration")
        finish()
    }
    fun onCancelClicked(view: View){
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}