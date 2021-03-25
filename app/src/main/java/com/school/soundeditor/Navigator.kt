package com.school.soundeditor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

internal object Navigator {

    /*internal fun showMainScreen(context: AppCompatActivity) {
        packAndStart(context, MainActivity.getIntent(context))
    }

    fun showEqualizer(context: AppCompatActivity) {
        packAndStart(context, EqualizerActivity.getIntent(context))
    }

    fun openRecordScreen() {
        currentIntent = RecordActivity.getIntent(currentContext)
        packAndStart()
    }

    fun openPlaybackScreen() {
        currentIntent = PlaybackActivity.getIntent(currentContext)
        packAndStart()
    }*/

    internal fun packAndStart(context: AppCompatActivity, currentIntent: Intent) {
        currentIntent.putExtra("message", "Hello from ${context.localClassName}")
        context.startActivity(currentIntent)
    }
}
