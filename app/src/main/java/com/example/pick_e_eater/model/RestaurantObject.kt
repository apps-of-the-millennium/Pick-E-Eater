package com.example.pick_e_eater.model

class RestaurantObject(
    rest_name: String,
    rest_type: String?,
    rest_photo: String?,
    rest_url: String?,
    rest_area: String?,
    rest_address: String,
    rest_lat: Double?,
    rest_long: Double?,
    hours_mon: String?,
    hours_tues: String?,
    hours_wed: String?,
    hours_thurs: String?,
    hours_fri: String?,
    hours_sat: String?,
    hours_sun: String?,
) {
    val name = rest_name
    val type = rest_type
    val photo = rest_photo
    val url = rest_url
    val area = rest_area
    val address = rest_address
    val lat = rest_lat
    val long = rest_long
    val hoursMon = hours_mon
    val hoursTues = hours_tues
    val hoursWed = hours_wed
    val hoursThurs = hours_thurs
    val hoursFri = hours_fri
    val hoursSat = hours_sat
    val hoursSun = hours_sun
}