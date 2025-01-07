package com.example.sharedwallet.firebase.objects

/*
groups
    groupId: String
    name: String
    description: String
    members: List<String>
        userId: String
    expenses: List<ExpenseDO>
        expenseId: String
        paidBy: String
        paidFor: String
        debtAmount: Double
 */

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
    val debtReason: String? = null
)