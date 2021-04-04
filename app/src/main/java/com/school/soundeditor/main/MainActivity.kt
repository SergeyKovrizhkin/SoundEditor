package com.school.soundeditor.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.school.soundeditor.R
import com.school.soundeditor.equalizer.EqualizerFragment
import com.school.soundeditor.playback.PlaybackFragment
import com.school.soundeditor.record.RecordFragment
import kotlinx.android.synthetic.main.activity_main.*


internal class MainActivity : AppCompatActivity(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        openMainFragment()
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_screen_item -> {
                    openMainFragment()
                    true
                }
                R.id.equalizer_item -> {
                    openEqualizerFragment()
                    true
                }
                R.id.to_record_item -> {
                    openRecordFragment()
                    true
                }
                R.id.to_playback_item -> {
                    openPlaybackFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun openMainFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val mainFragment = MainFragment.newInstance("", "")
        transaction.add(R.id.fragment_container, mainFragment)
        transaction.addToBackStack("")
        transaction.commitAllowingStateLoss()
    }

    private fun openEqualizerFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val equalizerFragment = EqualizerFragment.newInstance("MySwitch", "")
        transaction.add(R.id.fragment_container, equalizerFragment)
        transaction.addToBackStack("")
        transaction.commitAllowingStateLoss()
    }

    private fun openRecordFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val recordFragment = RecordFragment.newInstance("", "")
        transaction.add(R.id.fragment_container, recordFragment)
        transaction.addToBackStack("")
        transaction.commitAllowingStateLoss()
    }

    private fun openPlaybackFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val playbackFragment = PlaybackFragment.newInstance("", "")
        transaction.add(R.id.fragment_container, playbackFragment)
        transaction.addToBackStack("")
        transaction.commitAllowingStateLoss()
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(this, mp3, Toast.LENGTH_SHORT).show()
    }
}
