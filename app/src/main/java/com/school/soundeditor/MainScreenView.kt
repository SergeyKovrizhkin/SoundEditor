package com.school.soundeditor

internal interface MainScreenView {
    fun showTrack(mp3: String)
    fun showEqualizer()
    fun openRecordScreen()
    fun openPlaybackScreen()
}
