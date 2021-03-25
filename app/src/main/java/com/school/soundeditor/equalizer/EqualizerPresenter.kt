package com.school.soundeditor.equalizer

internal class EqualizerPresenter(private val view: EqualizerScreenView) :
    EqualizerScreenPresenter {
    override fun onLoadTrack() {
        view.showTrack("Bohemian Rhapsody on equalize")
    }
}