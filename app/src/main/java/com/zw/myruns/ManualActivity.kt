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
import androidx.core.view.marginLeft
import androidx.core.view.setPadding
import java.util.*


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

        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)

        val dateDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                println(
                    "${monthOfYear + 1}/$dayOfMonth/$year"
                )
            },
            year,
            month,
            day
        )

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
                2 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_NUMBER_FLAG_DECIMAL) { str -> duration = str.toDouble() } }
                3 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_NUMBER_FLAG_DECIMAL) { str -> distance = str.toDouble() } }
                4 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER) { str -> calories = str.toInt() } }
                5 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER) { str -> heartRate = str.toInt() } }
                6 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_TEXT, "Note how your activity went") { str -> comment = str } }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

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

    private fun setDuration(input : String){
        duration = input.toDouble()
    }

    fun onSaveClicked(view: View){
        println("duration: $duration")
        finish()
    }
    fun onCancelClicked(view: View){
        finish()
    }

}