package com.example.myruns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun onSave(view: View) {
        val toast = Toast.makeText(this, "Saved", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    fun onCancel(view: View) {
        val toast = Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

}