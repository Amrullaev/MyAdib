package com.amrullaev.myadib.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.amrullaev.myadib.R
import com.amrullaev.myadib.adapters.BasicPagerAdapter
import com.amrullaev.myadib.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BasicPagerAdapter
    private lateinit var sPref: SharedPreferences
    private var currentPosition: Int? = null
    private val KEY = "key"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        sPref = requireContext().getSharedPreferences("shared", Context.MODE_PRIVATE)
        if (sPref.getString("mode", "").equals("night")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        setUpUI()

        return binding.root
    }

    private fun setUpUI() {
        binding.bottomBar.onItemSelected = {
            when (it) {
                0 -> {
                    binding.viewPager.currentItem = 0
                    sPref.edit().putInt(KEY, 0).apply()
                    currentPosition = 0
                }
                1 -> {
                    binding.viewPager.currentItem = 1
                    currentPosition = 1
                }
                2 -> {
                    binding.viewPager.currentItem = 2
                    sPref.edit().putInt(KEY, 2).apply()
                    currentPosition = 2
                }
            }
        }
        adapter = BasicPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false

        val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomBar.itemActiveIndex = position
            }
        }

        binding.viewPager.registerOnPageChangeCallback(myPageChangeCallback)
        if (currentPosition != null) {
            binding.viewPager.currentItem = currentPosition!!
        } else {
            binding.viewPager.currentItem = sPref.getInt(KEY, 0)
        }
    }


}