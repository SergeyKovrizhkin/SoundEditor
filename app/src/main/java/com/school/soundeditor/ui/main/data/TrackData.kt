package com.school.soundeditor.ui.main.data

import android.os.Parcelable
import com.school.soundeditor.ui.audioTrimmerActivity.customAudioViews.SoundFile
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackData(
    val name: String = "",
    val performer: String = "",
    val duration: String = "",
    val image: Int = 0,
    val fileSrc: String = "",
    val soundFile: SoundFile? = null
) : Parcelable
