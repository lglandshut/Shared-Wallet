package com.example.sharedwallet.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.R
import com.example.sharedwallet.firebase.objects.UserDO

class FriendsAdapter(private val friends: List<UserDO>) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendUsername: TextView = itemView.findViewById(R.id.group_detail_friend_debt)
        val friendEmail: TextView = itemView.findViewById(R.id.group_detail_friend_username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_row_item, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = friends[position]
        holder.friendUsername.text = friend.username
        holder.friendEmail.text = friend.email
    }

    override fun getItemCount() = friends.size
}
