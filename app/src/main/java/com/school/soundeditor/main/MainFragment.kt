package com.school.soundeditor.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.fragment_main.*

internal class MainFragment : Fragment(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = MyAdapter(getTrackList(), object : MyAdapter.OnClickListener {
            override fun onClick(itemData: TrackData) {
                //Открыть в новом окне
                Toast.makeText(requireContext(), itemData.name, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTrackList(): MutableList<TrackData> {
        val dataList: MutableList<TrackData> = mutableListOf(
            TrackData(),
            TrackData(
                "Bohemian Rhapsody",
                "Queen",
                "5:55",
                "Mp3",
                R.drawable.bohemian
            )
        )
        for (i in 1..9) {
            dataList.add(
                TrackData(
                    "Test name №$i",
                    "Test performer №$i",
                    "$i:0$i",
                    if (i % 2 == 0) "Mp3" else "Wav",
                    R.drawable.sample_image
                )
            )
        }
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
