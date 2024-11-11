package com.example.sharedwallet.firebase.objects

data class UserDO(
    val userId: String? = null,
    val email: String? = null,
    val username: String? = null,
    val groups: List<String>? = null,
    val friends: List<String>? = null,
    val createdAt: Any? = null)
