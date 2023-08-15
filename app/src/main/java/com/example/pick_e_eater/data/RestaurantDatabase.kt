package com.example.pick_e_eater.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pick_e_eater.model.Restaurant
import com.example.pick_e_eater.operations.RestaurantDao

@Database(entities = [Restaurant::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}