package com.school.soundeditor.ui.main.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.school.soundeditor.R
import kotlinx.android.synthetic.main.help_dialog_fragment.*


class HelpDialogFragment : DialogFragment() {

    private var pagesCounter: Int = 0
    private var pages = listOf(
        R.layout.help_page0,
        R.layout.help_page1,
        R.layout.help_page2,
        R.layout.help_page3,
        R.layout.help_page4,
        R.layout.help_page5,
        R.layout.help_page6,
        R.layout.help_page7,
        R.layout.help_page8,
        R.layout.help_page9
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.help_dialog_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showNewPage(pagesCounter)
        dismissButton.setOnClickListener { dismiss() }
        previousPageButton.setOnClickListener {
            if (pagesCounter == 0) {
                dismiss()
            } else {
                pagesCounter--
                showNewPage(pagesCounter)
            }
        }
        nextPageButton.setOnClickListener {
            if (pagesCounter == pages.size - 1) {
                dismiss()
            } else {
                pagesCounter++
                showNewPage(pagesCounter)
            }
        }
    }

    private fun showNewPage(pagesCounter: Int) {
        container_for_pages.removeAllViewsInLayout()
        val newPage = layoutInflater.inflate(pages[pagesCounter], null)
        container_for_pages.addView(newPage)
    }
}