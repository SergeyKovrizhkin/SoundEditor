package com.school.soundeditor.equalizer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.school.soundeditor.Data
import com.school.soundeditor.OnEqualizerSave
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.fragment_equalizer.*

internal class EqualizerFragment : Fragment(), EqualizerScreenView {

    private val presenter: EqualizerScreenPresenter = EqualizerPresenter(this)
    private var param1: String? = null
    private var listener: OnEqualizerSave? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnEqualizerSave
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

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
        }
        switch1.text = param1

        onSaveButton.setOnClickListener {
            listener?.onSave("Hello from Equalizer")
        }
    }

    override fun getTrack(mp3: String) {
        TODO("Not yet implemented")
    }

    override fun showTrack(mp3: String) {
        Toast.makeText(requireContext(), mp3, Toast.LENGTH_SHORT).show()
    }

    fun setListener(listener: OnEqualizerSave) {
        this.listener = listener
    }

    fun showData(data: Data) {
        Toast.makeText(requireContext(), data.string, Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(param1: String) =
            EqualizerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}
