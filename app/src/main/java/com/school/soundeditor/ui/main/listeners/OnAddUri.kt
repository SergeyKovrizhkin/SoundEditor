package com.school.soundeditor.ui.main.listeners

import android.content.ContentValues
import android.net.Uri

interface OnAddUri {
    fun onAdd (uri: Uri, values: ContentValues): Uri?
}