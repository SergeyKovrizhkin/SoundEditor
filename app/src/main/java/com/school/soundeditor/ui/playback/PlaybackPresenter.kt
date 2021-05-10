package com.school.soundeditor.ui.playback

internal class PlaybackPresenter(private val view: PlaybackScreenView) : PlaybackScreenPresenter {
    override fun onLoadTrack() {
        view.showTrack("Bohemian Rhapsody on play")
    }
}
