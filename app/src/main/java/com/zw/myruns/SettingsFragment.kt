package com.zw.myruns

import android.content.Context
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
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

// For display of various other settings
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val manager: PreferenceManager = preferenceManager
        manager.sharedPreferencesName = getString(R.string.settings_preference_key)
        //manager.setSharedPreferencesMode(Context. )
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }


    /*
    //PreferenceFrag MUST INFLATE OWN VIEW OR CRASHES
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }
    */

}