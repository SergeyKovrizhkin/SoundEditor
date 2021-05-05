package com.school.soundeditor.playback

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import java.io.File


internal class PlaybackFragment : Fragment(), PlaybackScreenView {

    private val presenter: PlaybackScreenPresenter = PlaybackPresenter(this)
    private var param1: SuperRecyclerItemData? = null
    private var mediaPlayer: MediaPlayer? = null
    private val myHandler: Handler = Handler()
    private var runnableTimeCounter = 0
    private var isAudioFilePlaying = false

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
        init()
    }

    private fun init() {
        seekPlayAudio.isClickable = false
        setPlayButtonEnabled(false)
        btnPlayAudio.setOnClickListener {
            handlePlayClicked()
            isAudioFilePlaying = if (!isAudioFilePlaying) {
                btnPlayAudio.setBackgroundResource(R.drawable.ic_pause_black_24dp)
                true
            } else {
                btnPlayAudio.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp)
                false
            }
        }
        tvAudioCurrentPosition.text = getString(R.string.zero_tv_audio_current_position)
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
                data.fileSrc?.let { handleFileChosenMediaPlayer(it) }
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

    private fun setPlayButtonEnabled(enabled: Boolean) {
        if (enabled) btnPlayAudio.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp) else btnPlayAudio.setBackgroundResource(
            R.drawable.ic_play_arrow_faded_24dp
        )
        btnPlayAudio.isEnabled = enabled
    }

    private fun handlePlayClicked() {
        //run updateTrackTime after 100ms
        myHandler.postDelayed(trackTimeRunnable, 100)
        //if already playing, pause
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        } else {
            mediaPlayer!!.start()
        }
    }

    private val trackTimeRunnable: Runnable = object : Runnable {
        override fun run() {
            //checks if time passed is less than or equal to the track's duration
            if (runnableTimeCounter <= mediaPlayer!!.duration) {
                //check if the track is running
                if (isAudioFilePlaying) {
                    //get current position of the track
                    val mediaPlayerPosition = mediaPlayer!!.currentPosition
                    //change the ms to seconds with one decimal
                    val value = mediaPlayerPosition / 100
                    //set value to the text view of the seek bar
                    tvAudioCurrentPosition.text = (value / (10.0)).toString() + "s"
                    //update progress of seek bar
                    seekPlayAudio.progress = mediaPlayerPosition
                    //re run after 100ms delay
                    myHandler.postDelayed(this, 100)
                    //increment timer by 100ms
                    runnableTimeCounter += 100
                }
            } else {
                //else the track reached it's end
                handleTrackEnd()
            }
        }
    }

    private fun handleTrackEnd() {
        resetAudioUI()
    }

    private fun resetAudioUI() {
        //set seek bar to start
        seekPlayAudio.progress = 0
        //set text of text view of the seek bar to 0.0s
        tvAudioCurrentPosition.text = getString(R.string.zero_tv_audio_current_position)
        //change value of is playing to false
        isAudioFilePlaying = false
        //change icon from pause to play
        btnPlayAudio.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp)
        runnableTimeCounter = 0
    }

    private fun handleFileChosenMediaPlayer(fileSrc: String) {
        resetMediaPlayer(fileSrc)
        mediaPlayer?.let { seekPlayAudio.max = it.duration }
        setPlayButtonEnabled(true)
        //set seek bar to start
        resetAudioUI()
    }

    private fun resetMediaPlayer(filePath: String) {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(activity, Uri.fromFile(File(filePath)))
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
