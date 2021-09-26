package com.zw.myruns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun onSaveClicked(view :View){
        finish()
    }
    fun onCancelClicked(view: View){
        finish()
    }
}