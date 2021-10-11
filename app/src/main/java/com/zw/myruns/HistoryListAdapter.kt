package com.zw.myruns

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.content.Intent
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round


/**
 * Specifies how each ExerciseEntry list item in HistoryFragment list will look
 */
class HistoryListAdapter (val context: Context, var entriesList: List<ExerciseEntry>) : BaseAdapter(){
    override fun getCount(): Int {
        return entriesList.size
    }

    override fun getItem(position: Int): Any {
        return entriesList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.layout_history_list_adapter, null)
        val listItemTitle = view.findViewById<TextView>(R.id.entry_title) as TextView
        val listItemDetail = view.findViewById<TextView>(R.id.entry_detail) as TextView

        //Get units settings from preferences
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.settings_preference_key),
            Context.MODE_PRIVATE
        )
        val units = sharedPref.getString("units_key", context.getString(R.string.default_units))
        val comment = sharedPref.getString("comments_key", "No comment")
        val postAnonymously = sharedPref.getBoolean("privacy_key", true)


        val listItem = entriesList.get(position)

        //Entry duration stored in miles. If not prefer to display in miles, convert to km
        val distanceInUnitsPreferred = when {
            units.equals("Miles") -> {
                listItem.distance
            }
            else -> {
                Util.milesToKm(listItem.distance)
            }
        }
        val minutes = Util.getMinutes(listItem.duration)
        val seconds = Util.getSeconds(listItem.duration)

        listItemTitle.text = "${listItem.inputType}: ${listItem.activityType}"
        listItemDetail.text = "${listItem.dateTime}\n${String.format("%.2f", distanceInUnitsPreferred)} ${units!!.lowercase()}, ${minutes} min ${seconds} sec"


        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(context, ExerciseDisplayActivity::class.java)
                intent.putExtra("entry_id", listItem.id)
                context.startActivity(intent)
            }
        })

        return view
    }


    fun replace( newList : List<ExerciseEntry>){
        entriesList = newList
    }

}