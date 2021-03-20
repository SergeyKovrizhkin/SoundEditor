package com.school.soundeditor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

//Почитать про ModelViewPresenter и MVVM
//Почитать про Clean Architecture и SOLID
//Git

internal class MainActivity : AppCompatActivity(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        loadTrackButton.setOnClickListener {
            presenter.onLoadTrack()
        }
        showEqualizerButton.setOnClickListener {
            showEqualizer()
        }
        recordButton.setOnClickListener {
            openRecordScreen()
        }
        playbackButton.setOnClickListener {
            openPlaybackScreen()
        }
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(this, mp3, Toast.LENGTH_SHORT).show()
    }

    override fun showEqualizer() {
        AlertDialog.Builder(this)
            .setTitle("Title")
            .setMessage("message")
            .setPositiveButton("OK") { dialog, view ->
                Toast.makeText(this, "Closed", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Cancel", null)
            .create()
            .show()
    }

    override fun openRecordScreen() {
        startActivity(RecordActivity.getIntent(this))
    }

    override fun openPlaybackScreen() {
        startActivity(PlaybackActivity.getIntent(this))
    }
}
