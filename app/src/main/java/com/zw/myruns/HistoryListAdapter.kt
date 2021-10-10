package com.zw.myruns

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.content.Intent




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
        //TODO: exercise list adapter view / layout
        val view = View.inflate(context, R.layout.layout_history_list_adapter, null)
        val listItemTitle = view.findViewById<TextView>(R.id.entry_title) as TextView
        val listItemDetail = view.findViewById<TextView>(R.id.entry_detail) as TextView

        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.settings_preference_key),
            Context.MODE_PRIVATE
        )

        val units = sharedPref.getString("units_key", "Miles")
        val comment = sharedPref.getString("comments_key", "No comment")
        val postAnonymously = sharedPref.getBoolean("privacy_key", true)

        val listItem = entriesList.get(position)
        listItemTitle.text = "ID-${listItem.id} INPUT-${listItem.inputType} ACTIVITY-${listItem.activityType}"
        listItemDetail.text = "Units pref test: ${units}"


        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(context, ExerciseDisplayActivity::class.java)
                intent.putExtra("entry_id", listItem.id)
                context.startActivity(intent)
            }
        })



        /*
        //for reference
        val view: View = View.inflate(context, R.layout.layout_adapter,null)

        val textViewID = view.findViewById(R.id.tv_number) as TextView
        val textViewComment = view.findViewById(R.id.tv_string) as TextView

        textViewID.text = commentList.get(position).id.toString()
        textViewComment.text = commentList.get(position).comment

        return view

         */
        return view
    }


    fun replace( newList : List<ExerciseEntry>){
        entriesList = newList
    }
}