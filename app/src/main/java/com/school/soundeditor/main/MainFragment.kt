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
import com.school.soundeditor.ShowItemForPlayback
import kotlinx.android.synthetic.main.fragment_main.*

internal class MainFragment : Fragment(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)
    private var listener: ShowItemForPlayback? = null

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

        val adapter = MyAdapter(getTrackList(), object : MyAdapter.OnClickListener {
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

    private fun getTrackList(): MutableList<SuperRecyclerItemData> {
        val dataList: MutableList<SuperRecyclerItemData> = mutableListOf(
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
        /*for (i in 1..9) {
            dataList.add(
                if (i % 3 != 0) {
                    TrackData(
                        "Test name №$i",
                        "Test performer №$i",
                        "$i:0$i",
                        if (i % 2 == 0) "Mp3" else "Wav",
                        R.drawable.sample_image
                    )
                } else {
                    MovieData(
                        "Test name №$i",
                        "Test producer №$i",
                        "${i * 20} min",
                        if (i % 2 == 0) "avi" else "mp4",
                        R.drawable.defaultmovie,
                        "Test actor №$i\nTest actress №$i"
                    )
                }
            )
        }*/
        dataList.add(FooterData())
        return dataList
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(requireContext(), mp3, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
