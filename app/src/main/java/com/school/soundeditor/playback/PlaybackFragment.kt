package com.school.soundeditor.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.school.soundeditor.R
import com.school.soundeditor.main.MovieData
import com.school.soundeditor.main.SuperRecyclerItemData
import com.school.soundeditor.main.TrackData
import kotlinx.android.synthetic.main.fragment_playback.*

internal class PlaybackFragment : Fragment(), PlaybackScreenView {

    private val presenter: PlaybackScreenPresenter = PlaybackPresenter(this)
    private var param1: SuperRecyclerItemData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
        when (param1) {
            is TrackData -> {
                container.removeAllViewsInLayout()
                val trackDetailLayout = layoutInflater.inflate(R.layout.track_detail_layout, null)
                val data = param1 as TrackData
                trackDetailLayout.findViewById<ImageView>(R.id.track_image)
                    .setImageResource(data.image)
                trackDetailLayout.findViewById<TextView>(R.id.track_name_text_view).text = data.name
                trackDetailLayout.findViewById<TextView>(R.id.track_performer_text_view).text =
                    data.performer
                trackDetailLayout.findViewById<TextView>(R.id.track_duration_text_view).text =
                    data.duration
                trackDetailLayout.findViewById<TextView>(R.id.track_format_text_view).text =
                    data.format
                container.addView(trackDetailLayout)
            }
            is MovieData -> {
                container.removeAllViewsInLayout()
                val movieDetailLayout = layoutInflater.inflate(R.layout.movie_detail_layout, null)
                val data = param1 as MovieData
                movieDetailLayout.findViewById<ImageView>(R.id.movie_image)
                    .setImageResource(data.image)
                movieDetailLayout.findViewById<TextView>(R.id.movie_name_text_view).text = data.name
                movieDetailLayout.findViewById<TextView>(R.id.movie_producer_text_view).text =
                    data.producer
                movieDetailLayout.findViewById<TextView>(R.id.movie_duration_text_view).text =
                    data.duration
                movieDetailLayout.findViewById<TextView>(R.id.movie_format_text_view).text =
                    data.format
                movieDetailLayout.findViewById<TextView>(R.id.starring_text_view).text =
                    data.starring
                container.addView(movieDetailLayout)
            }
            else -> {
            }
        }
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
        fun newInstance(param1: SuperRecyclerItemData?) =
            PlaybackFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}
