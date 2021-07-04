package com.school.soundeditor.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.school.soundeditor.ui.main.data.TrackData
import com.school.soundeditor.ui.main.listeners.RemoveItemFromInputs
import com.school.soundeditor.ui.main.listeners.SwapItemsInInputs

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(itemData: TrackData)
    abstract fun setRemoveListener (listener: RemoveItemFromInputs)
    abstract fun setSwapListener (listener: SwapItemsInInputs)
}
