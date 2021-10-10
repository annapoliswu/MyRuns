package com.zw.myruns

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.preference.PreferenceFragmentCompat
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback


// Activity that shows on startup, handles the 3 tabs
class MainActivity : AppCompatActivity() {

    private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    private val tabNames = arrayOf("Start", "History", "Settings")
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2
    private lateinit var fragments : ArrayList<Fragment>
    private lateinit var tabLayoutMediator : TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Util.checkPermissions(this, PERMISSIONS)

        //Setup fragments for tabs
        val startFragment = StartFragment()
        val historyFragment = HistoryFragment()
        val settingsFragment = SettingsFragment()

        fragments = ArrayList<Fragment>()
        fragments.add(startFragment)
        fragments.add(historyFragment)
        fragments.add(settingsFragment)

        //Tabs setup
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tab)
        val fsa = ArrayListFragmentStateAdapter(this, fragments) //adapter tells viewpager how to render data
        viewPager.adapter = fsa

        //to make sure tabs update for live data changes from other tabs
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                fsa.notifyItemChanged(position)
            }
        })

        val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy {
            tab, position -> tab.text = tabNames[position]    //set names of tabs
        }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager, tabConfigurationStrategy) //wire together
        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }

    override fun onPause() {
        super.onPause()
    }

}