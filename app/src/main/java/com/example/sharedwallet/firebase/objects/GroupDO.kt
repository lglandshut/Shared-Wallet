package com.example.sharedwallet.firebase.objects

data class GroupDO(
    val groupId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val members: ArrayList<String>? = null
)
