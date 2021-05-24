package com.school.soundeditor.ui.playback

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.coremedia.iso.boxes.Container
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import com.school.soundeditor.R
import com.school.soundeditor.ui.main.data.BaseData
import com.school.soundeditor.ui.main.data.TrackData
import kotlinx.android.synthetic.main.fragment_playback.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.util.*

internal class PlaybackFragment : Fragment(), PlaybackScreenView {

    //private val presenter: PlaybackScreenPresenter = PlaybackPresenter(this)
    private var trackData: BaseData? = null
    private lateinit var mediaPlayer: MediaPlayer
    private val myHandler: Handler = Handler()
    private var runnableTimeCounter = 0
    private var isAudioFilePlaying = false

    private lateinit var fileSrc: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            trackData = it.getParcelable(TRACK_DATA_EXTRA)
        }
        init()
    }

    private fun init() {
        seekPlayAudio.isClickable = false
        setPlayButtonEnabled(false)
        btnPlayAudio.setOnClickListener { playAudio() }
        tvAudioCurrentPosition.text = getString(R.string.zero_tv_audio_current_position)

        createNewTrackLayout()
        setAudioSeekBar()

        btnTrimAndLoop.setOnClickListener {
            handleTrimAndLoopButtonClick()
        }
        btnAdd.setOnClickListener {
            addToLoop()
        }
        btnSubtract.setOnClickListener {
            subtractFromLoop()
        }

        setSeekBarStartTime()
        setSeekBarEndTime()
        getDuration()
    }

    private fun getDuration() {
        //get duration of audio file
        val fileDuration = getAudioFileDuration(fileSrc)
        //set seek bar limit to 90% of file duration
        configureSeekBars(fileDuration - (fileDuration * 0.1).toInt())
    }

    private fun setSeekBarEndTime() {
        seekBarEndTime.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                //convert i which is in value of milliseconds to seconds with one decimal
                val textToShow = (i / 100 / 10.0).toString() + "s"
                tvSeekEnd.text = textToShow
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setSeekBarStartTime() {
        seekBarStartTime.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                //convert i which is in value of milliseconds to seconds with one decimal
                val textToShow = (i / 100 / 10.0).toString() + "s"
                tvSeekStart.text = textToShow
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setAudioSeekBar() {
        seekPlayAudio.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                showMinutesSeconds(seekPlayAudio.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer.seekTo(seekPlayAudio.progress)
                runnableTimeCounter = seekPlayAudio.progress
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.reset()
    }

    private fun createNewTrackLayout() {
        container.removeAllViewsInLayout()
        val trackDetailLayout = layoutInflater.inflate(R.layout.track_detail_layout, null)
        val data = trackData as TrackData
        trackDetailLayout.findViewById<ImageView>(R.id.track_image)
            .setImageResource(data.image)
        trackDetailLayout.findViewById<TextView>(R.id.track_name_text_view).text = data.name
        trackDetailLayout.findViewById<TextView>(R.id.track_performer_text_view).text =
            data.performer
        trackDetailLayout.findViewById<TextView>(R.id.track_duration_text_view).text =
            data.duration
        handleFileChosenMediaPlayer(data.fileSrc)
        fileSrc = data.fileSrc
        container.addView(trackDetailLayout)
    }

    private fun playAudio() {
        handlePlayClicked()
        isAudioFilePlaying = if (!isAudioFilePlaying) {
            btnPlayAudio.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            true
        } else {
            btnPlayAudio.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp)
            false
        }
    }

    private fun showMinutesSeconds(positionToShow: Int) {
        val value = positionToShow / 1000
        val minutes: Int = value / 60
        val seconds: Int = value % 60
        val textForShow =
            "${if (minutes < 10) "0" else ""}$minutes:${if (seconds < 10) "0" else ""}$seconds"
        tvAudioCurrentPosition.text = textForShow
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
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    private val trackTimeRunnable: Runnable = object : Runnable {
        override fun run() {
            seekPlayAudio?.let {
                //checks if time passed is less than or equal to the track's duration
                if (runnableTimeCounter < mediaPlayer.duration) {
                    //check if the track is running
                    if (isAudioFilePlaying) {
                        seekPlayAudio.progress = mediaPlayer.currentPosition
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
        mediaPlayer = MediaPlayer.create(activity, Uri.fromFile(File(fileSrc)))
        resetMediaPlayer(fileSrc)
        seekPlayAudio.max = mediaPlayer.duration
        setPlayButtonEnabled(true)
        //set seek bar to start
        resetAudioUI()
    }

    private fun resetMediaPlayer(filePath: String) {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(activity, Uri.fromFile(File(filePath)))
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(requireContext(), mp3, Toast.LENGTH_SHORT).show()
    }

    /**
     * Subtracts one from the value of the loop field.
     */
    private fun subtractFromLoop() {
        var tvLoopNumber = Integer.valueOf(tvLoop.text.toString())
        if (tvLoopNumber == 1) return
        tvLoopNumber--
        val tvLoopText = tvLoopNumber.toString() + ""
        tvLoop.text = tvLoopText
    }

    /**
     * Adds one to the value of the loop field.
     */
    private fun addToLoop() {
        var tvLoopNumber = Integer.valueOf(tvLoop.text.toString())
        tvLoopNumber++
        val tvLoopText = tvLoopNumber.toString() + ""
        tvLoop.text = tvLoopText
    }

    /**
     * Handles trim and loop button click.
     */
    private fun handleTrimAndLoopButtonClick() {
        val tvLoopString = tvLoop.text.toString()
        val loopNumber = Integer.valueOf(tvLoopString)
        if (loopNumber == 0) {
            Toast.makeText(
                requireContext(),
                "Please Enter Number Greater than 0",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val fileDuration = getAudioFileDuration(fileSrc)
        val trimFromStartTime = seekBarStartTime.progress.toDouble()
        val trimFromEndTime = seekBarEndTime.progress.toDouble()
        //trim file from start time to end time
        val endTime = fileDuration - trimFromEndTime
        trimFile(fileSrc, trimFromStartTime / 1000.0, endTime / 1000.0)
        //loop file
        loopFile(TRIMMED_FILE, loopNumber)
    }

    /**
     * Trims a file to get a result of the file from startTime to endTime.
     */
    private fun trimFile(inputFilePath: String, startTime: Double, endTime: Double) {
        try {
            // get file from memory
            val dir = File(Environment.getExternalStorageDirectory(), "Music/mp4Test/")
            dir.mkdirs()
            val inputFile = File(inputFilePath)
            // create movie from the file
            val movie = MovieCreator.build(inputFile.path)

            // get tracks from movie
            val tracks = movie.tracks
            movie.tracks = LinkedList()

            //start time which will be updated according to samples
            var startTimeAfterSync = startTime
            //end time which will be updated according to samples
            var endTimeAfterSync = endTime
            var timeCorrected = false

            // Here we try to find a track that has sync samples. Since we can only start decoding
            // at such a sample we SHOULD make sure that the start of the new fragment is exactly
            // such a frame
            for (track in tracks) {
                if (track.syncSamples != null && track.syncSamples.isNotEmpty()) {
                    if (timeCorrected) {
                        // This exception here could be a false positive in case we have multiple tracks
                        // with sync samples at exactly the same positions. E.g. a single movie containing
                        // multiple qualities of the same video (Microsoft Smooth Streaming file)
                        throw RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.")
                    }
                    startTimeAfterSync = correctTimeToSyncSample(track, startTimeAfterSync, false)
                    endTimeAfterSync = correctTimeToSyncSample(track, endTimeAfterSync, true)
                    timeCorrected = true
                }
            }
            for (track in tracks) {
                var currentSample: Long = 0
                var currentTime = 0.0
                var lastTime = -1.0
                var startSample1: Long = -1
                var endSample1: Long = -1
                for (i in track.sampleDurations.indices) {
                    val delta = track.sampleDurations[i]
                    if (currentTime > lastTime && currentTime <= startTimeAfterSync) {
                        // current sample is still before the new starttime
                        startSample1 = currentSample
                    }
                    if (currentTime > lastTime && currentTime <= endTimeAfterSync) {
                        // current sample is after the new start time and still before the new endtime
                        endSample1 = currentSample
                    }
                    lastTime = currentTime
                    currentTime += delta.toDouble() / track.trackMetaData.timescale.toDouble()
                    currentSample++
                }
                movie.addTrack(AppendTrack(CroppedTrack(track, startSample1, endSample1)))
            }
            val start1 = System.currentTimeMillis()
            val out = DefaultMp4Builder().build(movie)
            val start2 = System.currentTimeMillis()
            val output = File("$dir/$TRIMMED_FILE")
            if (output.exists()) {
                output.delete()
            }
            val fos =
                FileOutputStream("$dir/$TRIMMED_FILE") //String.format("output-%f-%f.mp4", startTime1, endTime1)
            val fc: FileChannel = fos.channel
            out.writeContainer(fc)
            fc.close()
            fos.close()
            val start3 = System.currentTimeMillis()
            System.err.println("Building IsoFile took : " + (start2 - start1) + "ms")
            System.err.println("Writing IsoFile took  : " + (start3 - start2) + "ms")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Syncs time with nearest sample, since trim can only be done from the start of a sample
     */
    private fun correctTimeToSyncSample(track: Track, cutHere: Double, next: Boolean): Double {
        val timeOfSyncSamples = DoubleArray(track.syncSamples.size)
        var currentSample: Long = 0
        var currentTime = 0.0
        for (i in track.sampleDurations.indices) {
            val delta = track.sampleDurations[i]
            if (Arrays.binarySearch(track.syncSamples, currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1
                timeOfSyncSamples[Arrays.binarySearch(track.syncSamples, currentSample + 1)] =
                    currentTime
            }
            currentTime += delta.toDouble() / track.trackMetaData.timescale.toDouble()
            currentSample++
        }
        var previous = 0.0
        for (timeOfSyncSample in timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                return if (next) {
                    timeOfSyncSample
                } else {
                    previous
                }
            }
            previous = timeOfSyncSample
        }
        return timeOfSyncSamples[timeOfSyncSamples.size - 1]
    }

    /**
     * Creates a loop of the given audio file according to the given loop number.
     */
    private fun loopFile(inputFilePath: String, loopNumber: Int) {
        verifyStoragePermissions(requireActivity())
        val dir = File(Environment.getExternalStorageDirectory(), "/Music/mp4Test/")
        dir.mkdirs()
        //file which we will work on
        val inputFile = File(dir, inputFilePath)
        //file to be saved
        val outputFile = File(dir, LOOPED_FILE)
        // create movie from input file
        var inputMovie: Movie? = null
        try {
            inputMovie = MovieCreator.build(inputFile.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // create array of input movies
        val inputMovies = arrayOfNulls<Movie>(loopNumber)
        for (i in 0 until loopNumber) {
            inputMovies[i] = inputMovie
        }
        // create output movie
        val outputMovie = Movie()
        // create audio tracks
        val audioTracks: MutableList<Track> = ArrayList()
        for (movie in inputMovies) {
            if (movie != null) {
                for (track in movie.tracks) {
                    if (track.handler.equals("soun")) {
                        audioTracks.add(track)
                    }
                }
            }
        }
        // add audio tracks to output movie
        try {
            outputMovie.addTrack(AppendTrack(*audioTracks.toTypedArray()))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        // save output to file
        var container: Container? = null
        try {
            container = DefaultMp4Builder().build(outputMovie)
        } catch (e: java.lang.Exception) {
        }
        var fc: FileChannel? = null
        try {
            fc = RandomAccessFile(outputFile, "rw").channel
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            container?.writeContainer(fc)
            Toast.makeText(requireContext(), "File was saved", Toast.LENGTH_SHORT).show()
            val textToShow = "File Saved: " + outputFile.path
            tvSavedFile.text = textToShow
            handleLoopedFileSaved(outputFile.path)
        } catch (e: java.lang.Exception) {
            Toast.makeText(requireContext(), "File failed to be saved", Toast.LENGTH_SHORT).show()
        }
        try {
            fc?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Handles the evenet of saving a new audio file
     * @param fileSrc is the path of the saved file
     */
    private fun handleLoopedFileSaved(fileSrc: String) {
        handleFileChosenMediaPlayer(fileSrc)
    }

    /**
     * Configure Seek bars according to passed audio duration
     * @param fileDuration is the duration of the file
     */
    private fun configureSeekBars(fileDuration: Int) {
        seekBarStartTime.max = fileDuration / 2
        seekBarEndTime.max = fileDuration / 2
    }


    /**
     * Returns the duration of the audio file found at the given path.
     * If anything went wrong, 0 will be returned.
     */
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

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    private fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                permissions,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    companion object {

        private const val TRACK_DATA_EXTRA = "param1"
        private const val TRIMMED_FILE = "outputAfterTrim.mp4"
        private const val LOOPED_FILE = "loopedAfterTrim.mp4"
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @JvmStatic
        fun newInstance(trackData: BaseData?) =
            PlaybackFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TRACK_DATA_EXTRA, trackData)
                }
            }
    }
}
