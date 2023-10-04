package com.example.pick_e_eater.operations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pick_e_eater.model.Restaurant

@Dao
interface RestaurantDao {
    @Insert
    suspend fun insert(entity: Restaurant)
    //TODO: change to circle radius by adding extra check with distance (but we may not implement this because efficiency issues)
    @Query("SELECT id FROM restaurant WHERE rest_lat >= :lat_down AND " +
            "rest_lat <= :lat_up AND rest_long <= :long_right AND rest_long >= :long_left AND "+
            "(:curr_day != 'Closed' or :curr_day is null) AND rest_type IN (:food_types)")
    suspend fun getWithFilters(lat_up: Double, lat_down: Double, long_left: Double,
                               long_right: Double, curr_day: String,
                               food_types: MutableList<String>): List<Int>

    @Query("SELECT id FROM restaurant WHERE rest_lat >= :lat_down AND " +
            "rest_lat <= :lat_up AND rest_long <= :long_right AND rest_long >= :long_left")
    suspend fun getWithinRange(lat_up: Double, lat_down: Double, long_left: Double,
                               long_right: Double): List<Int>

    @Query("SELECT id FROM restaurant WHERE :curr_day != 'Closed' or :curr_day is null")
    suspend fun checkClosed(curr_day: String): List<Int>

    @Query("SELECT id FROM restaurant WHERE rest_type IN (:food_types)")
    suspend fun getFoodType(food_types: MutableList<String>): List<Int>

    @Query("SELECT rest_name FROM restaurant WHERE id == :restId")
    suspend fun getRestaurantName(restId: Int): String

    @Query("SELECT rest_type FROM restaurant WHERE id == :restId")
    suspend fun getRestaurantType(restId: Int): String?

    @Query("SELECT rest_photo FROM restaurant WHERE id == :restId")
    suspend fun getRestaurantPhoto(restId: Int): String?

    @Query("SELECT rest_url FROM restaurant WHERE id == :restId")
    suspend fun getRestaurantUrl(restId: Int): String?

    @Query("SELECT rest_area FROM restaurant WHERE id == :restId")
    suspend fun getRestaurantArea(restId: Int): String?

    @Query("SELECT rest_address FROM restaurant WHERE id == :restId")
    suspend fun getRestaurantAddress(restId: Int): String

    @Query("SELECT rest_lat FROM restaurant WHERE id == :restId")
    suspend fun getRestaurantLat(restId: Int): Double?

    @Query("SELECT rest_long FROM restaurant WHERE id == :restId")
    suspend fun getRestaurantLong(restId: Int): Double?

    @Query("SELECT hours_mon FROM restaurant WHERE id == :restId")
    suspend fun getHoursMon(restId: Int): String?

    @Query("SELECT hours_tues FROM restaurant WHERE id == :restId")
    suspend fun getHoursTues(restId: Int): String?

    @Query("SELECT hours_wed FROM restaurant WHERE id == :restId")
    suspend fun getHoursWed(restId: Int): String?

    @Query("SELECT hours_thurs FROM restaurant WHERE id == :restId")
    suspend fun getHoursThurs(restId: Int): String?

    @Query("SELECT hours_fri FROM restaurant WHERE id == :restId")
    suspend fun getHoursFri(restId: Int): String?

    @Query("SELECT hours_sat FROM restaurant WHERE id == :restId")
    suspend fun getHoursSat(restId: Int): String?

    @Query("SELECT hours_sun FROM restaurant WHERE id == :restId")
    suspend fun getHoursSun(restId: Int): String?

//    TODO: add these to the database (rating and cost)
//    @Query("SELECT id FROM restaurant WHERE rest_rating :rating_op :rating AND rest_cost :cost_op :cost")
//    suspend fun getWithinRatingAndCost(rating_op: String, rating: Int, cost_op: String, cost: Int) :List<Int>

    // Add more queries as needed
}