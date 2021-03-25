package com.school.soundeditor.playback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.school.soundeditor.Navigator
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.activity_track_playback.*

class PlaybackActivity : AppCompatActivity(), PlaybackScreenView {

    private val presenter: PlaybackScreenPresenter = PlaybackPresenter(this)
    private lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_playback)
        initViews()
    }

    private fun initViews() {
        navigator = Navigator(this)
        bottomNavigationPlaybackScreen.selectedItemId = R.id.to_playback_item
        bottomNavigationPlaybackScreen.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_screen_item -> {
                    navigator.showMainScreen()
                    true
                }
                R.id.equalizer_item -> {
                    navigator.showEqualizer()
                    true
                }
                R.id.to_record_item -> {
                    navigator.openRecordScreen()
                    true
                }
                R.id.to_playback_item -> {
                    true
                }
                else -> false
            }
        }
        playbackSpaceForHello.text = intent.getStringExtra("message")
    }

    override fun onRestart() {
        super.onRestart()
        bottomNavigationPlaybackScreen.selectedItemId = R.id.to_playback_item
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, PlaybackActivity::class.java)
        }
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(this, mp3, Toast.LENGTH_SHORT).show()
    }
}