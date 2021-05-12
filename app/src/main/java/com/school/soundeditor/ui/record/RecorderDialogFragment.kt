package com.school.soundeditor.ui.record

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.fragment_record_dialog.*
import java.io.File
import java.util.*


class RecorderDialogFragment : DialogFragment() {

    val handler = Handler()

    // callback
    private var fragmentListener: OnRecordingSavedListener? = null

    // recorder state
    enum class RecordButtonState {
        START_PRESSED, STOP_PRESSED
    }

    // timer
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    var secondsCounter = 0

    // recorder
    private var mediaRecorder: MediaRecorder? = null
    private var recordButtonState = RecordButtonState.STOP_PRESSED
    private var audioFile: File? = null
    private var filePath: String? = null

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        checkNeededPermission()
        defineAudioOutput()
        setSaveAndCancelButtonActivation(false)
        updateRecordingButtonText()
    }

    override fun onAttach(context: Context) {
        fragmentListener = if (context is OnRecordingSavedListener) {
            context
        } else {
            throw IllegalStateException("Parent must implement OnRecordingSavedListener")
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        fragmentListener = null
        //reset the media recording when dialog fragment is detached
        mediaRecorder?.let {
            mediaRecorder!!.reset()
            mediaRecorder = null
        }
    }

    private fun checkNeededPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED -> {
                }
                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                    showOnRejectedPermissionDialogMicro(it)
                }
                else -> {
                    requestPermissionMicro()
                }
            }
        }
    }

    private fun showOnRejectedPermissionDialogMicro(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Доступ к микрофону")
            .setMessage("Для того, чтобы записать звук, приложению необходимо разрешение")
            .setPositiveButton("Предоставить доступ") { _, _ ->
                requestPermissionMicro()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun requestPermissionMicro() {
        activity?.requestPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_CODE_MICROPHONE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_MICROPHONE -> {
                // Проверяем, дано ли пользователем разрешение по нашему запросу
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] != PackageManager.PERMISSION_GRANTED)
                ) {
                    // Поясните пользователю, что экран останется пустым, потому что доступ к контактам не предоставлен
                    showOnRejectedPermissionDialogMicro(requireActivity())
                }
                return
            }
        }
    }

    /**
     * Initializes the fragment's views.
     */
    private fun init() {
        btnRecord.setOnClickListener {
            handleBtnRecordClicked()
        }
        btnCancelRecorded.setOnClickListener {
            cancelFileSaving()
        }
        btnSaveRecorded.setOnClickListener {
            saveAudioFile()
        }
    }

    private fun defineAudioOutput() {
        //create file in external memory, in our folder with name plus timestamp
        filePath =
            Environment.getExternalStorageDirectory().path + "/soundEditorTest/recording" + System.currentTimeMillis() + ".mp4"
        audioFile = File(filePath!!)
    }

    /**
     * Set Save and cancel buttons activation
     */
    private fun setSaveAndCancelButtonActivation(state: Boolean) {
        setSaveAndCancelButtonsClickability(state)
        setSaveAndCancelButtonOpacity(state)
    }

    // TIMER //
    private fun startTimer() {
        //set a new Timer
        timer = Timer()
        //initialize the TimerTask's job
        initializeTimerTask()
        //schedule the timer, after the first 0ms the TimerTask will run every 1000ms
        timer!!.schedule(timerTask, 1000, 1000)
    }

    private fun stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    private fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post {
                    secondsCounter++
                    updateRecordingButtonText()
                }
            }
        }
    }

    /**
     * Updates the timing text of the record button.
     * Initially it is set to 0.
     */
    private fun updateRecordingButtonText() {
        // set the timer string format,(0:00)
        val time = String.format(
            "%01d:%02d",
            secondsCounter % 3600 / 60, secondsCounter % 60
        )
        // set the time string text to record button
        btnRecordText.text = time
    }

    /**
     * Prepares the recorder, adding the wanted configurations to it.
     * @return true if prepare was successful, false otherwise
     */
    private fun resetRecorder(): Boolean {
        try {
            //set audio source to microphone
            mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            //set output format to mp4
            mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            //set encoding to aac
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            //set output file
            mediaRecorder!!.setOutputFile(audioFile?.absolutePath)
            //try to prepare the media recorder
            mediaRecorder!!.prepare()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * Handles the click of record button
     */
    private fun handleBtnRecordClicked() {
        //checks if record button is in the stop state
        if (recordButtonState === RecordButtonState.STOP_PRESSED) {
            handleStopStateRecordButtonPressed()
        } else {
            handleStartStateRecordButtonPressed()
        }
    }

    /**
     * Handles the record button click when its in start state.
     */
    private fun handleStartStateRecordButtonPressed() {
        //stop the timer
        stopTimerTask()
        //set save and cancel buttons to clickable
        setSaveAndCancelButtonActivation(true)
        //change the record button state to stop
        recordButtonState = RecordButtonState.STOP_PRESSED
        btnRecord.setImageResource(R.drawable.circular_record_button_green)
    }

    /**
     * Handles the record button click when its in stop state.
     */
    private fun handleStopStateRecordButtonPressed() {
        //if media recorder not null reset it
        if (mediaRecorder != null) {
            mediaRecorder!!.reset()
        }
        //set seconds counter to 0 and initialize new media recorder
        secondsCounter = 0
        mediaRecorder = MediaRecorder()
        //checks if reset recorder was successful
        if (resetRecorder()) {
            try {
                btnRecord.setImageResource(R.drawable.circular_record_button_red)
                //start media recorder and timer
                mediaRecorder!!.start()
                startTimer()
                //change the record button state to start
                recordButtonState = RecordButtonState.START_PRESSED
                //set save and cancel buttons to unclickable
                setSaveAndCancelButtonActivation(false)
            } catch (e: java.lang.IllegalStateException) {
                //if an exception occurred, dismiss the dialog fragment
                dismiss()
            }
        }
    }

    /**
     * Updates the clickability of the save and cancel buttons.
     */
    private fun setSaveAndCancelButtonsClickability(state: Boolean) {
        btnCancelRecorded.isEnabled = state
        btnSaveRecorded.isEnabled = state
    }

    /**
     * Sets the opacity of Save and Cancel buttons according to state
     */
    private fun setSaveAndCancelButtonOpacity(state: Boolean) {
        if (state) {
            btnCancelRecorded.background.alpha = 255
            btnSaveRecorded.background.alpha = 255
        } else {
            btnCancelRecorded.background.alpha = 16
            btnSaveRecorded.background.alpha = 16
        }
    }

    /**
     * Cancels the recording done.
     */
    private fun cancelFileSaving() {
        if (mediaRecorder != null) {
            //resets the media recorder
            mediaRecorder!!.reset()
            //set the seconds counter to 0 and update the text of the recorder buuton
            secondsCounter = 0
            updateRecordingButtonText()
            //set media recorder to null
            mediaRecorder = null
            btnRecord.setBackgroundResource(R.drawable.circular_record_button_green)
            //set save and cancel buttons to unclickable
            setSaveAndCancelButtonActivation(false)
        }
    }

    /**
     * Saves the audio file recorded
     */
    private fun saveAudioFile() {
        //set seconds counter to 0 and update text of recording button
        secondsCounter = 0
        updateRecordingButtonText()
        btnRecord.setBackgroundResource(R.drawable.circular_record_button_green)
        try {
            //stop, reset and release media recorder
            mediaRecorder!!.stop()
            mediaRecorder!!.reset()
            mediaRecorder!!.release()
            mediaRecorder = null
            //set save and cancel buttons to unclickable
            setSaveAndCancelButtonActivation(false)
            // inform parent
            fragmentListener?.onSaved(filePath)
        } catch (e: java.lang.Exception) {
            fragmentListener?.onFailed()
        }
        dismiss()
    }

    // CALLBACKS //
    /**
     * Interface definition for callbacks to be invoked
     * when events occur in the [RecorderDialogFragment].
     */
    interface OnRecordingSavedListener {
        /**
         * Called when audio file saving successfully done.
         * @param filePath The path of the saved file.
         */
        fun onSaved(filePath: String?)

        /**
         * Called when an error occurs during file saving.
         */
        fun onFailed()
    }

    companion object {

        internal const val REQUEST_CODE_MICROPHONE = 1
    }
}