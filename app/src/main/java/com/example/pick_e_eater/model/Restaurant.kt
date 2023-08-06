package com.example.pick_e_eater.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant")
data class Restaurant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // String is TEXT because of SQLite
    val rest_name: String,
    val rest_type: String?,
    val rest_photo: String?,
    val rest_url: String?,
    val rest_area: String?,
    val rest_address: String,
    //    Could store as real but text works well too just need to conv
    val rest_lat: Double?,
    val rest_long: Double?,
    val hours_mon: String?,
    val hours_tues: String?,
    val hours_wed: String?,
    val hours_thurs: String?,
    val hours_fri: String?,
    val hours_sat: String?,
    val hours_sun: String?,
    // Add more columns as needed
)