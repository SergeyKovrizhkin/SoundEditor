package com.school.soundeditor

import android.os.Parcelable
import com.school.soundeditor.ui.main.data.TrackData
import kotlinx.android.parcel.Parcelize

@Parcelize
class RecyclerSavedListData : Parcelable {
    var data: MutableList<TrackData> = mutableListOf()
}
