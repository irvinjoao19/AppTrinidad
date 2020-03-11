package com.dsige.apptrinidad.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dsige.apptrinidad.ui.fragments.VehicleCombustibleFragment
import com.dsige.apptrinidad.ui.fragments.VehicleGeneralFragment
import com.dsige.apptrinidad.ui.fragments.VehiclePorVencerFragment

abstract class TabLayoutAdapter {

    class TabLayoutRegister(fm: FragmentManager, private val numberOfTabs: Int,private val id : String) :
        FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> VehicleGeneralFragment.newInstance(id)
                1 -> VehicleCombustibleFragment.newInstance(id)
                2 -> VehiclePorVencerFragment.newInstance("","")
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }
}