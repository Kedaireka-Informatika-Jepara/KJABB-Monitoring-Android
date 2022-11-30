package com.kedaireka.monitoringkjabb.ui.statistics.parameter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.databinding.FragmentPhLevelsBinding
import com.kedaireka.monitoringkjabb.model.Sensor


class PhLevelsFragment : Fragment() {
    private lateinit var phLvelFragmentViewModel: PhLevelsFragmentViewModel
    private lateinit var recordsInRange: ArrayList<Sensor>

    private var _binding: FragmentPhLevelsBinding? = null
    private val binding get() = _binding!!

    private var max = 0.0
    private var min = 0.0
    private var avg = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        phLvelFragmentViewModel = ViewModelProvider(this)[PhLevelsFragmentViewModel::class.java]

        _binding = FragmentPhLevelsBinding.inflate(inflater, container, false)
        val root: View = binding.root
    }
}