package com.zw.myruns

import android.app.Activity
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
import androidx.lifecycle.ViewModelProvider
import java.time.DayOfWeek
import java.time.MonthDay
import java.time.YearMonth
import java.util.*
import kotlin.properties.Delegates


//Manual input for exercise entry

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
    private lateinit var calendar : Calendar
    private lateinit var pickedCalendar: Calendar

    private var duration = 0F
    private var distance = 0F
    private var calories = 0
    private var heartRate = 0
    private var comment = ""

    private lateinit var inputType : String
    private lateinit var activityType : String

    private lateinit var dateDialog: DatePickerDialog
    private lateinit var timeDialog: TimePickerDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)
        listView = findViewById(R.id.manualListView)

        val extras = intent.extras
        if(extras != null){
            activityType = extras.getString("activity_type", "")
            inputType = extras.getString("input_type", "")
        }

        //get current system date for picker dialogs
        calendar = Calendar.getInstance()
        pickedCalendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        dateDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, dateYear, monthOfYear, dayOfMonth ->
                pickedCalendar.set(dateYear, monthOfYear, dayOfMonth)
            },
            year,
            month,
            day
        )
        timeDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view, dateHour, dateMinute ->
                pickedCalendar.set(Calendar.HOUR, dateHour)
                pickedCalendar.set(Calendar.MINUTE, dateMinute)
            },
            hour,
            minute,
            false
        )


        //programmatically add input popup dialogs to list items
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
                2 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL) { str -> if(str.isNotBlank()){duration = str.toFloat()} } }
                3 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL) { str -> if(str.isNotBlank()){distance = str.toFloat()} }  }
                4 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER) { str -> if(str.isNotBlank()){calories = str.toInt()} }  }
                5 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_NUMBER) { str -> if(str.isNotBlank()){heartRate = str.toInt()} }  }
                6 -> { makeDialog(ENTRY_ITEMS[position], InputType.TYPE_CLASS_TEXT, "Note how your activity went") { str -> comment = str } }
            }
        }
    }



    /**
     * For ease of making multiple input dialogs
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

    // Dialog without hint
    private fun makeDialog (title:String, inputType : Int, func : (input: String) -> Unit) {
        makeDialog(title, inputType, "", func)
    }


    fun onSaveClicked(view: View){
        val dateTime = Util.calendarToString(pickedCalendar)
        val intent = Util.createEntryIntent(inputType, activityType, dateTime, duration, distance, calories, heartRate, comment)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    fun onCancelClicked(view: View){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}