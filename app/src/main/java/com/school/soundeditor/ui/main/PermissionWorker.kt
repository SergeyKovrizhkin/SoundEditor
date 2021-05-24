package com.school.soundeditor.ui.main

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.school.soundeditor.ui.audioTrimmerActivity.AudioTrimmerActivity

internal fun checkPermissionReadStorage(fragment: MainFragment) {
    when {
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED -> {
            chooseFile(fragment)
        }
        fragment.shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
            showOnRejectedStorageDialog(fragment)
        }
        else -> {
            requestPermissionExternalStorage(fragment)
        }
    }
}

internal fun checkMicrophone(fragment: MainFragment) {
    if (checkWriteAndRecordPermission(fragment)) {
        fragment.startActivityForResult(
            Intent(fragment.context, AudioTrimmerActivity::class.java),
            MainFragment.ADD_AUDIO
        )
    } else {
        requestWriteAndRecordPermission(fragment)
    }
}

private fun requestWriteAndRecordPermission(fragment: MainFragment) {
    fragment.requestPermissions(
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        ),
        MainFragment.REQUEST_MICROPHONE
    )
}

private fun checkWriteAndRecordPermission(fragment: MainFragment): Boolean {
    return ActivityCompat.checkSelfPermission(
        fragment.requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
}

internal fun chooseFile(fragment: MainFragment) {
    //create intent of Action get content
    var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
    chooseFile.type = "*/*"
    //set available types to audio and video only
    val mimetypes = arrayOf("audio/*", "video/*")
    chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
    //trigger file chooser
    chooseFile = Intent.createChooser(chooseFile, "Choose a file")
    //start activity and wait for the result
    fragment.startActivityForResult(chooseFile, MainFragment.REQUEST_CODE_PICK_FILE)
}

internal fun showOnRejectedStorageDialog(fragment: MainFragment) {
    AlertDialog.Builder(fragment.requireContext())
        .setTitle("Доступ к файлам на устройстве")
        .setMessage("Для того, чтобы добавить звуковой файл в проект, приложению необходимо разрешение")
        .setPositiveButton("Предоставить доступ") { _, _ ->
            requestPermissionExternalStorage(fragment)
        }
        .setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}

internal fun showOnRejectedMicrophoneDialog(fragment: MainFragment) {
    AlertDialog.Builder(fragment.requireContext())
        .setTitle("Доступ к звукозаписи")
        .setMessage("Для того, чтобы записать звук, приложению необходимо разрешение")
        .setPositiveButton("Предоставить доступ") { _, _ ->
            requestPermissionExternalStorage(fragment)
        }
        .setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}

private fun requestPermissionExternalStorage(fragment: MainFragment) {
    fragment.requestPermissions(
        arrayOf(READ_EXTERNAL_STORAGE),
        MainFragment.REQUEST_CODE_EXTERNAL_STORAGE
    )
}
