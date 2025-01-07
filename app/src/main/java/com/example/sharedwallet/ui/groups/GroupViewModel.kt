package com.example.sharedwallet.ui.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedwallet.firebase.DatabaseManager
import com.example.sharedwallet.firebase.objects.GroupDO
import java.util.UUID

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
        val groupId = UUID.randomUUID().toString() // Generate unique groupId
        val newGroup = group.copy(groupId = groupId) // Set groupId to the GroupDO object

        val newGroups = _groups.value.orEmpty().toMutableList()
        newGroups.add(newGroup)
        _groups.value = newGroups

        databaseManager.createGroup(newGroup.name.toString(), newGroup.description.toString(), groupId) // Pass groupId to databaseManager
    }
}