package com.amrullaev.myadib.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.amrullaev.myadib.MainActivity
import com.amrullaev.myadib.R
import com.amrullaev.myadib.adapters.PagerAdapter
import com.amrullaev.myadib.database.AppDatabase
import com.amrullaev.myadib.database.dao.WriterDao
import com.amrullaev.myadib.databinding.FragmentHomeBinding
import com.amrullaev.myadib.databinding.ItemTabBinding
import com.amrullaev.myadib.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment(), MainActivity.OnBackPressedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var sPref: SharedPreferences
    private lateinit var dao: WriterDao
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        sPref = requireContext().getSharedPreferences("shared", Context.MODE_PRIVATE)
        dao = AppDatabase.getInstance(binding.root.context).writerDao()
        setUpViewPager()

        binding.searchCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("page", "home")
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment, bundle)
        }
        setTabs()
        return binding.root
    }


    private fun setUpViewPager() {
        val pagesList =
            arrayListOf("Mumtoz adabiyoti", "O\'zbek adabiyoti", "Jahon adabiyoti")

        pagerAdapter = PagerAdapter(childFragmentManager, lifecycle, pagesList)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tablayout, binding.viewPager) { tab, position ->
            tab.text = pagesList[position]
        }.attach()

    }

    private fun setTabs() {
        val pagesList = arrayOf("Mumtoz adabiyoti", "O\'zbek adabiyoti", "Jahon adabiyoti")
        val count: Int = binding.tablayout.tabCount
        for (i in 0 until count) {
            val itemView = ItemTabBinding.inflate(layoutInflater)
            if (i == 0) {
                itemView.textView.setTextColor(Color.parseColor("#ffffff"))
                itemView.textView.background.setTint(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_green
                    )
                )
            }
            itemView.textView.text = pagesList[i]
            binding.tablayout.getTabAt(i)?.customView = itemView.root
        }

        binding.tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView = tab.customView
                sPref.edit().putInt("posi", tab.position).apply()
                val textview = tabView!!.findViewById<TextView>(R.id.text_view)
                textview.setTextColor(Color.parseColor("#ffffff"))
                textview.background.setTint(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_green
                    )
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView = tab.customView
                val textview = tabView!!.findViewById<TextView>(R.id.text_view)
                textview.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_text
                    )
                )
                textview.background.setTint(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_light_back
                    )
                )
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tablayout.getTabAt(position)?.select()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    binding.tablayout.callOnClick()
                }
            }
        }

        binding.viewPager.registerOnPageChangeCallback(myPageChangeCallback)

    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
        (activity as MainActivity).setOnBackPressedListener(this@HomeFragment)
    }

    override fun onBackPressed() {
        viewModel.refreshData()
    }
}