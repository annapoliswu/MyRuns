package com.zw.myruns

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


//NOTE don't need to crop image for this class

class ProfileActivity : AppCompatActivity() {

    private lateinit var photoView: ImageView
    private lateinit var nameView: TextView
    private lateinit var emailView: TextView
    private lateinit var phoneView: TextView
    private lateinit var genderView: RadioGroup
    private lateinit var classView: TextView
    private lateinit var majorView: TextView

    private val tempImgName = "temp_img.jpg"
    private lateinit var tempImgUri: Uri
    private val profileImgName = "profile_img.jpg"
    private lateinit var profileImgUri: Uri

    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //find views
        photoView = findViewById(R.id.profile_photo)
        nameView = findViewById(R.id.profile_name)
        emailView = findViewById(R.id.profile_email)
        phoneView = findViewById(R.id.profile_phone_number)
        genderView = findViewById(R.id.profile_gender)
        classView = findViewById(R.id.profile_class)
        majorView = findViewById(R.id.profile_major)

        //get photo storage locations
        val tempImgFile = File(getExternalFilesDir(null), tempImgName)
        tempImgUri = FileProvider.getUriForFile(this, "com.zw.myruns", tempImgFile)
        val profileImgFile = File(getExternalFilesDir(null), profileImgName)
        profileImgUri = FileProvider.getUriForFile(this, "com.zw.myruns", profileImgFile)


        //load saved profile from sharedPref
        sharedPref = this.getSharedPreferences(
                getString(R.string.profile_preference_key),
                Context.MODE_PRIVATE
        )
        loadProfile()

        //for config changes
        if (savedInstanceState != null) {
            setPhoto(tempImgName, tempImgUri)
        }
    }


    private fun fileExists(imageName: String):Boolean {
        return File(getExternalFilesDir(null), imageName).exists()
    }

    //safe set for photo using uri, checks if file at uri exists first
    private fun setPhoto(imageName: String, imageUri: Uri){
        if( fileExists(imageName)) {
            photoView.setImageBitmap(Util.getBitmap(this, imageUri))
        }
    }

    private fun deleteTemp(){
        val tempImgFile = File(getExternalFilesDir(null), tempImgName)
        tempImgFile.delete()
    }

    fun onChangePhotoClicked(view: View){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
        activityResultLauncher.launch(intent)
    }
    private val activityResultLauncher :  ActivityResultLauncher<Intent> = registerForActivityResult(
            //parameters: contract (type input needed and type for result)
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult -> setPhoto(tempImgName, tempImgUri)
    }

    fun onSaveClicked(view: View) {
        saveProfile()
        deleteTemp()
        val toast = Toast.makeText(this, "Saved", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    fun onCancelClicked(view: View) {
        deleteTemp()
        val toast = Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    //loads saved profile data. if profile section empty, uses empty string
    private fun loadProfile() {
        setPhoto(profileImgName, profileImgUri)

        val defaultData = ""
        nameView.text = sharedPref.getString("name_key", defaultData)
        emailView.text = sharedPref.getString("email_key", defaultData)
        phoneView.text = sharedPref.getString("phone_key", defaultData)
        classView.text = sharedPref.getString("class_key", defaultData)
        majorView.text = sharedPref.getString("major_key", defaultData)

        var gender = sharedPref.getString("gender_key", defaultData)
        when {
            gender.equals("Female") -> genderView.check(R.id.profile_female)
            gender.equals("Male") -> genderView.check(R.id.profile_male)
        }
    }

    //saves the profile in SharedPreference object
    private fun saveProfile(){
        if(fileExists(tempImgName)) {
            val profileImgFile = File(getExternalFilesDir(null), profileImgName)
            val fOut = FileOutputStream(profileImgFile)
            val bitmap = Util.getBitmap(this, tempImgUri)
            val matrix = Matrix()
            matrix.setRotate(-90f)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
        }

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

        with(sharedPref.edit()) {
            putString("name_key", name)
            putString("email_key", email)
            putString("phone_key", phoneNumber)
            putString("gender_key", gender)
            putString("class_key", classYear)
            putString("major_key", major)
            apply()
        }
        //println("SAVED: " + name + "\n" + email + "\n" + phoneNumber + "\n" + gender + "\n" + classYear + "\n" + major)
    }



}