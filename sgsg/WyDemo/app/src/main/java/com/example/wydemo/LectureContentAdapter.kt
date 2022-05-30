package com.example.wydemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

interface OnClickCallback {
    fun onClick(view: View, position: Int)
}

class LectureContentAdapter(
    val lectureContentList: List<LectureContent>,
    val monClickCallback: OnClickCallback,
) :
    RecyclerView.Adapter<LectureContentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val college: TextView = view.findViewById(R.id.college)
        val image: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lecture_content, parent, false)

        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            monClickCallback.onClick(viewHolder.itemView, position)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lectureContent = lectureContentList[position]
        holder.college.setText(lectureContent.college)
        Glide.with(holder.itemView)
            .load("http://"+lectureContent.url)
            .into(holder.image)
    }

    override fun getItemCount(): Int = lectureContentList.size
}