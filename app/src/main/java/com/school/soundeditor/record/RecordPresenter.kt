package com.school.soundeditor.record

internal class RecordPresenter(private val view: RecordScreenView) : RecordScreenPresenter {
    override fun onLoadTrack() {
        view.showTrack("Bohemian Rhapsody on record")
    }
}