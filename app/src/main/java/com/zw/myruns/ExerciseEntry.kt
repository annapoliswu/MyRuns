package com.zw.myruns

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList


/**
 * Defines the structure of one row (entry) in the database table
 */
@Entity(tableName = "exercise_table")
data class ExerciseEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "input_type")
    val inputType: Int,

    @ColumnInfo(name = "activity_type")
    val activityType: String,

    @ColumnInfo(name = "date_time")
    val dateTime: Calendar?,

    @ColumnInfo(name = "duration")
    val duration: Int?,

    @ColumnInfo(name = "distance")
    val distance: Float?,

    @ColumnInfo(name = "average_pace")
    val avgPace: Float?,

    @ColumnInfo(name = "average_speed")
    val avgSpeed: Float?,

    @ColumnInfo(name = "calories")
    val calories: Float?,

    @ColumnInfo(name = "climb")
    val climb: Float?,

    @ColumnInfo(name = "heart_rate")
    val heartRate: Int?,

    @ColumnInfo(name = "comment")
    val comment: String?,

    @ColumnInfo(name = "location_list")
    val locationList: ArrayList<LatLng>?

)