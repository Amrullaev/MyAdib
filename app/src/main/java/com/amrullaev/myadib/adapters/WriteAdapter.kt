package com.amrullaev.myadib.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amrullaev.myadib.R
import com.amrullaev.myadib.database.dao.WriterDao
import com.amrullaev.myadib.database.entity.WriterEntity
import com.amrullaev.myadib.databinding.ItemWriterBinding
import com.amrullaev.myadib.models.Writer
import com.squareup.picasso.Picasso

class WriteAdapter(private var dao: WriterDao, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<WriteAdapter.VH>() {

    private var list = arrayListOf<Writer>()
    private var animation = true

    inner class VH(private val itemWriterBinding: ItemWriterBinding) :
        RecyclerView.ViewHolder(itemWriterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(writer: Writer, position: Int) {
            val context = itemWriterBinding.root.context
            val itemAnimation = AnimationUtils.loadAnimation(context, R.anim.my_animation)
            val saveAnimation = AnimationUtils.loadAnimation(context, R.anim.save_anim)
            if (animation) itemWriterBinding.root.startAnimation(itemAnimation)
            var saved = false

            val localWriter = dao.getWriterByName(writer.fullname!!)
            if (localWriter != null) {
                itemWriterBinding.saveCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.save_green_back
                    )
                )
                itemWriterBinding.saveIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_save
                    )
                )
                saved = true
            } else {
                itemWriterBinding.saveCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.save_back
                    )
                )
                itemWriterBinding.saveIv.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_save_black
                    )
                )
                saved = false
            }
            Picasso.get().load(writer.photoUrl).placeholder(R.drawable.placeholder)
                .into(itemWriterBinding.imageView)

            val fullname = writer.fullname!!.replace(" ", "\n")
            itemWriterBinding.nameTv.text = fullname
            itemWriterBinding.yearTv.text = "(${writer.bornyear}-${writer.deathyear})"
            itemWriterBinding.saveCard.setOnClickListener {
                if (!saved) {
                    itemWriterBinding.saveCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.save_green_back
                        )
                    )
                    itemWriterBinding.saveIv.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_saved
                        )
                    )
                    itemWriterBinding.saveIv.startAnimation(saveAnimation)
                    saved = true
                    dao.insert(WriterEntity(writer.fullname!!))

                    //  sPref.edit().putString(writer.fullname, "true").apply()
                } else {
                    itemWriterBinding.saveCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.save_back
                        )
                    )
                    itemWriterBinding.saveIv.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_save_black
                        )
                    )
                    itemWriterBinding.saveIv.startAnimation(saveAnimation)
                    // sPref.edit().putString(writer.fullname, "false").apply()
                    dao.remove(WriterEntity(writer.fullname!!))
                    saved = false
                }
                listener.onItemSaveClick(writer, position)
            }
            itemWriterBinding.root.setOnClickListener {
                listener.onItemClick(writer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemWriterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(writer: Writer)
        fun onItemSaveClick(writer: Writer, position: Int)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun filterList(fList: ArrayList<Writer>) {
        list = fList
        this.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: ArrayList<Writer>, animation:Boolean){
        this.animation = animation
        this.list = list
        this.notifyDataSetChanged()
    }
}