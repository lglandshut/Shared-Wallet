package com.example.sharedwallet.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedwallet.firebase.DatabaseManager
import com.example.sharedwallet.firebase.objects.UserDO

class FriendsViewModel : ViewModel() {

    private val databaseManager = DatabaseManager

    private val _friends = MutableLiveData<List<UserDO>>()
    val friends: LiveData<List<UserDO>> = _friends

    fun loadFriends() {
        databaseManager.getFriends { result ->
            _friends.value = result
        }
    }

    fun searchUser(username: String, callback: (Boolean) -> Unit) {
        if (username.contains("@")) {
            // E-Mail-Adresse
            databaseManager.getUserByEmail(username) { result ->
                if (result != null) {
                    addFriend(result)
                    callback(true)
                } else callback(false)
            }
        } else {
            // Benutzername
            databaseManager.getUserByUsername(username) { result ->
                if (result != null) {
                    addFriend(result)
                    callback(true)
                } else callback(false)
            }
        }
    }

    private fun addFriend(friend: UserDO) {
        val newFriend = _friends.value.orEmpty().toMutableList()
        newFriend.add(friend)
        _friends.value = newFriend

        databaseManager.addFriend(friend.userId.toString())
    }
}