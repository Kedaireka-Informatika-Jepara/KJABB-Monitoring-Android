package com.kedaireka.monitoringkjabb.ui.prediction

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoringkjabb.ui.prediction.parameter.*

private const val NUM_TABS = 6

    class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return NUM_TABS
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return TurbidityPredictionFragment()
                1 -> return AmmoniaPredictionFragment()
                2 -> return SuhuPredictionFragment()
                3 -> return PhLevelsPredictionFragment()
                4 -> return TdsPredictionFragment()
            }

            return RaindropsPredictionFragment()
        }
    }

