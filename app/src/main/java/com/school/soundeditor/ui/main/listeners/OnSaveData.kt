package com.school.soundeditor.ui.main.listeners

import com.school.soundeditor.InputsSavedData
import com.school.soundeditor.RecyclerSavedListData

internal interface OnSaveData {
    fun onSave(dataList: RecyclerSavedListData, inputs: InputsSavedData)
}
