package com.example.sharedwallet.ui.groupdetail

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.R

data class UserDebt(
    var userName: String? = null,
    val userDebt: Double? = null
)

class UserDeptSplitAdapter(
    private var userDebtList: MutableList<UserDebt>, // Mutable, um Änderungen zu speichern
    private val userIdToUserNameMap: Map<String, String>
) : RecyclerView.Adapter<UserDeptSplitAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.expense_username)
        val userDebt: EditText = itemView.findViewById(R.id.expense_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_dept_split_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val userDebt = userDebtList[position]
        holder.userName.text = userIdToUserNameMap[userDebt.userName] ?: userDebt.userName
        holder.userDebt.setText(userDebt.userDebt?.toString() ?: "")

        // Überwache Änderungen im EditText
        holder.userDebt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userDebtList[position] = userDebt.copy(
                    userDebt = s?.toString()?.toDoubleOrNull() ?: 0.0
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount() = userDebtList.size

    // Zugriff auf die aktuelle Liste
    fun getUserDebtList(): List<UserDebt> = userDebtList
}


