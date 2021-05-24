package com.school.soundeditor.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.school.soundeditor.R
import com.school.soundeditor.RecyclerSavedListData
import com.school.soundeditor.ui.base.BaseViewHolder
import com.school.soundeditor.ui.main.data.BaseData
import com.school.soundeditor.ui.main.data.HeaderData
import com.school.soundeditor.ui.main.data.TrackData

//class vs inner class

class MyAdapter(
    val dataList: RecyclerSavedListData,
    val listener: OnClickListener,
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.track_item_layout, parent, false)
                TrackViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(dataList.data[position])
    }

    override fun getItemCount() = dataList.data.size

    override fun getItemViewType(position: Int): Int {
        return when (dataList.data[position]) {
            is HeaderData -> TYPE_HEADER
            else -> TYPE_TRACK
        }
    }

    fun addListItem(listItem: BaseData) {
        dataList.data.add(dataList.data.size, listItem)
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(itemData: BaseData) {
            itemView.setOnClickListener {
                listener.onClick(itemData)
            }
        }
    }

    inner class TrackViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(itemData: BaseData) {
            itemData as TrackData
            itemView.setOnClickListener {
                listener.onClick(itemData)
            }
            itemView.findViewById<TextView>(R.id.track_name_text_view).text = itemData.name
            itemView.findViewById<TextView>(R.id.track_performer_text_view).text =
                itemData.performer
            itemView.findViewById<TextView>(R.id.track_duration_text_view).text =
                itemData.duration
            itemView.findViewById<ImageView>(R.id.track_image).setImageResource(itemData.image)
            itemView.findViewById<ImageView>(R.id.remove_button).setOnClickListener {
                dataList.data.removeAt(layoutPosition)
                notifyItemRemoved(layoutPosition)
            }
        }
    }

    interface OnClickListener {
        fun onClick(itemData: BaseData)
    }

    companion object {
        const val TYPE_HEADER: Int = 0
        const val TYPE_TRACK: Int = 1
    }
}
