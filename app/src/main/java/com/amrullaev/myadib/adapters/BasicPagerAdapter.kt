package com.amrullaev.myadib.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amrullaev.myadib.ui.HomeFragment
import com.amrullaev.myadib.ui.SavedFragment
import com.amrullaev.myadib.ui.SettingFragment

class BasicPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> SavedFragment()
            2 -> SettingFragment()
            else -> HomeFragment()
        }
    }
}