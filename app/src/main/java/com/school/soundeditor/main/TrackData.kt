package com.school.soundeditor.main

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackData(
    val name: String = "",
    val performer: String = "",
    val duration: String = "",
    val format: String = "",
    val image: Int = 0,
    val fileSrc: String? = null
) : Parcelable, SuperRecyclerItemData()