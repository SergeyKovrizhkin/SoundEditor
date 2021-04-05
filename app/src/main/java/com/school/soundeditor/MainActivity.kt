package com.school.soundeditor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.school.soundeditor.equalizer.EqualizerFragment
import com.school.soundeditor.main.MainFragment
import com.school.soundeditor.main.MainPresenter
import com.school.soundeditor.main.MainScreenPresenter
import com.school.soundeditor.main.MainScreenView
import com.school.soundeditor.playback.PlaybackFragment
import com.school.soundeditor.record.RecordFragment
import kotlinx.android.synthetic.main.activity_main.*


internal class MainActivity : AppCompatActivity(), MainScreenView, OnEqualizerSave {

    private val presenter: MainScreenPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        val transaction = supportFragmentManager.beginTransaction()
        openMainFragment(transaction)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_screen_item -> {
                    openMainFragment(transaction)
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
        transaction.addToBackStack("")
        transaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.fragments.isEmpty()) {
            finish()
        }
        /*
        val fragment = supportFragmentManager.findFragmentByTag(RECORD_FRAGMENT)
        if (supportFragmentManager.findFragmentByTag(RECORD_FRAGMENT) != null) {
            finish()
        }*/
    }

    private fun openMainFragment(transaction: FragmentTransaction) {
        val mainFragment = MainFragment.newInstance("", "")
        transaction.add(R.id.fragment_container, mainFragment, MAIN_FRAGMENT)
    }

    private fun openEqualizerFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val equalizerFragment = EqualizerFragment.newInstance("MySwitch", "")
        /*equalizerFragment.setListener(object : OnEqualizerSave {
            override fun onSave(name: String) {
                Toast.makeText(this@MainActivity, name, Toast.LENGTH_SHORT).show()
            }
        })*/
        transaction.add(R.id.fragment_container, equalizerFragment, EQUALIZER_FRAGMENT)
        transaction.addToBackStack("")
        transaction.commitAllowingStateLoss()
    }

    private fun openRecordFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val recordFragment = RecordFragment.newInstance()
        transaction.add(R.id.fragment_container, recordFragment, RECORD_FRAGMENT)
        transaction.addToBackStack("")
        transaction.commitAllowingStateLoss()
    }

    private fun openPlaybackFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val playbackFragment = PlaybackFragment.newInstance(Data(""))
        transaction.add(R.id.fragment_container, playbackFragment, PLAYBACK_FRAGMENT)
        transaction.addToBackStack("")
        transaction.commitAllowingStateLoss()
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(this, mp3, Toast.LENGTH_SHORT).show()
    }

    override fun onSave(name: String) {
        //Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
        val fragment = supportFragmentManager.findFragmentByTag(EQUALIZER_FRAGMENT)
        if (fragment != null && fragment is EqualizerFragment) {
            fragment.showData(Data(name))
        }
    }

    companion object {
        private const val MAIN_FRAGMENT = "MAIN_FRAGMENT"
        private const val EQUALIZER_FRAGMENT = "EQUALIZER_FRAGMENT"
        private const val RECORD_FRAGMENT = "RECORD_FRAGMENT"
        private const val PLAYBACK_FRAGMENT = "PLAYBACK_FRAGMENT"
    }
}
