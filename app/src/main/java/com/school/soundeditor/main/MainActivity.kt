package com.school.soundeditor.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.school.soundeditor.Navigator
import com.school.soundeditor.R
import com.school.soundeditor.equalizer.EqualizerActivity
import com.school.soundeditor.playback.PlaybackActivity
import com.school.soundeditor.record.RecordActivity
import kotlinx.android.synthetic.main.activity_main.*


internal class MainActivity : AppCompatActivity(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationMainScreen.selectedItemId = R.id.main_screen_item
    }

    private fun initViews() {
        loadTrackButton.setOnClickListener {
            presenter.onLoadTrack()
        }
        bottomNavigationMainScreen.selectedItemId = R.id.main_screen_item
        bottomNavigationMainScreen.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_screen_item -> {
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
                    Navigator.packAndStart(this, PlaybackActivity.getIntent(this))
                    true
                }
                else -> false
            }
        }
        mainSpaceForHello.text = intent.getStringExtra("message")
    }

    override fun onRestart() {
        super.onRestart()
        bottomNavigationMainScreen.selectedItemId = R.id.main_screen_item
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(this, mp3, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
