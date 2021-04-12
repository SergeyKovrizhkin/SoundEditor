package com.school.soundeditor

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ExitAppDialogFragment : DialogFragment() {

    private var listener: OnExit? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnExit
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Хотите выйти?")
            .setNeutralButton("Отмена") { dialog, button ->
            }
            .setPositiveButton("Выйти") { dialog, button ->
                listener?.onExit()
            }
            .create()
    }
}