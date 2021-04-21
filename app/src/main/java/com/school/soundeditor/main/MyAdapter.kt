package com.school.soundeditor.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.school.soundeditor.R

//class vs inner class

class MyAdapter(private val data: List<SuperRecyclerItemData>, val listener: OnClickListener) :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(itemView)
            }
            TYPE_TRACK -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.track_item_layout, parent, false)
                TrackViewHolder(itemView)
            }
            TYPE_MOVIE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_item_layout, parent, false)
                MovieViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_footer, parent, false)
                FooterViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is HeaderData -> TYPE_HEADER
            is TrackData -> TYPE_TRACK
            is MovieData -> TYPE_MOVIE
            else -> TYPE_FOOTER
        }
    }

    inner class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(itemData: SuperRecyclerItemData) {
            itemView.setOnClickListener {
                listener.onClick(itemData)
            }
        }
    }

    inner class TrackViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(itemData: SuperRecyclerItemData) {
            itemData as TrackData
            itemView.setOnClickListener {
                listener.onClick(itemData)
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

    inner class MovieViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(itemData: SuperRecyclerItemData) {
            itemData as MovieData
            itemView.setOnClickListener {
                listener.onClick(itemData)
            }
            itemView.findViewById<TextView>(R.id.movie_name_text_view).text = itemData.name
            itemView.findViewById<TextView>(R.id.movie_producer_text_view).text =
                itemData.producer
            itemView.findViewById<TextView>(R.id.movie_duration_text_view).text =
                itemData.duration
            itemView.findViewById<TextView>(R.id.movie_format_text_view).text = itemData.format
            itemView.findViewById<ImageView>(R.id.movie_image).setImageResource(itemData.image)
            itemView.findViewById<TextView>(R.id.starring_text_view).text = itemData.starring
        }
    }

    inner class FooterViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(itemData: SuperRecyclerItemData) {
            itemView.setOnClickListener {
                listener.onClick(itemData)
            }
        }
    }

    interface OnClickListener {
        fun onClick(itemData: SuperRecyclerItemData)
    }

    companion object {
        const val TYPE_HEADER: Int = 0
        const val TYPE_TRACK: Int = 1
        const val TYPE_MOVIE: Int = 2
        const val TYPE_FOOTER: Int = 3
    }
}
