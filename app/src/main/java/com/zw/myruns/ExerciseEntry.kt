package com.zw.myruns

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList


/**
 * Defines the structure of one row (entry) in the database table
 */
@Entity(tableName = "exercise_table")
data class ExerciseEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "input_type")
    var inputType: Int = 0,

    @ColumnInfo(name = "activity_type")
    var activityType: String = "",

    @ColumnInfo(name = "date_time")
    var dateTime: String = "",
    //var dateTime: Calendar? = null,
    //TODO : correct data types, conversion, just testing now

    @ColumnInfo(name = "duration")
    var duration: Int = 0,

    @ColumnInfo(name = "distance")
    var distance: Float = 0F,

    @ColumnInfo(name = "average_pace")
    var avgPace: Float = 0F,

    @ColumnInfo(name = "average_speed")
    var avgSpeed: Float = 0F,

    @ColumnInfo(name = "calories")
    var calories: Float = 0F,

    @ColumnInfo(name = "climb")
    var climb: Float = 0F,

    @ColumnInfo(name = "heart_rate")
    var heartRate: Int = 0,

    @ColumnInfo(name = "comment")
    var comment: String = "",

    @ColumnInfo(name = "location_list")
    var locationList: String = ""
    //var locationList: ArrayList<LatLng>? = null


)
object DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}