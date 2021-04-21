package com.school.soundeditor.main

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HeaderData(val name: String = "HEADER") : Parcelable, SuperRecyclerItemData()