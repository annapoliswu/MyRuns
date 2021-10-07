package com.zw.myruns

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/*
 * Mainly for listview of manual activity.
 * Communicates with viewpager2 and tells how to display fragments, FragmentActivity because could not always be main activity
 */
class ArrayListFragmentStateAdapter(activity: FragmentActivity, var list: ArrayList<Fragment>) : FragmentStateAdapter(activity){

    //2 functions will be called automatically
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
    override fun getItemCount(): Int {
        return list.size
    }

}
