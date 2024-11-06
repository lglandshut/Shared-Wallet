package com.example.sharedwallet.firebase.objects

data class UserDO(
    val userId: String? = null,
    val email: String? = null,
    val username: String? = null,
    val groups: Array<String>? = null,
    val createdAt: Any? = null)
