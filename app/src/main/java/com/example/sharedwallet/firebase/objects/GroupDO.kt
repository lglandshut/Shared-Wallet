package com.example.sharedwallet.firebase.objects

import com.google.firebase.Timestamp

data class GroupDO(
    val groupId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val members: List<String>? = null,
    val expenses: List<ExpenseDO>? = null
)

data class ExpenseDO(
    var expenseId: String? = null,
    var paidBy: String? = null,
    var paidFor: String? = null,
    val debtAmount: Double? = null,
    val debtReason: String? = null,
    var date: Timestamp? = null
)