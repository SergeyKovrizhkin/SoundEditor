package com.school.soundeditor.ui.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.school.soundeditor.R
import com.school.soundeditor.RecyclerSavedListData
import com.school.soundeditor.ShowItemForPlayback
import com.school.soundeditor.ui.audioTrimmerActivity.AudioTrimmerActivity
import com.school.soundeditor.ui.audioTrimmerActivity.customAudioViews.SoundFile
import com.school.soundeditor.ui.main.data.TrackData
import com.school.soundeditor.ui.main.listeners.OnSaveData
import com.school.soundeditor.ui.main.listeners.OnSaveScrollingPosition
import com.school.soundeditor.ui.main.mixer.MixerHelper
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


internal class MainFragment : Fragment(), MainScreenView {

    //private val presenter: MainScreenPresenter = MainPresenter(this)
    private var listener: ShowItemForPlayback? = null
    private lateinit var dataList: RecyclerSavedListData
    private var savedScrollingPosition = 0
    private var onSaveDataListener: OnSaveData? = null
    private var onSaveScrollingPositionListener: OnSaveScrollingPosition? = null
    private lateinit var adapter: MyAdapter

    private val inputs: MutableList<Uri> = ArrayList()

    internal fun setListener(listener: ShowItemForPlayback) {
        this.listener = listener
    }

    internal fun setOnSaveDataListener(onSaveDataListener: OnSaveData) {
        this.onSaveDataListener = onSaveDataListener
    }

    internal fun setOnSaveScrollingPositionListener(onSaveScrollingPositionListener: OnSaveScrollingPosition) {
        this.onSaveScrollingPositionListener = onSaveScrollingPositionListener
    }

    override fun onDetach() {
        listener = null
        onSaveDataListener = null
        onSaveScrollingPositionListener = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            dataList = it.getParcelable(ARG_PARAM1) ?: getTrackList()
            savedScrollingPosition = it.getInt(ARG_PARAM2)
        }
        initView()
    }

    private fun initView() {
        adapter = MyAdapter(dataList, object : MyAdapter.OnClickListener {
            override fun onClick(itemData: TrackData) {
                listener?.onShow(itemData)
            }
        })
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        recyclerView.scrollToPosition(savedScrollingPosition)

        add_button.setOnClickListener {
            //checkPermission()
            checkPermissionReadStorage(this)
        }
        record_button.setOnClickListener {
            checkMicrophone(this)
        }
        saveButton.setOnClickListener {
            MixerHelper.startMixing(requireActivity(), inputs)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_EXTERNAL_STORAGE -> {
                // Проверяем, дано ли пользователем разрешение по нашему запросу
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    activity?.let {
                        chooseFile(this)
                    }
                } else {
                    // Поясните пользователю, что экран останется пустым, потому что доступ к контактам не предоставлен
                    showOnRejectedStorageDialog(this)
                }
                return
            }
            REQUEST_MICROPHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(
                        Intent(requireContext(), AudioTrimmerActivity::class.java),
                        ADD_AUDIO
                    )
                } else {
                    showOnRejectedMicrophoneDialog(this)
                }
                return
            }
        }
    }

    private fun getListItem(path: String): TrackData {
        //val myUri = MediaStore.Audio.Media.getContentUriForPath(fileSrc)
        //val path: String = File(URI(path).getPath()).getCanonicalPath()
        val cursor = context?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.YEAR
            ),
            MediaStore.Audio.Media.DATA + " = ?", arrayOf(
                path
            ),
            ""
        )
        var name = ""
        var performer = ""
        var duration = 0
        while (cursor!!.moveToNext()) {
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            performer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.TRACK))
            name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA))
            duration =
                (cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))).toInt() / 1000
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.YEAR))
        }

        val minutes: Int = duration / 60
        val seconds: Int = duration % 60
        val durationForShow =
            "${if (minutes < 10) "0" else ""}$minutes:${if (seconds < 10) "0" else ""}$seconds"

        //myUri?.encodedUserInfo
        return TrackData(
            name,
            performer,
            durationForShow,
            R.drawable.sample_image,
            path,
            SoundFile.create(path, null)
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_PICK_FILE -> {
                if (data != null) {
                    //get uri from intent returned
                    //val ddata = data.data
                    val uri: Uri? = data.data
                    //get file path of the uri
                    val fileSrc = uri?.let {
                        inputs.add(it)
                        getPath(requireContext(), it)
                    }
                    //update chosen file textView with filesrc
                    Toast.makeText(requireContext(), fileSrc, Toast.LENGTH_LONG).show()
                    //handleFileChosenMediaPlayer(fileSrc)
                    fileSrc?.let { adapter.addListItem(getListItem(fileSrc)) }
                    recyclerView.scrollToPosition(dataList.data.size - 1)
                }
            }
            ADD_AUDIO -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        val extras = data.extras
                        val path =
                            extras?.getString(AudioTrimmerActivity.RECORDED_AUDIO_FILE_PATH_EXTRA)
                        //val soundFile: SoundFile = extras?.getSerializable(AudioTrimmerActivity.RECORDED_SOUND_FILE_EXTRA) as SoundFile
                        Toast.makeText(
                            requireContext(),
                            "Audio stored at $path",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        path?.let { adapter.addListItem(getListItem(path)) }
                        recyclerView.scrollToPosition(dataList.data.size - 1)
                        //adapter.addListItem(getListItem(fileSrc, waveFormData))
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getTrackList(): RecyclerSavedListData {
        val data: MutableList<TrackData> = mutableListOf()
        val dataList = RecyclerSavedListData()
        dataList.data = data
        return dataList
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(requireContext(), mp3, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        val manager = recyclerView.layoutManager as LinearLayoutManager
        val position = manager.findFirstCompletelyVisibleItemPosition()
        onSaveScrollingPositionListener?.onSaveScrollingPosition(position)
        onSaveDataListener?.onSave(dataList)
        super.onDestroyView()
    }

    companion object {

        internal const val REQUEST_CODE_EXTERNAL_STORAGE = 42
        internal const val REQUEST_MICROPHONE = 1
        internal const val REQUEST_CODE_PICK_FILE = 10
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        internal const val ADD_AUDIO = 1001

        @JvmStatic
        fun newInstance(dataList: RecyclerSavedListData?, savedScrollingPosition: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, dataList)
                    putInt(ARG_PARAM2, savedScrollingPosition)
                }
            }
    }
}
