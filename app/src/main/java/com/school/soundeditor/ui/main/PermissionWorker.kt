package com.school.soundeditor.ui.main

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

//TODO Вынести ненужные методы из Фрагмента (onActivityResult не вызывается)

internal fun checkPermission(context: FragmentActivity) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED -> {
            chooseFile(context)
        }
        context.shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
            showOnRejectedPermissionDialog(context)
        }
        else -> {
            requestPermissionExternalStorage(context)
        }
    }
}

internal fun chooseFile(context: FragmentActivity) {
    //create intent of Action get content
    var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
    chooseFile.type = "*/*"
    //set available types to audio and video only
    val mimetypes = arrayOf("audio/*", "video/*")
    chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
    //trigger file chooser
    chooseFile = Intent.createChooser(chooseFile, "Choose a file")
    //start activity and wait for the result
    context.startActivityForResult(chooseFile, MainFragment.REQUEST_CODE_PICK_FILE)
}

internal fun showOnRejectedPermissionDialog(context: FragmentActivity) {
    AlertDialog.Builder(context)
        .setTitle("Доступ к файлам на устройстве")
        .setMessage("Для того, чтобы добавить звуковой файл в проект, приложению необходимо разрешение")
        .setPositiveButton("Предоставить доступ") { _, _ ->
            requestPermissionExternalStorage(context)
        }
        .setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}

private fun requestPermissionExternalStorage(context: FragmentActivity) {
    context.requestPermissions(
        arrayOf(READ_EXTERNAL_STORAGE),
        MainFragment.REQUEST_CODE_EXTERNAL_STORAGE
    )
}
