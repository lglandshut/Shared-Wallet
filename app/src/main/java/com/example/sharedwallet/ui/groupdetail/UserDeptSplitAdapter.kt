package com.example.sharedwallet.ui.groupdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.R
import com.google.android.material.textfield.TextInputEditText

data class UserDebt(
    val userName: String? = null,
    val userDebt: Double? = 0.0
)

class UserDeptSplitAdapter(private var userDebtList: List<UserDebt>) :
    RecyclerView.Adapter<UserDeptSplitAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.expense_username)
        val userDebt: EditText = itemView.findViewById(R.id.expense_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_dept_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userDebt = userDebtList[position]
        holder.userName.text = userDebt.userName
    }

    override fun getItemCount() = userDebtList.size
}

