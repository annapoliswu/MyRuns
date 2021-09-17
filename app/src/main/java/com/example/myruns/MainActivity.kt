package com.example.myruns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

//don't need to crop image for us

class MainActivity : AppCompatActivity() {

    private lateinit var nameView: TextView
    private lateinit var emailView: TextView
    private lateinit var phoneView: TextView
    private lateinit var genderView: RadioGroup
    private lateinit var classView: TextView
    private lateinit var majorView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        nameView = findViewById(R.id.profile_name)
        emailView = findViewById(R.id.profile_email)
        phoneView = findViewById(R.id.profile_phone_number)
        genderView = findViewById(R.id.profile_gender)
        classView = findViewById(R.id.profile_class)
        majorView = findViewById(R.id.profile_major)
    }

    fun onChangePhotoClicked(view: View){

    }

    fun onSaveClicked(view: View) {
        saveProfile()
        val toast = Toast.makeText(this, "Saved", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    fun onCancelClicked(view: View) {
        val toast = Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }
    private fun loadProfile() {
        // help load user data that has already been saved
        //Your helper function calls loadProfile( ) method in onCreate() and uses the same SharedPreference object to load the data and display it to the screen.
        //first time that the app runs when no previous data is saved. need to make sure some default data (e.g., empty string elements) are displayed.
    }
    private fun saveProfile(){
        //this function saves the user input data using a SharedPreference object.
        var name : String = nameView.text.toString()
        var email : String = emailView.text.toString()
        var phoneNumber : String = phoneView.text.toString()
        var classYear : String = classView.text.toString()
        var major : String = majorView.text.toString()

        var gender = ""
        var genderID = genderView.checkedRadioButtonId
        if(genderID != -1){
            var genderButton : RadioButton = findViewById(genderID)
            gender = genderButton.text.toString()
        }

        println(name + "\n" + email + "\n" + phoneNumber + "\n" + gender + "\n" + classYear + "\n" + major)
    }

}