package com.example.sharedwallet.ui.groupdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.R

data class UserDebt(
    val userId: String,
    val username: String,
    val debtAmount: Double
)

class GroupDetailUserAdapter(private var userDebtList: List<UserDebt>) :
    RecyclerView.Adapter<GroupDetailUserAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.group_detail_friend_username)
        val userDebt: TextView = itemView.findViewById(R.id.group_detail_friend_debt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_dept_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userDebt = userDebtList[position]
        holder.userName.text = userDebt.username
        holder.userDebt.text = "${String.format("%.2f", userDebt.debtAmount)} €"
    }

    override fun getItemCount() = userDebtList.size

    // Aktualisiere Daten und informiere RecyclerView
    fun updateData(newList: List<UserDebt>) {
        userDebtList = newList
        notifyDataSetChanged()
    }
}

