package com.example.sharedwallet.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedwallet.firebase.DatabaseManager
import com.example.sharedwallet.firebase.objects.ExpenseDO

class ActivityViewModel : ViewModel() {

    private val databaseManager = DatabaseManager

    private val _expenses = MutableLiveData<List<ExpenseDO>>()
    val expenses: LiveData<List<ExpenseDO>> = _expenses

    var userIdToUserNameMap: Map<String, String> = emptyMap()

    fun loadData() {
        databaseManager.getGroups() { result ->
            val expenseList = result.flatMap { it.expenses ?: emptyList() }
            //Map userIds to usernames
            if(expenseList.isNotEmpty()) {
                databaseManager.getAllUsers() { users ->
                    userIdToUserNameMap = users
                    //Add expenses to recyclerview
                    _expenses.value = expenseList.map { it.copy() }
                }
            }
        }
    }
}