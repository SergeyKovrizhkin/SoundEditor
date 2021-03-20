package com.school.soundeditor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_record)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RecordActivity::class.java)
        }
    }
}