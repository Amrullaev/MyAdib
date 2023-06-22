package com.amrullaev.myadib.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.amrullaev.myadib.MainActivity
import com.amrullaev.myadib.R
import com.amrullaev.myadib.database.AppDatabase
import com.amrullaev.myadib.database.dao.WriterDao
import com.amrullaev.myadib.database.entity.WriterEntity
import com.amrullaev.myadib.databinding.FragmentWriterBinding
import com.amrullaev.myadib.models.Writer
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import kotlin.math.abs


class WriterFragment : Fragment() {
    private var _binding: FragmentWriterBinding? = null
    private val binding get() = _binding!!
    private var isSearching = false
    private var isSaved = false
    private var isCollapsed = false
    private lateinit var dao: WriterDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriterBinding.inflate(layoutInflater, container, false)
        dao = AppDatabase.getInstance(binding.root.context).writerDao()
        val writer = arguments?.getSerializable("writer") as Writer

        saveWriterSetup(writer)

        loadData(writer)
        onClickListener()


        binding.appbarlayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val postion = appBarLayout?.totalScrollRange!! - abs(verticalOffset)
            if (postion in 0..80) {
                if (isSearching) binding.collapsingToolbar.title = ""
                if(isSaved){
                    binding.saveCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.color_light_back))
                    binding.saveIv.setImageDrawable(ContextCompat.getDrawable(binding.root.context,R.drawable.ic_saved_green))
                }else{
                    binding.saveCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.color_light_back))
                    binding.saveIv.setImageDrawable(ContextCompat.getDrawable(binding.root.context,R.drawable.ic_save))
                }
                isCollapsed = true
            } else {
                if (!isSearching) binding.collapsingToolbar.title = writer.fullname
                if (isSaved){
                    binding.saveCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.save_green_back))
                    binding.saveIv.setImageDrawable(ContextCompat.getDrawable(binding.root.context,R.drawable.ic_saved))
                }else{
                    binding.saveCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.color_light_back))
                    binding.saveIv.setImageDrawable(ContextCompat.getDrawable(binding.root.context,R.drawable.ic_save))
                }
                isCollapsed = false
            }
        })



        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "") {
                    binding.aboutEmphasisTv.text = writer.info
                    binding.searchEt.hint = "Qidiruv"
                }
                if (s.toString() != "\n" && s.toString() != " " && s.toString() != "  ") {
                    binding.aboutEmphasisTv.setTextToHighlight(s.toString())
                    binding.aboutEmphasisTv.highlight()
                }
            }

        })


        return binding.root
    }

    private fun saveWriterSetup(writer: Writer) {
        val writerByName = dao.getWriterByName(writer.fullname!!)
        if (writerByName != null) {
            binding.saveCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.save_green_back))
            binding.saveIv.setImageDrawable(ContextCompat.getDrawable(binding.root.context,R.drawable.ic_saved))
            isSaved = true
        }

        val saveAnimation = AnimationUtils.loadAnimation(context, R.anim.save_anim)

        binding.saveCard.setOnClickListener {
            if (isSaved) {
                binding.saveIv.startAnimation(saveAnimation)
                binding.saveCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.color_light_back))
                binding.saveIv.setImageDrawable(ContextCompat.getDrawable(binding.root.context, R.drawable.ic_save))
                dao.remove(WriterEntity(writer.fullname!!))
                isSaved = false

            } else {
                isSaved = if (isCollapsed) {
                    binding.saveIv.startAnimation(saveAnimation)
                    binding.saveCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.color_light_back))
                    binding.saveIv.setImageDrawable(ContextCompat.getDrawable(binding.root.context,R.drawable.ic_saved_green))
                    dao.insert(WriterEntity(writer.fullname!!))
                    true
                } else {
                    binding.saveIv.startAnimation(saveAnimation)
                    binding.saveCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.save_green_back))
                    binding.saveIv.setImageDrawable(ContextCompat.getDrawable(binding.root.context,R.drawable.ic_saved))
                    dao.insert(WriterEntity(writer.fullname!!))
                    true
                }
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onClickListener() {
        binding.searchCard.setOnClickListener { openSearchToolbar() }
        binding.closeIv.setOnClickListener {
            closeSearchToolbar()
            binding.searchEt.setText("")
        }
        binding.exitIv.setOnClickListener {
            requireActivity().currentFocus?.let { view ->
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            (activity as MainActivity?)?.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(writer: Writer) {
        binding.collapsingToolbar.title = writer.fullname
        binding.yearTv.text = "(${writer.bornyear}-${writer.deathyear})"
        Picasso.get().load(writer.photoUrl).into(binding.photoIv)
        binding.aboutEmphasisTv.text = writer.info
        binding.aboutEmphasisTv.setTextHighlightColor(R.color.color_check)
    }
    private fun openSearchToolbar() {
        binding.firstToolbar.visibility = View.GONE
        binding.secondToolbar.visibility = View.GONE
        binding.searchToolbar.visibility = View.VISIBLE
        isSearching = true
    }

    private fun closeSearchToolbar() {
        requireActivity().currentFocus?.let { view ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.firstToolbar.visibility = View.VISIBLE
        binding.secondToolbar.visibility = View.VISIBLE
        binding.searchToolbar.visibility = View.GONE
        isSearching = false

    }

}