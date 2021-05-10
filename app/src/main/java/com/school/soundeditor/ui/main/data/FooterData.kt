package com.school.soundeditor.ui.main.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FooterData(val name: String = "FOOTER") : Parcelable, BaseData()
