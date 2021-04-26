package com.school.soundeditor.main

import com.school.soundeditor.RecyclerSavedListData

internal interface OnSaveData {
    fun onSave(dataList: RecyclerSavedListData)
}
