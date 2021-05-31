package com.school.soundeditor.ui.activity

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.school.soundeditor.*
import com.school.soundeditor.ui.equalizer.EqualizerFragment
import com.school.soundeditor.ui.main.MainFragment
import com.school.soundeditor.ui.main.MainScreenView
import com.school.soundeditor.ui.main.data.TrackData
import com.school.soundeditor.ui.main.listeners.OnAddUri
import com.school.soundeditor.ui.main.listeners.OnSaveData
import com.school.soundeditor.ui.main.listeners.OnSaveScrollingPosition
import com.school.soundeditor.ui.playback.PlaybackFragment
import com.school.soundeditor.ui.record.RecorderDialogFragment
import kotlinx.android.synthetic.main.activity_main.*


internal class MainActivity : AppCompatActivity(), MainScreenView, OnEqualizerSave, OnExit,
    RecorderDialogFragment.OnRecordingSavedListener {

    //private val presenter = MainPresenter(this)
    private var dataList = RecyclerSavedListData()
    private var inputs = InputsSavedData()
    private var savedScrollingPosition = 0
    private var itemSelected: TrackData? = null


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
                /*R.id.equalizer_item -> {
                    openEqualizerFragment(transaction)
                }
                R.id.to_record_item -> {
                    openRecordFragment(transaction)
                }*/
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
        //supportFragmentManager.popBackStack()
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
        bottomNavigation.visibility = View.GONE
        val mainFragment = MainFragment.newInstance(dataList, savedScrollingPosition, inputs)
        mainFragment.setListener(object : ShowItemForPlayback {
            override fun onShow(itemData: TrackData) {
                this@MainActivity.itemSelected = itemData
                bottomNavigation.selectedItemId = R.id.to_playback_item
            }
        })
        mainFragment.setOnSaveDataListener(object : OnSaveData {
            override fun onSave(dataList: RecyclerSavedListData, inputs: InputsSavedData) {
                this@MainActivity.dataList = dataList
                this@MainActivity.inputs = inputs
            }
        })
        mainFragment.setOnSaveScrollingPositionListener(object : OnSaveScrollingPosition {
            override fun onSaveScrollingPosition(savedScrollingPosition: Int) {
                this@MainActivity.savedScrollingPosition = savedScrollingPosition
            }
        })
        transaction.replace(R.id.fragment_container, mainFragment, MAIN_FRAGMENT)
    }

    /*private fun openEqualizerFragment(transaction: FragmentTransaction) {
        bottomNavigation.visibility = View.VISIBLE
        val equalizerFragment = EqualizerFragment.newInstance("MySwitch")
        *//*equalizerFragment.setListener(object : OnEqualizerSave {
            override fun onSave(name: String) {
                Toast.makeText(this@MainActivity, name, Toast.LENGTH_SHORT).show()
            }
        })*//*
        transaction.replace(R.id.fragment_container, equalizerFragment, EQUALIZER_FRAGMENT)
    }*/

    /*private fun openRecordFragment(transaction: FragmentTransaction) {
        bottomNavigation.visibility = View.VISIBLE
        val recordFragment = RecordFragment.newInstance()
        transaction.replace(R.id.fragment_container, recordFragment, RECORD_FRAGMENT)
    }*/

    private fun openPlaybackFragment(
        transaction: FragmentTransaction,
        itemData: TrackData?
    ) {
        bottomNavigation.visibility = View.VISIBLE
        val playbackFragment = PlaybackFragment.newInstance(itemData)
        playbackFragment.setOnAddingUriListener(object : OnAddUri {
            override fun onAdd(uri: Uri, values: ContentValues): Uri? {
                val newUri = getContentResolver().insert(uri, values)
                return newUri
            }
        })
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

    override fun onSaved(filePath: String?) {
        //show toast conforming the successful saving of the recorded audio file
        Toast.makeText(
            this,
            "Audio file saved",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onFailed() {
        //show toast saying that the saving of the recorded audio file failed
        Toast.makeText(
            this,
            "Audio failed to save",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val MAIN_FRAGMENT = "MAIN_FRAGMENT"
        private const val EQUALIZER_FRAGMENT = "EQUALIZER_FRAGMENT"
        private const val RECORD_FRAGMENT = "RECORD_FRAGMENT"
        private const val PLAYBACK_FRAGMENT = "PLAYBACK_FRAGMENT"
        public const val OUTPUT_DIR = "SchoolSoundEditor/"
    }

    /*fun sampleUsage(){
        val input1: AudioInput = GeneralAudioInput(input1Path)
        input1.setVolume(0.5f) //Optional

        // It will produce a blank portion of 3 seconds between input1 and input2 if mixing type is sequential.
        // But it will does nothing in parallel mixing.
        // It will produce a blank portion of 3 seconds between input1 and input2 if mixing type is sequential.
        // But it will does nothing in parallel mixing.
        val blankInput: AudioInput = BlankAudioInput(3000000) //

        val input2: AudioInput = GeneralAudioInput(this, input2Uri, null)
        input2.startTimeUs = 3000000 //Optional

        input2.endTimeUs = 9000000 //Optional

        //input2.setStartOffsetUs(5000000) //Optional. It is needed to start mixing the input at a certain time.

        val outputPath = (Environment.getDownloadCacheDirectory().absolutePath
                + "/" + "audio_mixer_output.mp3") // for example

        val audioMixer = AudioMixer(outputPath)
        audioMixer.addDataSource(input1)
        audioMixer.addDataSource(blankInput)
        audioMixer.addDataSource(input2)
        audioMixer.setSampleRate(44100) // Optional

        audioMixer.setBitRate(128000) // Optional

        audioMixer.setChannelCount(2) // Optional //1(mono) or 2(stereo)


        // Smaller audio inputs will be encoded from start-time again if it reaches end-time
        // It is only valid for parallel mixing
        //audioMixer.setLoopingEnabled(true);

        // Smaller audio inputs will be encoded from start-time again if it reaches end-time
        // It is only valid for parallel mixing
        //audioMixer.setLoopingEnabled(true);
        audioMixer.setMixingType(AudioMixer.MixingType.PARALLEL) // or AudioMixer.MixingType.SEQUENTIAL

        audioMixer.setProcessingListener(object : AudioMixer.ProcessingListener {
            override fun onProgress(progress: Double) {
                runOnUiThread { *//*progressDialog.setProgress((progress * 100).toInt())*//* }
            }

            override fun onEnd() {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Success!!!", Toast.LENGTH_SHORT).show()
                    audioMixer.release()
                }
            }
        })


        //it is for setting up the all the things


        //it is for setting up the all the things
        audioMixer.start()

        *//* These getter methods must be called after calling 'start()'*//*
        //audioMixer.getOutputSampleRate();
        //audioMixer.getOutputBitRate();
        //audioMixer.getOutputChannelCount();
        //audioMixer.getOutputDurationUs();

        //starting real processing

        *//* These getter methods must be called after calling 'start()'*//*
        //audioMixer.getOutputSampleRate();
        //audioMixer.getOutputBitRate();
        //audioMixer.getOutputChannelCount();
        //audioMixer.getOutputDurationUs();

        //starting real processing
        audioMixer.processAsync()

        // We can stop the processing immediately by calling audioMixer.stop() when we want.

        // audioMixer.processSync() is generally not used.
        // We have to use this carefully.
        // Tt will do the processing in caller thread
        // And calling audioMixer.stop() from the same thread won't stop the processing

    }*/
}
