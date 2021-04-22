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

        val movieLayout = layoutInflater.inflate(R.layout.movie_item_layout, null)

        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
        when (param1) {
            is TrackData -> {
                val data = param1 as TrackData
                movieLayout.findViewById<ImageView>(R.id.movie_image).setImageResource(data.image)
                movieLayout.findViewById<TextView>(R.id.movie_name_text_view).text = data.name
                container.addView(movieLayout)

                /*item_image.setImageResource(data.image)
                ("""${(data).name}${(data).performer}${(data).duration}${(data).format}""").also {
                    item_data_text_view.text = it
                }*/
            }
            is MovieData -> {


                /*item_image.setImageResource((param1 as MovieData).image)
                """${(param1 as MovieData).name}
${(param1 as MovieData).producer}
${(param1 as MovieData).duration}
${(param1 as MovieData).format}
Starring:
${(param1 as MovieData).starring}""".also { item_data_text_view.text = it }*/
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
