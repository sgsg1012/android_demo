package com.example.wydemo

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class LectureAdapter(val lectureList: List<Lecture>) :
    RecyclerView.Adapter<LectureAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lectureContentTitle: TextView = view.findViewById(R.id.lectureContentTitle)
        val lectureContentTime: TextView = view.findViewById(R.id.lectureContentTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lecture, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val lecture = lectureList[position]
            val intent = Intent(parent.context, LectureContentActivity::class.java).apply {
                putExtra("time", lecture.time)
                putExtra("title", lecture.title)
            }
            parent.context.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lecture = lectureList[position]
        holder.lectureContentTitle.setText(lecture.title)
        holder.lectureContentTime.setText(lecture.time)
    }

    override fun getItemCount() = lectureList.size
}