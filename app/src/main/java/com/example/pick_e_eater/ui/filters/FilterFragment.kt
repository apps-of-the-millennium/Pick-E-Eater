package com.example.pick_e_eater.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.GridLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pick_e_eater.BuildConfig
import com.example.pick_e_eater.animations.AwesomeCarousel
import com.example.pick_e_eater.databinding.FragmentFiltersBinding
import com.example.pick_e_eater.di.DatabaseModule
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class FilterFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Currently limit to 24 while testing
    // TODO: enumerate later to check which has been checked off
    private val foodTypes = arrayOf("Afghan", "African", "American/Canadian", "Caribbean", "Chinese",
        "Colombian", "Desserts", "Egyptian", "Ethiopian", "Filipino", "Fast Food", "Greek",
        "German", "Indian", "Indonesian", "Italian", "Japanese", "Lebanese", "Mexican",
        "Middle Eastern", "Pakistani", "Turkish", "Vietnamese", "TBD")

    private val checkBoxes = ArrayList<CheckBox>()
    private var inputLatLng = LatLng(0.0,0.0)

    @OptIn(ExperimentalFoundationApi::class)
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


        // Initialize Places SDK with your API key
        Places.initialize(context, BuildConfig.MAPS_API_KEY)
        // TODO: There's probably a better way to do this
        val autocompleteFragment = childFragmentManager.fragments[0] as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setCountry("CAN")
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                System.err.println("Place: ${place.address}, ${place.latLng}")
                autocompleteFragment.setText(place.address)
                inputLatLng = place.latLng
                binding.errorMessageText.isVisible = false
            }
            override fun onError(status: Status) {
                // TODO: Handle the error.
                System.err.println("An error occurred: $status")
            }
        })

        binding.randomizeButton.setOnClickListener {
            lifecycleScope.launch {
                if (validationCheck()) {
                    val restaurants = getRestaurants()
                    System.err.println("GetResults$restaurants")
                    try {
                        val finalRest = restaurants.random()
                        System.err.println(finalRest)
                        binding.composeView.setContent { AwesomeCarousel(restaurantId = finalRest) }
                    } catch (e: NoSuchElementException) {
                        binding.errorMessageText.isVisible = true
                        binding.errorMessageText.text = "No restaurants available with these filters," +
                                " please try expanding your options"
                    }
                }
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
        // TODO: check if it's empty, this only works with default no input otherwise will use prev input even if empty
        if (inputLatLng == LatLng(0.0,0.0)) {
            binding.errorMessageText.isVisible = true
            binding.errorMessageText.text = "Please enter a location"
            return false
        }
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

    private suspend fun getRestaurants(): List<Int> {
        // TODO: handle empty input
        val startingPoint = inputLatLng
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

        val validRests = withContext(Dispatchers.IO) {
            val foodTypeList = mutableListOf<String>()
            for (checkbox in checkBoxes) {
                if(checkbox.isChecked) {
                    foodTypeList.add(checkbox.text as String)
                }
            }

            val currDay = getDayOfWeekString(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

            val validRests: List<Int> =
                restaurantDao.getWithFilters(
                    latUp, latDown, longLeft, longRight,
                    currDay, foodTypeList
                )

            System.err.println("In coroutine$validRests")
            return@withContext validRests
        }
        return validRests
    }
}