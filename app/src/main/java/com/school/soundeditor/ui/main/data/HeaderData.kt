package com.school.soundeditor.ui.main.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HeaderData(val name: String = "HEADER") : Parcelable, BaseData()
