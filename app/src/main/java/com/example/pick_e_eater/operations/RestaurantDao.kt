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
    @Query("SELECT * FROM restaurant WHERE rest_name == 'rest_name_name'")
    suspend fun getAllEntities(): List<Restaurant>

    // Add more queries as needed
}