package com.school.soundeditor.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.fragment_record.*


internal class RecordFragment : Fragment(), RecordScreenView {

    private val presenter: RecordScreenPresenter = RecordPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        record_button.setOnClickListener {
            handleRecordAudioButtonClick()
        }
    }

    private fun handleRecordAudioButtonClick() {
        //get fragment manager
        val fm: FragmentManager? = fragmentManager
        //init new RecorderDialogFragment
        val dialogFragment = RecorderDialogFragment()
        //show fragment
        dialogFragment.show(fm!!, "dialogFragment")
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(requireContext(), mp3, Toast.LENGTH_SHORT).show()
    }

    companion object {

        @JvmStatic
        fun newInstance() = RecordFragment()
    }
}
