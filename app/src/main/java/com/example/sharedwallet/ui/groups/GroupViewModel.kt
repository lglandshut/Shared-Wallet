package com.example.sharedwallet.ui.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedwallet.firebase.DatabaseManager
import com.example.sharedwallet.firebase.objects.GroupDO

class GroupViewModel : ViewModel() {

    private val databaseManager = DatabaseManager

    private val _groups = MutableLiveData<List<GroupDO>>()
    val groups: LiveData<List<GroupDO>> = _groups

    fun loadGroups() {
        databaseManager.getGroups { result ->
            _groups.value = result
        }
    }

    fun addGroup(group: GroupDO) {
        val newGroups = _groups.value.orEmpty().toMutableList()
        newGroups.add(group)
        _groups.value = newGroups

        databaseManager.createGroup(group.name.toString(), group.description.toString())
    }
}