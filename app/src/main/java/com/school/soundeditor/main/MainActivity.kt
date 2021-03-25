package com.school.soundeditor.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.school.soundeditor.Navigator
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.activity_main.*


internal class MainActivity : AppCompatActivity(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)
    private lateinit var navigator: Navigator

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
        navigator = Navigator(this)
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
                    navigator.showEqualizer()
                    true
                }
                R.id.to_record_item -> {
                    navigator.openRecordScreen()
                    true
                }
                R.id.to_playback_item -> {
                    navigator.openPlaybackScreen()
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
