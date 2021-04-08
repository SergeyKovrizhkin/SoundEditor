package com.school.soundeditor.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.school.soundeditor.R

internal class MainFragment : Fragment(), MainScreenView {

    private val presenter: MainScreenPresenter = MainPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
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