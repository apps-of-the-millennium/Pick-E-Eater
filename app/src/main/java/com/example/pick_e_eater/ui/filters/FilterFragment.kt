package com.example.pick_e_eater.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pick_e_eater.databinding.FragmentFiltersBinding
import com.example.pick_e_eater.di.DatabaseModule
import com.example.pick_e_eater.model.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val filterViewModel =
            ViewModelProvider(this).get(FilterViewModel::class.java)

        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        val ratingSpinnerId = binding.ratingSpinner
        val costSpinnerId = binding.costSpinner

        val options = listOf(">=", "<=", "==")
        val arrayAdp = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        ratingSpinnerId.adapter = arrayAdp
        costSpinnerId.adapter = arrayAdp

        val gridContainer: GridLayout = binding.foodType
        val selectAll: CheckBox = binding.selectAll
        val checkboxes = ArrayList<CheckBox>()

        for (foodType in foodTypes) {
            val checkbox = CheckBox(requireContext())
            checkbox.text = foodType
            gridContainer.addView(checkbox)
            checkboxes.add(checkbox)
        }

        selectAll.setOnCheckedChangeListener { _, isChecked ->
            for (checkbox in checkboxes) {
                checkbox.isChecked = isChecked
            }
        }

        binding.testButton.setOnClickListener {
            getRestaurants()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class LatLng(val latitude: Double, val longitude: Double)

    private fun calculateNewCoords(lat: Double, lon: Double, distanceInKm: Double): List<LatLng> {
        val degreesPerKm = 0.009

        val north = LatLng(lat + distanceInKm * degreesPerKm, lon)
        val south = LatLng(lat - distanceInKm * degreesPerKm, lon)
        val east = LatLng(lat, lon + distanceInKm * degreesPerKm)
        val west = LatLng(lat, lon - distanceInKm * degreesPerKm)

        return listOf(north, south, east, west)
    }

    fun getRestaurants(): List<Int>? {
        // TODO: add function that pulls our current location
        // TODO: Provide opportunity to change location via map
        val startingPoint = LatLng(43.65427, -79.39925) // Example starting point
        // TODO: pull from distance input in fragment, may need to confirm it's a number
        val distanceInKm = 5.0

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
            val restaurants: List<Int> =
                restaurantDao.getWithinRange(latUp, latDown, longLeft, longRight)
            System.err.println(restaurants)
        }
        return null

    }
}