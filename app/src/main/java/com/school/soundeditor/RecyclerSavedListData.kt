package com.school.soundeditor

import android.os.Parcelable
import com.school.soundeditor.main.SuperRecyclerItemData
import kotlinx.android.parcel.Parcelize

@Parcelize
class RecyclerSavedListData : Parcelable {
    var data: MutableList<SuperRecyclerItemData> = mutableListOf()
}