package com.example.sharedwallet.ui.groupdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedwallet.firebase.DatabaseManager
import com.example.sharedwallet.firebase.objects.GroupDO

class GroupDetailViewModel : ViewModel() {

    private val databaseManager = DatabaseManager

    private val _group = MutableLiveData<GroupDO>()
    val group: LiveData<GroupDO> = _group

    fun loadGroup(groupId: String) {
        databaseManager.getGroupById(groupId) { result ->
            _group.value = result
        }
    }
}
