package com.school.soundeditor.main

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                    adapter.addListItem(getListItem())
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
            "Bohemian Rhapsody",
            "Queen",
            "5:55",
            "Mp3",
            R.drawable.bohemian
        )
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
