package com.school.soundeditor

internal class MainPresenter(private val view: MainScreenView) : MainScreenPresenter {

    override fun onLoadTrack() {
        //Загружаем трек в отдельном потоке
        view.showTrack("Bohemian Rhapsody")
    }
}
