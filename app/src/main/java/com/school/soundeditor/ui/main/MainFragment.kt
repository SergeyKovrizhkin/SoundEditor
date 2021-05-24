package com.school.soundeditor.ui.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.school.soundeditor.R
import com.school.soundeditor.RecyclerSavedListData
import com.school.soundeditor.ShowItemForPlayback
import com.school.soundeditor.ui.audioTrimmerActivity.AudioTrimmerActivity
import com.school.soundeditor.ui.main.data.BaseData
import com.school.soundeditor.ui.main.data.HeaderData
import com.school.soundeditor.ui.main.data.TrackData
import com.school.soundeditor.ui.main.listeners.OnSaveData
import com.school.soundeditor.ui.main.listeners.OnSaveScrollingPosition
import kotlinx.android.synthetic.main.fragment_main.*


internal class MainFragment : Fragment(), MainScreenView {

    //private val presenter: MainScreenPresenter = MainPresenter(this)
    private var listener: ShowItemForPlayback? = null
    private lateinit var dataList: RecyclerSavedListData
    private var savedScrollingPosition = 0
    private var onSaveDataListener: OnSaveData? = null
    private var onSaveScrollingPositionListener: OnSaveScrollingPosition? = null
    private lateinit var adapter: MyAdapter

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
            override fun onClick(itemData: BaseData) {
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
        add_button.setOnClickListener {
            //checkPermission()
            checkPermissionReadStorage(this)
        }
        recyclerView.scrollToPosition(savedScrollingPosition)
        record_button.setOnClickListener {
            checkMicrophone(this)
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

    private fun getAudioFileDuration(filePath: String): Int {
        var duration = 0
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(filePath)
            val strDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            mmr.release()
            if (strDuration != null) {
                duration = strDuration.toInt()
            }
        } catch (e: Exception) {
            // nothing to be done
        }
        return duration
    }

    private fun getListItem(path: String): BaseData {
        //val myUri = MediaStore.Audio.Media.getContentUriForPath(fileSrc)
        //val path: String = File(URI(path).getPath()).getCanonicalPath()
        val c = context!!.contentResolver.query(
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
        while (c!!.moveToNext()) {
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            performer = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.TRACK))
            name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE))
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
            //c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA))
            duration = (c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION))).toInt()/1000
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
            path
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
                    val fileSrc = uri?.let { getPath(requireContext(), it) }
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
                        val path = data.extras?.getString("INTENT_AUDIO_FILE")
                        val waveFormData = data.extras?.getString("INTENT_WAVE_FORM")
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
        val data: MutableList<BaseData> = mutableListOf(
            HeaderData()
        )
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


    internal fun checkPermissionReadStorage(fragment: MainFragment) {
        when {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                chooseFile(fragment)
            }
            fragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showOnRejectedStorageDialog(fragment)
            }
            else -> {
                requestPermissionExternalStorage(fragment)
            }
        }
    }

    internal fun checkMicrophone(fragment: MainFragment) {
        if (checkWriteAndRecordPermission(fragment)) {
            fragment.startActivityForResult(
                Intent(fragment.context, AudioTrimmerActivity::class.java),
                MainFragment.ADD_AUDIO
            )
        } else {
            requestWriteAndRecordPermission(fragment)
        }
    }

    private fun requestWriteAndRecordPermission(fragment: MainFragment) {
        fragment.requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            ),
            MainFragment.REQUEST_MICROPHONE
        )
    }

    private fun checkWriteAndRecordPermission(fragment: MainFragment): Boolean {
        return ActivityCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
    }

    internal fun chooseFile(fragment: MainFragment) {
        //create intent of Action get content
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        //set available types to audio and video only
        val mimetypes = arrayOf("audio/*", "video/*")
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        //trigger file chooser
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        //start activity and wait for the result
        fragment.startActivityForResult(chooseFile, MainFragment.REQUEST_CODE_PICK_FILE)
    }

    internal fun showOnRejectedStorageDialog(fragment: MainFragment) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Доступ к файлам на устройстве")
            .setMessage("Для того, чтобы добавить звуковой файл в проект, приложению необходимо разрешение")
            .setPositiveButton("Предоставить доступ") { _, _ ->
                requestPermissionExternalStorage(fragment)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    internal fun showOnRejectedMicrophoneDialog(fragment: MainFragment) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Доступ к звукозаписи")
            .setMessage("Для того, чтобы записать звук, приложению необходимо разрешение")
            .setPositiveButton("Предоставить доступ") { _, _ ->
                requestPermissionExternalStorage(fragment)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun requestPermissionExternalStorage(fragment: MainFragment) {
        fragment.requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            MainFragment.REQUEST_CODE_EXTERNAL_STORAGE
        )
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
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
