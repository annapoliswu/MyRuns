package com.zw.myruns

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.preference.PreferenceFragmentCompat


//NOTE don't need to crop image for this class

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    private val tabNames = arrayOf("Start", "History", "Settings")
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2
    private lateinit var fragments : ArrayList<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Util.checkPermissions(this, PERMISSIONS)

        val startFragment = StartFragment()
        val historyFragment = HistoryFragment()
        val settingsFragment = SettingsFragment()

        fragments = ArrayList<Fragment>()
        fragments.add(startFragment)
        fragments.add(historyFragment)
        fragments.add(settingsFragment)

        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tab)
        val fsa = ArrayListFragmentStateAdapter(this, fragments)
        viewPager.adapter = fsa

        val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy {
            tab, position -> tab.text = tabNames[position]    //sets name of tabs
        }
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }


}