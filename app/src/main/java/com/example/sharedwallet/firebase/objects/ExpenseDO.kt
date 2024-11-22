package com.example.sharedwallet.firebase.objects

data class ExpenseDO(
    val expenseId: String? = null,
    val payerId: String? = null,
    val debts: Map<String, Double>? = null,
    val affectedUserIds: List<String>? = null
)