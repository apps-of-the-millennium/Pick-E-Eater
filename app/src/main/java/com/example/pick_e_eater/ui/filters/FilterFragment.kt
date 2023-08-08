package com.example.pick_e_eater.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import com.example.pick_e_eater.databinding.FragmentFiltersBinding
import com.example.pick_e_eater.di.DatabaseModule
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class FilterFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Currently limit to 24 while testing
    // TODO: enumerate later to check which has been checked off
    val foodTypes = arrayOf("Afghan", "African", "American/Canadian", "Caribbean", "Chinese",
        "Colombian", "Desserts", "Egyptian", "Ethiopian", "Filipino", "Fast Food", "Greek",
        "German", "Indian", "Indonesian", "Italian", "Japanese", "Lebanese", "Mexican",
        "Middle Eastern", "Pakistani", "Turkish", "Vietnamese", "TBD")

//    private lateinit var mapView: MapView
//    private lateinit var googleMap: GoogleMap
    private val checkBoxes = ArrayList<CheckBox>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val ratingSpinnerId = binding.ratingSpinner
        val costSpinnerId = binding.costSpinner

        val options = listOf(">=", "<=", "==")
        val arrayAdp = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        ratingSpinnerId.adapter = arrayAdp
        costSpinnerId.adapter = arrayAdp

        val gridContainer: GridLayout = binding.foodType
        val selectAll: CheckBox = binding.selectAll

        for (foodType in foodTypes) {
            val checkbox = CheckBox(requireContext())
            checkbox.text = foodType
            gridContainer.addView(checkbox)
            checkBoxes.add(checkbox)
        }

        selectAll.setOnCheckedChangeListener { _, isChecked ->
            for (checkbox in checkBoxes) {
                checkbox.isChecked = isChecked
            }
        }

        // Default 1km distance
        binding.distanceInput.setText("1")

        binding.randomizeButton.setOnClickListener {
            if (validationCheck()) {
                getRestaurants()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // TODO: validation check for rating and cost maybe empty checkboxes?
    private fun validationCheck(): Boolean {
        if (binding.distanceInput.text.toString() == "" || binding.distanceInput.text.toString().toDouble() == 0.0) {
            binding.distanceInput.error = "Distance required, must be greater than 0"
            return false
        }
//        if (binding.ratingBar.rating == 0.0f || binding.costBar.rating == 0.0f)
//        System.err.println(binding.ratingBar.rating)
//        System.err.println(binding.costBar.rating)
//        System.err.println(binding.ratingSpinner.selectedItemId)
//        System.err.println(binding.costSpinner.selectedItemId)
        return true

    }

    private fun calculateNewCoords(lat: Double, lon: Double, distanceInKm: Double): List<LatLng> {
        val degreesPerKm = 0.009

        val north = LatLng(lat + distanceInKm * degreesPerKm, lon)
        val south = LatLng(lat - distanceInKm * degreesPerKm, lon)
        val east = LatLng(lat, lon + distanceInKm * degreesPerKm)
        val west = LatLng(lat, lon - distanceInKm * degreesPerKm)

        return listOf(north, south, east, west)
    }

    private fun getDayOfWeekString(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "hours_sun"
            Calendar.MONDAY -> "hours_mon"
            Calendar.TUESDAY -> "hours_tues"
            Calendar.WEDNESDAY -> "hours_wed"
            Calendar.THURSDAY -> "hours_thurs"
            Calendar.FRIDAY -> "hours_fri"
            Calendar.SATURDAY -> "hours_sat"
            // TODO: may need to adjust invalid to ensure no filter on days
            else -> "Invalid"
        }
    }

    private fun getRestaurants(): List<Int>? {
        // TODO: add function that pulls our current location
        // TODO: Provide opportunity to change location via map (Hardcoded for now)
        val startingPoint = LatLng(43.65427, -79.39925) // Example starting point
        val distanceInKm = binding.distanceInput.text.toString().toDouble()

        val newCoordinates =
            calculateNewCoords(startingPoint.latitude, startingPoint.longitude, distanceInKm)

        // North
        val latUp = newCoordinates[0].latitude
        // South
        val latDown = newCoordinates[1].latitude
        // East
        val longRight = newCoordinates[2].longitude
        // West
        val longLeft = newCoordinates[3].longitude

        val restaurantDb = DatabaseModule.provideDatabase(requireContext())
        val restaurantDao = restaurantDb.restaurantDao()

        CoroutineScope(Dispatchers.IO).launch {
            val foodTypeList = mutableListOf<String>()
            for (checkbox in checkBoxes) {
                if(checkbox.isChecked) {
                    foodTypeList.add(checkbox.text as String)
                }
            }

            System.err.println(foodTypeList)
            val currDay = getDayOfWeekString(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

            val validRests: List<Int> =
                restaurantDao.getWithFilters(latUp, latDown, longLeft, longRight,
                    currDay, foodTypeList)
            System.err.println(validRests)
        }
        return null

    }
}