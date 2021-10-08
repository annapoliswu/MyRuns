package com.zw.myruns

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

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
        /*
        //for reference
        val view: View = View.inflate(context, R.layout.layout_adapter,null)

        val textViewID = view.findViewById(R.id.tv_number) as TextView
        val textViewComment = view.findViewById(R.id.tv_string) as TextView

        textViewID.text = commentList.get(position).id.toString()
        textViewComment.text = commentList.get(position).comment

        return view

         */
        return convertView!!
    }


    fun replace( newList : List<ExerciseEntry>){
        entriesList = newList
    }
}