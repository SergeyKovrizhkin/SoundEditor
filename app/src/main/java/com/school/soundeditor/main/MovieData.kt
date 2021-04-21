package com.school.soundeditor.main

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieData(
    val name: String = "",
    val producer: String = "",
    val duration: String = "",
    val format: String = "",
    val image: Int = 0,
    val starring: String = ""
) : Parcelable, SuperRecyclerItemData()