package com.school.soundeditor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.school.soundeditor.equalizer.EqualizerFragment
import com.school.soundeditor.main.MainFragment
import com.school.soundeditor.main.MainPresenter
import com.school.soundeditor.main.MainScreenPresenter
import com.school.soundeditor.main.MainScreenView
import com.school.soundeditor.playback.PlaybackFragment
import com.school.soundeditor.record.RecordFragment
import kotlinx.android.synthetic.main.activity_main.*


internal class MainActivity : AppCompatActivity(), MainScreenView, OnEqualizerSave, OnExit {

    private val presenter: MainScreenPresenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        var transaction = supportFragmentManager.beginTransaction()
        openMainFragment(transaction)
        transaction.commitAllowingStateLoss()
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            transaction = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.main_screen_item -> {
                    openMainFragment(transaction)
                }
                R.id.equalizer_item -> {
                    openEqualizerFragment(transaction)
                }
                R.id.to_record_item -> {
                    openRecordFragment(transaction)
                }
                R.id.to_playback_item -> {
                    openPlaybackFragment(transaction)
                }
                else -> {
                }
            }
            transaction.commitAllowingStateLoss()
            true
        }
    }

    override fun onBackPressed() {
        ExitAppDialogFragment().show(supportFragmentManager, "")
        //super.onBackPressed()
        //if (supportFragmentManager.fragments.isEmpty()) {
        //    finish()
        //}
        /*when (getVisibleFragment()) {
            is MainFragment -> bottomNavigation.menu.findItem(R.id.main_screen_item).isChecked =
                true
            is EqualizerFragment -> bottomNavigation.menu.findItem(R.id.equalizer_item).isChecked =
                true
            is RecordFragment -> bottomNavigation.menu.findItem(R.id.to_record_item).isChecked =
                true
            is PlaybackFragment -> bottomNavigation.menu.findItem(R.id.to_playback_item).isChecked =
                true
        }*/
        /*
        val fragment = supportFragmentManager.findFragmentByTag(RECORD_FRAGMENT)
        if (supportFragmentManager.findFragmentByTag(RECORD_FRAGMENT) != null) {
            finish()
        }*/
    }

    private fun getVisibleFragment(): Fragment? {
        var visibleFragment: Fragment? = null
        supportFragmentManager.fragments.forEach {
            if (it.isVisible) {
                visibleFragment = it
            }
        }
        return visibleFragment
    }

    private fun openMainFragment(transaction: FragmentTransaction) {
        val mainFragment = MainFragment.newInstance()
        transaction.replace(R.id.fragment_container, mainFragment, MAIN_FRAGMENT)
    }

    private fun openEqualizerFragment(transaction: FragmentTransaction) {
        val equalizerFragment = EqualizerFragment.newInstance("MySwitch")
        /*equalizerFragment.setListener(object : OnEqualizerSave {
            override fun onSave(name: String) {
                Toast.makeText(this@MainActivity, name, Toast.LENGTH_SHORT).show()
            }
        })*/
        transaction.replace(R.id.fragment_container, equalizerFragment, EQUALIZER_FRAGMENT)
    }

    private fun openRecordFragment(transaction: FragmentTransaction) {
        val recordFragment = RecordFragment.newInstance()
        transaction.replace(R.id.fragment_container, recordFragment, RECORD_FRAGMENT)
    }

    private fun openPlaybackFragment(transaction: FragmentTransaction) {
        val playbackFragment = PlaybackFragment.newInstance(Data(""))
        transaction.replace(R.id.fragment_container, playbackFragment, PLAYBACK_FRAGMENT)
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

    override fun onExit() {
        finish()
    }

    companion object {
        private const val MAIN_FRAGMENT = "MAIN_FRAGMENT"
        private const val EQUALIZER_FRAGMENT = "EQUALIZER_FRAGMENT"
        private const val RECORD_FRAGMENT = "RECORD_FRAGMENT"
        private const val PLAYBACK_FRAGMENT = "PLAYBACK_FRAGMENT"
    }
}
