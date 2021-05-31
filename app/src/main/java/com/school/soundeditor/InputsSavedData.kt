package com.school.soundeditor

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class InputsSavedData : Parcelable {
    var data: MutableList<Uri> = mutableListOf()
}