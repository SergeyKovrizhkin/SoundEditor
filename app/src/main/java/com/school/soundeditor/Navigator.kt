package com.school.soundeditor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.school.soundeditor.equalizer.EqualizerActivity
import com.school.soundeditor.main.MainActivity
import com.school.soundeditor.playback.PlaybackActivity
import com.school.soundeditor.record.RecordActivity.Companion as RecordActivity

class Navigator(private val view: AppCompatActivity) {

    private val currentContext = view.applicationContext
    private lateinit var currentIntent: Intent

    fun showMainScreen() {
        currentIntent = MainActivity.getIntent(currentContext)
        packAndStart()
    }

    fun showEqualizer() {
        currentIntent = EqualizerActivity.getIntent(currentContext)
        packAndStart()
    }

    fun openRecordScreen() {
        currentIntent = RecordActivity.getIntent(currentContext)
        packAndStart()
    }

    fun openPlaybackScreen() {
        currentIntent = PlaybackActivity.getIntent(currentContext)
        packAndStart()
    }

    private fun packAndStart() {
        currentIntent.putExtra("message", "Hello from ${view.localClassName}")
        startActivity(currentContext, currentIntent, null)
    }
}