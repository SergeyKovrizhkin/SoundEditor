package com.school.soundeditor.equalizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.fragment_equalizer.*

class EqualizerFragment : Fragment(), EqualizerScreenView {

    private val presenter: EqualizerScreenPresenter = EqualizerPresenter(this)

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_equalizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        switch1.text = param1
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        //Toast.makeText(this, mp3, Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EqualizerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}