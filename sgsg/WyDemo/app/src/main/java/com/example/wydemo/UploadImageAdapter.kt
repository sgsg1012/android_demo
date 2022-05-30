package com.example.wydemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_uploadimage.view.*

interface OnClickCallback2 {
    fun onClick(view: View, position: Int,type:Int)
}

class UploadImageAdapter(
    val UploadImageList: ArrayList<UploadImage>,
    val monClickCallback: OnClickCallback2,
) :
    RecyclerView.Adapter<UploadImageAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.image
        val remove = view.remove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_uploadimage, parent, false)

        val viewHolder = ViewHolder(view)
        viewHolder.image.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            monClickCallback.onClick(viewHolder.image, position,0)
        }
        viewHolder.remove.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            monClickCallback.onClick(viewHolder.remove, position,1)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uploadImage = UploadImageList[position]
        holder.image.setImageBitmap(uploadImage.bitmap)
    }

    override fun getItemCount(): Int = UploadImageList.size
}