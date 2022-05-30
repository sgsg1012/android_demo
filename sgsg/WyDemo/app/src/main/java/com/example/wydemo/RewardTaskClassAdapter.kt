package com.example.wydemo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RewardTaskClassAdapter(val rewardTaskList: ArrayList<RewardTaskClass>) :
    RecyclerView.Adapter<RewardTaskClassAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView = view.findViewById(R.id.taskTitle)
        val taskTime: TextView = view.findViewById(R.id.taskTime)
        val tagArea: LinearLayout = view.findViewById(R.id.tagArea)
        val tag1: LinearLayout = view.findViewById(R.id.tag1)
        val tag1Text: TextView = view.findViewById(R.id.tag1Text)
        val tag2: LinearLayout = view.findViewById(R.id.tag2)
        val tag2Text: TextView = view.findViewById(R.id.tag2Text)
        val tag3: LinearLayout = view.findViewById(R.id.tag3)
        val tag3Text: TextView = view.findViewById(R.id.tag3Text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reward_task, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val rewardTask = rewardTaskList[position]
            val intent = Intent(parent.context, RewardTaskContentActivity::class.java).apply {
                putExtra("projectId", rewardTask.projectId)
            }
            parent.context.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rewardTask = rewardTaskList[position]
        holder.taskTitle.setText(rewardTask.title)
        holder.taskTime.setText(rewardTask.time)
        val len = rewardTask.tags.size
        if (len > 0) {
            holder.tagArea.visibility = View.VISIBLE
            holder.tag1.visibility = View.VISIBLE
            holder.tag1Text.setText(rewardTask.tags[0])
            if (len > 1) {
                holder.tag2.visibility = View.VISIBLE
                holder.tag2Text.setText(rewardTask.tags[1])
                if (len > 2) {
                    holder.tag3.visibility = View.VISIBLE
                    holder.tag3Text.setText(rewardTask.tags[2])
                } else {
                    holder.tag3.visibility = View.GONE
                }
            } else {
                holder.tag2.visibility = View.GONE
                holder.tag3.visibility = View.GONE
            }
        } else holder.tagArea.visibility = View.GONE
    }

    override fun getItemCount() = rewardTaskList.size


}