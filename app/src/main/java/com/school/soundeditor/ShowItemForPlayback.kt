package com.school.soundeditor

import com.school.soundeditor.ui.main.data.TrackData

interface ShowItemForPlayback {
    fun onShow(itemData: TrackData)
}
