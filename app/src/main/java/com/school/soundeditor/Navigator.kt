package com.school.soundeditor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

internal object Navigator {

    internal fun packAndStart(context: AppCompatActivity, currentIntent: Intent) {
        currentIntent.putExtra("message", "Hello from ${context.localClassName}")
        context.startActivity(currentIntent)
    }
}