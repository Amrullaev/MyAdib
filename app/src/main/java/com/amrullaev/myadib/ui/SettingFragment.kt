package com.amrullaev.myadib.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.amrullaev.myadib.R
import com.amrullaev.myadib.databinding.FragmentSettingBinding
import com.amrullaev.myadib.databinding.ItemDialogBinding


class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(layoutInflater, container, false)

        sPref = requireContext().getSharedPreferences("shared", Context.MODE_PRIVATE)
        if (sPref.getString("mode", "")=="night"){
            binding.switchMode.isChecked = true
            binding.modeTv.text = "Tungi rejim"
        }
        binding.switchMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    sPref.edit().putString("mode", "night").apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.modeTv.text = "Tungi rejim"
                } else {
                    sPref.edit().putString("mode", "day").apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    binding.modeTv.text = "Kunduzgi rejim"
                }
            }
        }

        binding.addCard.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addFragment)
        }

        binding.infoCard.setOnClickListener {
            val alertDialog = AlertDialog.Builder(binding.root.context, R.style.SheetDialog)
            val itemDialog = ItemDialogBinding.inflate(layoutInflater)
            alertDialog.setView(itemDialog.root)
            alertDialog.show()
        }

        return binding.root
    }


}