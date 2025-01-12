package com.example.sharedwallet.ui.groupdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.R
import com.example.sharedwallet.firebase.objects.UserDebt

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
        holder.userName.text = userDebt.userName
        holder.userDebt.text = "${String.format("%.2f", userDebt.userDebt)} €"

        //Set color based of debt
        val textColor = if ((userDebt.userDebt ?: 0.0) < 0) {
            android.graphics.Color.RED
        } else {
            android.graphics.Color.GREEN
        }
        holder.userDebt.setTextColor(textColor)
    }

    override fun getItemCount() = userDebtList.size

    //Refresh data and tell RecyclerView
    fun updateData(newList: List<UserDebt>, userIdToUserNameMap: Map<String, String>) {
        newList.forEach { expense ->
            expense.userName = userIdToUserNameMap[expense.userName] ?: expense.userName
        }
        userDebtList = newList
        notifyDataSetChanged()
    }
}

