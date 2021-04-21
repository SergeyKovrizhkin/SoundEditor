package com.school.soundeditor.main

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FooterData(val name: String = "FOOTER") : Parcelable, SuperRecyclerItemData()