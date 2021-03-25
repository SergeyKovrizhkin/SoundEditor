package com.school.soundeditor.record

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.school.soundeditor.Navigator
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.activity_track_record.*

class RecordActivity : AppCompatActivity(), RecordScreenView {

    private val presenter: RecordScreenPresenter = RecordPresenter(this)
    private lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_record)
        initViews()
    }

    private fun initViews() {
        navigator = Navigator(this)
        bottomNavigationRecordScreen.selectedItemId = R.id.to_record_item
        bottomNavigationRecordScreen.setOnNavigationItemSelectedListener { item ->
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
                    true
                }
                R.id.to_playback_item -> {
                    navigator.openPlaybackScreen()
                    true
                }
                else -> false
            }
        }
        recordSpaceForHello.text = intent.getStringExtra("message")
    }

    override fun onRestart() {
        super.onRestart()
        bottomNavigationRecordScreen.selectedItemId = R.id.to_record_item
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RecordActivity::class.java)
        }
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(this, mp3, Toast.LENGTH_SHORT).show()
    }
}