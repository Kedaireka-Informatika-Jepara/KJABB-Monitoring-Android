package com.kedaireka.monitoringkjabb.ui.prediction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.kedaireka.monitoringkjabb.databinding.FragmentPredictionBinding


private val parameterArray = arrayOf(
    "Ammonia",
    "Raindrops",
    "Water Temperature",
    "pH Level",
    "Dissolved Oxygen"
)

class PredictionFragment : Fragment() {

    private var _binding: FragmentPredictionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPredictionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager = binding.predictionViewpager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = parameterArray[position]
        }.attach()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}