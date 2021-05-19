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
            TYPE_TRACK -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.track_item_layout, parent, false)
                TrackViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_footer, parent, false)
                FooterViewHolder(itemView)
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
            is TrackData -> TYPE_TRACK
            else -> TYPE_FOOTER
        }
    }

    fun addListItem(listItem: BaseData) {
        dataList.data.add(dataList.data.size - 1, listItem)
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
            itemView.findViewById<TextView>(R.id.track_format_text_view).text = itemData.format
            itemView.findViewById<ImageView>(R.id.track_image).setImageResource(itemData.image)
            itemView.findViewById<ImageView>(R.id.add_button).setOnClickListener {
                dataList.data.add(layoutPosition, getNewTrackItem())
                notifyItemInserted(layoutPosition + 1)
            }
            itemView.findViewById<ImageView>(R.id.remove_button).setOnClickListener {
                dataList.data.removeAt(layoutPosition)
                notifyItemRemoved(layoutPosition)
            }
            itemView.findViewById<ImageView>(R.id.move_up_button).setOnClickListener {
                moveUp()
            }
            itemView.findViewById<ImageView>(R.id.move_down_button).setOnClickListener {
                moveDown()
            }
        }

        private fun moveUp() {
            layoutPosition.takeIf { it > 1 }?.also { currentPosition ->
                val element = dataList.data[currentPosition]
                dataList.data.removeAt(currentPosition)
                dataList.data.add(currentPosition - 1, element)
                notifyItemMoved(currentPosition, currentPosition - 1)
            }
        }

        private fun moveDown() {
            layoutPosition.takeIf { it < dataList.data.size - 2 }?.also { currentPosition ->
                val element = dataList.data[currentPosition]
                dataList.data.removeAt(currentPosition)
                dataList.data.add(currentPosition + 1, element)
                notifyItemMoved(currentPosition, currentPosition + 1)
            }
        }

        private fun getNewTrackItem(): BaseData {
            return TrackData(
                "Bohemian Rhapsody",
                "Queen",
                "5:55",
                "Mp3",
                R.drawable.bohemian
            )
        }
    }

    inner class FooterViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(itemData: BaseData) {
            itemView.setOnClickListener {
                listener.onClick(itemData)
            }
        }
    }

    interface OnClickListener {
        fun onClick(itemData: BaseData)
    }

    companion object {
        const val TYPE_HEADER: Int = 0
        const val TYPE_TRACK: Int = 1
        const val TYPE_FOOTER: Int = 3
    }
}
