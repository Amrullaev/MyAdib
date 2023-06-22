package com.amrullaev.myadib.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amrullaev.myadib.ui.PagerFragment

class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, val list: List<String>) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return PagerFragment.newInstance(list[position])
    }
}