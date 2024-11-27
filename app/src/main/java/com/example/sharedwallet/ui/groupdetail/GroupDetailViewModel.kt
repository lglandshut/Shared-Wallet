package com.example.sharedwallet.ui.groupdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedwallet.firebase.DatabaseManager
import com.example.sharedwallet.firebase.objects.GroupDO
import com.example.sharedwallet.firebase.objects.UserDO

class GroupDetailViewModel : ViewModel() {

    private val databaseManager = DatabaseManager

    private val _group = MutableLiveData<GroupDO>()
    val group: LiveData<GroupDO> = _group

    private val _userDebts = MutableLiveData<List<UserDebt>>()
    val userDebts: LiveData<List<UserDebt>> = _userDebts

    private val _friendsToAdd = MutableLiveData<List<UserDO>>()
    val friendsToAdd: LiveData<List<UserDO>> = _friendsToAdd

    fun loadGroup(groupId: String) {
        databaseManager.getGroupById(groupId) { result ->
            _group.value = result
        }
    }

    private fun loadUserDebts(groupId: String) {
        databaseManager.getUserDebtsByGroupId(groupId) { debts ->
            _userDebts.value = debts
        }
    }

    fun loadFriendsList() {
        databaseManager.getFriends { friends ->
            val currentMembers = group.value?.members ?: emptyList()
            val filteredFriends = friends.filterNot { friend ->
                currentMembers.contains(friend.userId)
            }
            _friendsToAdd.value = filteredFriends
        }
    }

    fun addFriendsToGroup(selectedUserIds: List<String?>) {
        val groupId = group.value?.groupId
        if (groupId != null) {
            databaseManager.addUsersToGroup(groupId, selectedUserIds) {
                // Erfolgreich hinzugefügt, Mitglieder und Freunde aktualisieren
                group.value?.let { currentGroup ->
                    val updatedMembers = currentGroup.members?.plus(selectedUserIds.filterNotNull())
                    _group.value = currentGroup.copy(members = updatedMembers)
                }
            }
        }
    }

    fun leaveGroup() {
        val groupId = group.value?.groupId
        if (groupId != null) {
            databaseManager.removeUserFromGroup(groupId)
        }
        TODO("Check if group is empty and delete if so" +
                "Check if expenses are settled")
    }

}
