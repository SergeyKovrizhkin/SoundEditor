package com.school.soundeditor.main

internal class MainPresenter(private val view: MainScreenView) : MainScreenPresenter {

    override fun onLoadTrack() {
        //Загружаем трек в отдельном потоке
        view.showTrack("Bohemian Rhapsody")
    }
}
