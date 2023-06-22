package com.amrullaev.myadib.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amrullaev.myadib.R
import com.amrullaev.myadib.adapters.WriteAdapter
import com.amrullaev.myadib.database.AppDatabase
import com.amrullaev.myadib.databinding.FragmentPagerBinding
import com.amrullaev.myadib.models.Writer
import com.amrullaev.myadib.viewmodel.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

private const val ARG_PARAM1 = "category"

class PagerFragment : Fragment() {
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getString(ARG_PARAM1)
        }
    }

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseFireStore: FirebaseFirestore
    private var list: ArrayList<Writer> = arrayListOf()
    private lateinit var writerAdapter: WriteAdapter
    private var isCreated = false
    private val parentViewModel by viewModels<HomeViewModel>({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerBinding.inflate(layoutInflater, container, false)

        firebaseFireStore = FirebaseFirestore.getInstance()
        fireBaseSetting()
        initAdapter()
        loadData()

        return binding.root
    }

    private fun initAdapter() {
        val dao = AppDatabase.getInstance(binding.root.context).writerDao()
        writerAdapter = WriteAdapter(dao, object : WriteAdapter.OnItemClickListener {
            override fun onItemClick(writer: Writer) {
                val bundle = Bundle()
                bundle.putSerializable("writer", writer)
                findNavController().navigate(R.id.action_mainFragment_to_writerFragment, bundle)
            }

            override fun onItemSaveClick(writer: Writer, position: Int) {
            }

        })
        binding.rv.adapter = writerAdapter

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun loadData() {
        firebaseFireStore.collection("writers")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    list = arrayListOf()
                    it.result?.forEach { queryDocumentSnapshot ->
                        val writer = queryDocumentSnapshot.toObject(Writer::class.java)
                        if (writer.typeWriter == category) {
                            list.add(writer)
                        }
                    }
                    parentViewModel.boolean.observe(viewLifecycleOwner) {
                        writerAdapter.submitList(list, false)
                    }
                    writerAdapter.submitList(list, true)
                    isCreated = true
                }
            }
    }

    private fun fireBaseSetting() {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firebaseFireStore.firestoreSettings = settings

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            PagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }


}
