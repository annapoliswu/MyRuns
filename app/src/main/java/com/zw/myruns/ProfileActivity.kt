package com.zw.myruns

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File


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

    /**
     * safe set for photo using uri, checks if file at uri exists first
     */
    private fun setPhoto(imageName: String, imageUri: Uri){
        if( fileExists(imageName)) {
            photoView.setImageBitmap(Util.getBitmap(this, imageUri))
        }
    }

    private fun deleteTemp(){
        val tempImgFile = File(getExternalFilesDir(null), tempImgName)
        tempImgFile.delete()
    }

    //button function to branch to launch Camera or Gallery select
    fun onChangePhotoClicked(view: View){
        var intent : Intent
        val photoDialogOptions = arrayOf("Open Camera", "Select from Gallery")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Profile Picture")
            .setItems(photoDialogOptions)
            { dialog, which ->
                if( photoDialogOptions[which].equals("Open Camera") ) {
                    intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
                    camResultLauncher.launch(intent)
                }else{
                    intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryResultLauncher.launch(intent)
                }
            }
        val photoDialog = builder.create()
        photoDialog.show()

    }

    //on get photo result returned, set the profile picture
    private val camResultLauncher :  ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                setPhoto(tempImgName, tempImgUri)
            }
    }
    private val galleryResultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                val intentData = result.data
                val uri = intentData?.data!!
                Util.writeBitmap(this, tempImgName, uri, 180f)
                setPhoto(tempImgName, tempImgUri)
            }
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

        val gender = sharedPref.getString("gender_key", defaultData)
        when {
            gender.equals("Female") -> genderView.check(R.id.profile_female)
            gender.equals("Male") -> genderView.check(R.id.profile_male)
        }
    }

    //saves the profile in SharedPreference object
    private fun saveProfile(){
        if(fileExists(tempImgName)) {
            Util.writeBitmap(this, profileImgName, tempImgUri, -90f)
        }

        val name : String = nameView.text.toString()
        val email : String = emailView.text.toString()
        val phoneNumber : String = phoneView.text.toString()
        val classYear : String = classView.text.toString()
        val major : String = majorView.text.toString()

        var gender = ""
        val genderID = genderView.checkedRadioButtonId
        if(genderID != -1){
            val genderButton : RadioButton = findViewById(genderID)
            gender = genderButton.text.toString()
        }

        //TODO: companion object??
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