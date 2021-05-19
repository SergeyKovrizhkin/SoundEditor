package com.school.soundeditor.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.school.soundeditor.ui.main.data.BaseData
import com.school.soundeditor.ui.main.data.FooterData
import com.school.soundeditor.ui.main.data.HeaderData
import com.school.soundeditor.ui.main.data.TrackData
import com.school.soundeditor.ui.main.listeners.OnSaveData
import com.school.soundeditor.ui.main.listeners.OnSaveScrollingPosition
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
        initView()
    }

    private fun initView() {
        val adapter = MyAdapter(dataList, object : MyAdapter.OnClickListener {
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
            checkPermission(this)

        }
        recyclerView.scrollToPosition(savedScrollingPosition)
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
                    showOnRejectedPermissionDialog(requireActivity())
                }
                return
            }
            REQUEST_CODE_CONTACTS -> {
                // Открываем контакты getContacts()
                return
            }
        }
    }

    private fun getListItem(): BaseData {
        return TrackData(
            "Test track name",
            "Test performer",
            "Test duration",
            "Test format",
            R.drawable.sample_image,
            fileSrc
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PICK_FILE) {
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
                recyclerView.scrollToPosition(dataList.data.size - 1)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    chooseFile()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showOnRejectedPermissionDialog(it)
                }
                else -> {
                    requestPermissionExternalStorage()
                }
            }
        }
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
        startActivityForResult(chooseFile, REQUEST_CODE_PICK_FILE)
    }

    private fun showOnRejectedPermissionDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Доступ к файлам на устройстве")
            .setMessage("Для того, чтобы добавить звуковой файл в проект, приложению необходимо разрешение")
            .setPositiveButton("Предоставить доступ") { _, _ ->
                requestPermissionExternalStorage()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun requestPermissionExternalStorage() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_EXTERNAL_STORAGE
        )
    }

    private fun getTrackList(): RecyclerSavedListData {
        val data: MutableList<BaseData> = mutableListOf(
            HeaderData(),
            TrackData(
                "Bohemian Rhapsody",
                "Queen",
                "5:55",
                "Mp3",
                R.drawable.bohemian
            ),
            TrackData(
                "Empty name",
                "Empty performer",
                "Empty duration",
                "Empty format",
                R.drawable.sample_image
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

        internal const val REQUEST_CODE_EXTERNAL_STORAGE = 42
        internal const val REQUEST_CODE_PICK_FILE = 10
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val REQUEST_CODE_CONTACTS = 101

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
