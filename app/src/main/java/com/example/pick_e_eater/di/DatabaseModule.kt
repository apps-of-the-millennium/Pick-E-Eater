package com.example.pick_e_eater.di

import android.content.Context
import androidx.room.Room
import com.example.pick_e_eater.data.RestaurantDatabase


object DatabaseModule {

    fun provideDatabase(
        context: Context
    ) : RestaurantDatabase {
        return Room.databaseBuilder(
            context,
            RestaurantDatabase::class.java,
            "pick-e-eater"
        ).createFromAsset("database/pick-e-eater.db").build()
    }

    fun provideDao(database: RestaurantDatabase) = database.restaurantDao()
}