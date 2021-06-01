package com.school.soundeditor.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.school.soundeditor.R
import com.school.soundeditor.RecyclerSavedListData
import com.school.soundeditor.ui.audioTrimmerActivity.customAudioViews.WaveformView
import com.school.soundeditor.ui.base.BaseViewHolder
import com.school.soundeditor.ui.main.data.TrackData
import com.school.soundeditor.ui.main.listeners.RemoveItemFromInputs

class MyAdapter(
    val dataList: RecyclerSavedListData,
    val listener: OnClickListener,
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var onRemoveListener: RemoveItemFromInputs? = null

    internal fun setRemoveListener(listener: RemoveItemFromInputs) {
        this.onRemoveListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item_layout, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        onRemoveListener?.let { holder.setRemoveListener(it) }
        holder.onBind(dataList.data[position])
    }

    override fun getItemCount() = dataList.data.size

    override fun getItemViewType(position: Int): Int {
        return when (dataList.data[position]) {
            else -> TYPE_TRACK
        }
    }

    fun addListItem(listItem: TrackData) {
        dataList.data.add(dataList.data.size, listItem)
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private var onRemoveListener: RemoveItemFromInputs? = null
        private lateinit var itemDataForListener: TrackData

        override fun setRemoveListener(listener: RemoveItemFromInputs) {
            this.onRemoveListener = listener
        }

        override fun onBind(itemData: TrackData) {
            itemDataForListener = itemData
            itemView.setOnClickListener {
                listener.onClick(itemData)
            }
            val waveformView = itemView.findViewById<WaveformView>(R.id.audioWaveform)
            waveformView.setSoundFile(itemData.soundFile)
            waveformView.setListener(waveFormListener)

            itemView.findViewById<TextView>(R.id.track_name_text_view).text = itemData.name
            itemView.findViewById<TextView>(R.id.track_performer_text_view).text =
                itemData.performer
            itemView.findViewById<TextView>(R.id.track_duration_text_view).text =
                itemData.duration
            itemView.findViewById<ImageView>(R.id.track_image).setImageResource(itemData.image)
            itemView.findViewById<ImageView>(R.id.remove_button).setOnClickListener {
                dataList.data.removeAt(layoutPosition)
                notifyItemRemoved(layoutPosition)
                onRemoveListener?.onRemove(layoutPosition)
            }
        }

        private val waveFormListener = object : WaveformView.WaveformListener {
            override fun waveformTouchStart(x: Float) {
                listener.onClick(itemDataForListener)
            }

            override fun waveformTouchMove(x: Float) {
                listener.onClick(itemDataForListener)
            }

            override fun waveformTouchEnd() {
                listener.onClick(itemDataForListener)
            }

            override fun waveformFling(x: Float) {
                listener.onClick(itemDataForListener)
            }

            override fun waveformDraw() {
            }

            override fun waveformZoomIn() {
                listener.onClick(itemDataForListener)
            }

            override fun waveformZoomOut() {
                listener.onClick(itemDataForListener)
            }
        }
    }

    interface OnClickListener {
        fun onClick(itemData: TrackData)
    }

    companion object {
        const val TYPE_TRACK: Int = 1
    }
}
