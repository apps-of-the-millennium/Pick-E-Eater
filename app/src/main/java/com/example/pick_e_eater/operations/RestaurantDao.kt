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

    @Query("SELECT * FROM restaurant WHERE rest_lat >= :lat_left AND " +
            "rest_lat <= :lat_right AND rest_long <= :long_up AND rest_long >= :long_down")
    suspend fun getWithinRange(lat_left: Double, lat_right: Double, long_down: Double, long_up: Double): List<Restaurant>

    // Add more queries as needed
}