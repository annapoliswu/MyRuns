package com.zw.myruns

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.TypeConverter
import com.google.android.gms.common.util.ArrayUtils.toArrayList
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Bunch of standard utility and conversion functions to use across Activities
 */
object Util {

    //checks an array of permissions and asks user for permission if one is missing
    fun checkPermissions(activity: Activity?, permissions: Array<String>){
        if (Build.VERSION.SDK_INT < 23) return
        var missingPermission : Boolean = false
        for(permission in permissions){
            if( ContextCompat.checkSelfPermission(activity!! , permission) != PackageManager.PERMISSION_GRANTED ){
                missingPermission = true
            }
        }
        if(missingPermission){
            ActivityCompat.requestPermissions( activity!!, permissions, 0)
        }
    }

    //from uri to bitmap with corrected rotation
    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(imgUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val matrix = Matrix()
        matrix.setRotate(90f)
        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        inputStream?.close()
        return newBitmap
    }

    //writes an image at uri to app storage
    fun writeBitmap(context: Context, imgName: String, readUri: Uri, rotation : Float){
        val tempImgFile = File(context.getExternalFilesDir(null), imgName)
        val fOut = FileOutputStream(tempImgFile)
        val bitmap = getBitmap(context, readUri)
        val matrix = Matrix()
        matrix.setRotate(rotation)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).compress(
            Bitmap.CompressFormat.JPEG,
            70,
            fOut
        )
        fOut.flush()
        fOut.close()
    }

    fun calendarToString(cal : Calendar):String{
        val sdf: DateFormat = SimpleDateFormat("MM/dd/yyyy, h:mm a")
        return sdf.format(cal.time)
    }


    //functions for easy exercise entry passing between various map/manual activities and start fragment
    fun createEntryIntent(inputType: String,
                          activityType: String,
                          dateTime : String,
                          duration : Float,
                          distance : Float,
                          calories : Int,
                          heartRate : Int,
                          comment: String,
                          climb : Float,
                          avgPace : Float,
                          avgSpeed : Float,
                          locations : ArrayList<LatLng> ): Intent {

        val intent = Intent()
        val bundle = createEntryBundle(inputType, activityType, dateTime, duration, distance, calories, heartRate, comment, climb, avgPace, avgSpeed, locations)
        intent.putExtras(bundle)
        return intent
    }


    fun createEntryIntent(
        inputType: String,
        activityType: String,
        dateTime: String,
        duration: Float,
        distance: Float,
        calories: Int,
        heartRate: Int,
        comment: String) : Intent{

        return createEntryIntent(inputType, activityType, dateTime, duration, distance, calories, heartRate, comment, 0F, 0F, 0F,
            ArrayList<LatLng>()
        )
    }


    //Ease of use function to convert intent to a database entry
    fun getEntryFromIntent(intent : Intent): ExerciseEntry{
        var ee = ExerciseEntry()
        val extras = intent.extras
        if(extras != null) {
            ee = getEntryFromBundle(extras)
        }else{
            println("EXTRAS NULL!")
        }
        return ee
    }

    //same but for bundles
    fun createEntryBundle(inputType: String, activityType: String, dateTime : String, duration : Float, distance : Float, calories : Int, heartRate : Int, comment: String, climb : Float, avgPace : Float, avgSpeed : Float, locations : ArrayList<LatLng>
    ): Bundle {
        val bundle = Bundle()
        bundle.putString("input_type", inputType)
        bundle.putString("activity_type", activityType)
        bundle.putString("date_time", dateTime)
        bundle.putFloat("duration", duration)
        bundle.putFloat("distance", distance)
        bundle.putInt("calories", calories)
        bundle.putInt("heart_rate", heartRate)
        bundle.putString("comment", comment)
        bundle.putFloat("climb", climb)
        bundle.putFloat("average_pace", avgPace)
        bundle.putFloat("average_speed", avgSpeed)
        bundle.putString("locations", fromArrayList(locations))
        return bundle
    }

    fun getBundleFromEntry(ee : ExerciseEntry) : Bundle{
        val bundle = createEntryBundle(ee.inputType, ee.activityType, ee.dateTime, ee.duration, ee.distance, ee.calories, ee.heartRate, ee.comment, ee.climb, ee.avgPace, ee.avgSpeed, ee.locations)
        return bundle
    }

    fun getEntryFromBundle(extras : Bundle): ExerciseEntry {
        val ee = ExerciseEntry()
        ee.inputType = extras.getString("input_type", "")
        ee.activityType = extras.getString("activity_type", "")
        ee.dateTime = extras.getString("date_time", "")
        ee.duration = extras.getFloat("duration", 0F)
        ee.distance = extras.getFloat("distance", 0F)
        ee.calories = extras.getInt("calories", 0)
        ee.heartRate = extras.getInt("heart_rate", 0)
        ee.comment = extras.getString("comment", "")
        ee.climb = extras.getFloat("climb", 0F)
        ee.avgPace = extras.getFloat("average_pace", 0F)
        ee.avgSpeed = extras.getFloat("average_speed", 0F)
        ee.locations = toArrayList( extras.getString("locations")!! )
        return ee
    }



    //For consistent conversions across Activities
    fun toArrayList(json : String ): ArrayList<LatLng>{
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        val array: ArrayList<LatLng> = gson.fromJson(json, listType)
        return array
    }
    fun fromArrayList(array : ArrayList<LatLng>): String{
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        val json: String = gson.toJson(array, listType)
        return json
    }


    //unit conversions
    fun milesToKm(miles: Float):Float{
        val km = miles * 1.609344F
        return km
    }
    fun kmToMiles(km : Float):Float{
        val miles = km / 1.609344F
        return miles
    }

    fun metersToMiles(meters : Float): Float{
        val miles = meters / 1609.344F
        return miles
    }

    fun milesToMeters(miles: Float) : Float{
        val meters = miles * 1609.344F
        return meters
    }

    //given minutes in a float, return just minutes
    fun getMinutes(duration: Float): Int{
        return duration.toInt()
    }

    //given minutes in a float, return just seconds
    fun getSeconds(duration : Float): Int{
        val minutes = duration.toInt()
        val seconds = ( 60 * (duration - minutes)).toInt()
        return seconds
    }

}