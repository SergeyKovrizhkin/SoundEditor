package com.school.soundeditor.ui.equalizer

import com.school.soundeditor.ui.equalizer.interfaces.EqualizerScreenPresenter
import com.school.soundeditor.ui.equalizer.interfaces.EqualizerScreenView

internal class EqualizerPresenter(private val view: EqualizerScreenView) :
    EqualizerScreenPresenter {
    override fun onLoadTrack() {
        view.showTrack("Bohemian Rhapsody on equalize")
    }
}
