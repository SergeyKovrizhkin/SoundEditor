package com.school.soundeditor.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.school.soundeditor.R

//class vs inner class

class MyAdapter(private val data: List<TrackData>, val listener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_layout, parent, false)
                MyViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            TYPE_HEADER -> {
                holder as HeaderViewHolder
                holder.onBind()
            }
            else -> {
                holder as MyViewHolder
                holder.onBind(data[position])
            }
        }
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return if (position == TYPE_HEADER) {
            TYPE_HEADER
        } else {
            TYPE_ELEMENT
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(itemData: TrackData) {
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, itemViewType.toString(), Toast.LENGTH_SHORT).show()
                //listener.onClick(itemData)
            }
            itemView.findViewById<TextView>(R.id.track_name_text_view).text = itemData.name
            itemView.findViewById<TextView>(R.id.track_performer_text_view).text =
                itemData.performer
            itemView.findViewById<TextView>(R.id.track_duration_text_view).text =
                itemData.duration
            itemView.findViewById<TextView>(R.id.track_format_text_view).text = itemData.format
            itemView.findViewById<ImageView>(R.id.track_image).setImageResource(itemData.image)
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind() {
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, itemViewType.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface OnClickListener {
        fun onClick(itemData: TrackData)
    }

    companion object {
        const val TYPE_HEADER: Int = 0
        const val TYPE_ELEMENT: Int = 1
    }
}
