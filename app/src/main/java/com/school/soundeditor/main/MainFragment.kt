package com.school.soundeditor.main

import android.Manifest
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.school.soundeditor.R
import com.school.soundeditor.RecyclerSavedListData
import com.school.soundeditor.ShowItemForPlayback
import kotlinx.android.synthetic.main.fragment_main.*


internal class MainFragment : Fragment(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)
    private var listener: ShowItemForPlayback? = null
    private lateinit var dataList: RecyclerSavedListData
    private var savedScrollingPosition = 0
    private var onSaveDataListener: OnSaveData? = null
    private var onSaveScrollingPositionListener: OnSaveScrollingPosition? = null
    private var fileSrc: String? = null

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
        val adapter = MyAdapter(dataList, object : MyAdapter.OnClickListener {
            override fun onClick(itemData: SuperRecyclerItemData) {
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
            checkPermission()
            context?.let {
                if (ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    chooseFile()
                }
            }
        }
        recyclerView.scrollToPosition(savedScrollingPosition)
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(context, "Файл добавлен в проект", Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    AlertDialog.Builder(it)
                        .setTitle("Доступ к файлам на устройстве")
                        .setMessage("Для того, чтобы добавить звуковой файл в проект, приложению необходимо разрешение")
                        .setPositiveButton("Предоставить доступ") { _, _ ->
                            requestPermission()
                        }
                        .setNegativeButton("Отмена") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE
        )
    }

    private fun getListItem(): SuperRecyclerItemData {
        return TrackData(
            "Test track name",
            "Test performer",
            "Test duration",
            "Test format",
            R.drawable.sample_image,
            fileSrc
        )
    }

    private fun chooseFile() {
        //create intent of Action get content
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        //set available types to audio and video only
        val mimetypes = arrayOf("audio/*", "video/*")
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        //trigger file chooser
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        //start activity and wait for the result
        startActivityForResult(chooseFile, RC_PICK_FILE)
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
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

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
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
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_PICK_FILE) {
            if (data != null) {
                //get uri from intent returned
                val uri: Uri? = data.data
                //get file path of the uri
                fileSrc = uri?.let { getPath(requireContext(), it) }
                //update chosen file textView with filesrc
                Toast.makeText(requireContext(), fileSrc, Toast.LENGTH_LONG).show()
                //handleFileChosenMediaPlayer(fileSrc)
                val adapter = recyclerView.adapter as MyAdapter
                adapter.addListItem(getListItem())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getTrackList(): RecyclerSavedListData {
        val data: MutableList<SuperRecyclerItemData> = mutableListOf(
            HeaderData(),
            TrackData(
                "Bohemian Rhapsody",
                "Queen",
                "5:55",
                "Mp3",
                R.drawable.bohemian
            ),
            MovieData(
                "Modern Times",
                "Charlie Chaplin",
                "87 min",
                "avi",
                R.drawable.moderntimes,
                "Charles Chaplin\nPaulette Goddard"
            )
        )
        data.add(FooterData())
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

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val REQUEST_CODE = 42
        private const val RC_PICK_FILE = 10

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
