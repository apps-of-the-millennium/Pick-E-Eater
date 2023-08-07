package com.example.pick_e_eater.operations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pick_e_eater.model.Restaurant

@Dao
interface RestaurantDao {
    @Insert
    suspend fun insert(entity: Restaurant)

    // TODO: table name hardcoded
    @Query("SELECT * FROM restaurant WHERE rest_name == '100 Percent Korean'")
    suspend fun getAllEntities(): List<Restaurant>

    @Query("SELECT id FROM restaurant WHERE rest_lat >= :lat_down AND " +
            "rest_lat <= :lat_up AND rest_long <= :long_right AND rest_long >= :long_left")
    suspend fun getWithinRange(lat_up: Double, lat_down: Double, long_left: Double, long_right: Double): List<Int>

    // Add more queries as needed
}