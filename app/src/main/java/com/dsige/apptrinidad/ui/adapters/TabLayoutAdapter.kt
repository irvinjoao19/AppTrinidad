package com.dsige.apptrinidad.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

abstract class TabLayoutAdapter {

    class TabLayoutCheckListAdapter(fm: FragmentManager, private val numberOfTabs: Int, var inspeccionId: Int, var usuarioId: Int, var name: String)
        : FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
//                0 -> GeneralFragment.newInstance(inspeccionId, usuarioId, name)
//                1 -> CheckListFragment.newInstance(inspeccionId, usuarioId)
//                2 -> ObservationFragment.newInstance(inspeccionId,usuarioId)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }
}