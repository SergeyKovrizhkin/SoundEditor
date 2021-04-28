package com.school.soundeditor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.school.soundeditor.equalizer.EqualizerFragment
import com.school.soundeditor.main.*
import com.school.soundeditor.playback.PlaybackFragment
import com.school.soundeditor.record.RecordFragment
import kotlinx.android.synthetic.main.activity_main.*

internal class MainActivity : AppCompatActivity(), MainScreenView, OnEqualizerSave, OnExit {

    private val presenter = MainPresenter(this)
    private var dataList = RecyclerSavedListData()
    private var savedScrollingPosition = 0
    private var itemSelected: SuperRecyclerItemData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        var transaction = supportFragmentManager.beginTransaction()
        openMainFragment(transaction, null, savedScrollingPosition)
        transaction.commitAllowingStateLoss()
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            transaction = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.main_screen_item -> {
                    openMainFragment(transaction, dataList, savedScrollingPosition)
                }
                R.id.equalizer_item -> {
                    openEqualizerFragment(transaction)
                }
                R.id.to_record_item -> {
                    openRecordFragment(transaction)
                }
                R.id.to_playback_item -> {
                    openPlaybackFragment(transaction, itemSelected)
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
    }

    /*private fun getVisibleFragment(): Fragment? {
        var visibleFragment: Fragment? = null
        supportFragmentManager.fragments.forEach {
            if (it.isVisible) {
                visibleFragment = it
            }
        }
        return visibleFragment
    }*/

    private fun openMainFragment(
        transaction: FragmentTransaction,
        dataList: RecyclerSavedListData?,
        savedScrollingPosition: Int
    ) {
        val mainFragment = MainFragment.newInstance(dataList, savedScrollingPosition)
        mainFragment.setListener(object : ShowItemForPlayback {
            override fun onShow(itemData: SuperRecyclerItemData) {
                if (itemData is TrackData || itemData is MovieData) {
                    this@MainActivity.itemSelected = itemData
                    bottomNavigation.selectedItemId = R.id.to_playback_item
                }
            }
        })
        mainFragment.setOnSaveDataListener(object : OnSaveData {
            override fun onSave(dataList: RecyclerSavedListData) {
                this@MainActivity.dataList = dataList
            }
        })
        mainFragment.setOnSaveScrollingPositionListener(object : OnSaveScrollingPosition {
            override fun onSaveScrollingPosition(savedScrollingPosition: Int) {
                this@MainActivity.savedScrollingPosition = savedScrollingPosition
            }
        })
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

    private fun openPlaybackFragment(
        transaction: FragmentTransaction,
        itemData: SuperRecyclerItemData?
    ) {
        val playbackFragment = PlaybackFragment.newInstance(itemData)
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
