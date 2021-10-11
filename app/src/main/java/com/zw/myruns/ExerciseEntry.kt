package com.zw.myruns

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * Defines the structure of one row (entry) in the database table
 */
@TypeConverters(MyConverters::class)
@Entity(tableName = "exercise_table")
data class ExerciseEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "input_type")
    var inputType: String = "",

    @ColumnInfo(name = "activity_type")
    var activityType: String = "",

    @ColumnInfo(name = "date_time")
    var dateTime: String = "",  //can store as String

    @ColumnInfo(name = "duration")
    var duration: Float = 0F,   //in minutes

    @ColumnInfo(name = "distance")  //stored in miles
    var distance: Float = 0F,

    @ColumnInfo(name = "average_pace")
    var avgPace: Float = 0F,

    @ColumnInfo(name = "average_speed")
    var avgSpeed: Float = 0F,

    @ColumnInfo(name = "calories")
    var calories: Int = 0,

    @ColumnInfo(name = "climb")
    var climb: Float = 0F,

    @ColumnInfo(name = "heart_rate")
    var heartRate: Int = 0,

    @ColumnInfo(name = "comment")
    var comment: String = "",

    @ColumnInfo(name = "locations")
    var locations: ArrayList<LatLng> = ArrayList()

)

class MyConverters {

    @TypeConverter
    fun toArrayList(json : String ): ArrayList<LatLng>{
        return Util.toArrayList(json)
    }

    @TypeConverter
    fun fromArrayList(array : ArrayList<LatLng>): String{
        return Util.fromArrayList(array)
    }

    //Gson
    //Gregorian Calendar
}

