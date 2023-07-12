package com.example.pick_e_eater.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pick_e_eater.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        val spinnerId = binding.ratingSpinner
        val spinnerId2 = binding.ratingSpinner2

        val options = listOf(">=", "<=")
        val arrayAdp = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        spinnerId.adapter = arrayAdp
        spinnerId2.adapter = arrayAdp

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}