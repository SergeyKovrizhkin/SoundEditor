package com.school.soundeditor.playback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.school.soundeditor.Navigator
import com.school.soundeditor.R
import com.school.soundeditor.equalizer.EqualizerActivity
import com.school.soundeditor.main.MainActivity
import com.school.soundeditor.record.RecordActivity
import kotlinx.android.synthetic.main.activity_track_playback.*

internal class PlaybackActivity : AppCompatActivity(), PlaybackScreenView {

    private val presenter: PlaybackScreenPresenter = PlaybackPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_playback)
        initViews()
    }

    private fun initViews() {
        bottomNavigationPlaybackScreen.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_screen_item -> {
                    Navigator.packAndStart(this, MainActivity.getIntent(this).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                    true
                }
                R.id.equalizer_item -> {
                    Navigator.packAndStart(this, EqualizerActivity.getIntent(this))
                    true
                }
                R.id.to_record_item -> {
                    Navigator.packAndStart(this, RecordActivity.getIntent(this))
                    true
                }
                R.id.to_playback_item -> {
                    true
                }
                else -> false
            }
        }
        //playbackSpaceForHello.text = intent.getStringExtra("message")
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationPlaybackScreen.selectedItemId = R.id.to_playback_item
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(this, mp3, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, PlaybackActivity::class.java)
        }
    }
}
