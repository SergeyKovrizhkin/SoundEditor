package com.school.soundeditor.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.school.soundeditor.MainActivity
import com.school.soundeditor.R
import com.school.soundeditor.RecyclerSavedListData
import com.school.soundeditor.ShowItemForPlayback
import kotlinx.android.synthetic.main.fragment_main.*

internal class MainFragment : Fragment(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)
    private var listener: ShowItemForPlayback? = null
    private var dataList: RecyclerSavedListData? = null


    fun setListener(listener: ShowItemForPlayback) {
        this.listener = listener
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
            dataList = it.getParcelable(ARG_PARAM1)
        }
        val adapter = MyAdapter(dataList ?: getTrackList(), object : MyAdapter.OnClickListener {
            override fun onClick(itemData: SuperRecyclerItemData) {
                MainActivity.itemSelected = itemData
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
            adapter.addListItem(getListItem())
        }
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

    companion object {

        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(dataList: RecyclerSavedListData?) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, dataList)
                }
            }
    }
}
