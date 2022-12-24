package com.kedaireka.monitoringkjabb.ui.statistics

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.*


private const val NUM_TABS = 6

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return TurbidityFragment()
            1 -> return AmmoniaFragment()
            2 -> return WaterTemperatureFragment()
            3 -> return PhLevelsFragment()
            4 -> return TdsFragment()
        }

        return RaindropsFragment()
    }
}