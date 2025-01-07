package com.example.sharedwallet.ui.groupdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.R
import com.example.sharedwallet.firebase.objects.ExpenseDO

class GroupDetailExpenseAdapter(private var expenseList: List<ExpenseDO>) :
    RecyclerView.Adapter<GroupDetailExpenseAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paidBy: TextView = itemView.findViewById(R.id.paidBy)
        val paidFor: TextView = itemView.findViewById(R.id.paidFor)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val reason: TextView = itemView.findViewById(R.id.reason)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.paidBy.text = expense.paidBy
        holder.paidFor.text = expense.paidFor
        holder.amount.text = "${String.format("%.2f", expense.debtAmount)} €"
        holder.reason.text = expense.debtReason
    }

    override fun getItemCount() = expenseList.size

    // Aktualisiere Daten und informiere RecyclerView
    fun updateData(newList: List<ExpenseDO>, userIdToUserNameMap: Map<String, String>) {
        newList.forEach { expense ->
            expense.paidBy = userIdToUserNameMap[expense.paidBy] ?: expense.paidBy
            expense.paidFor = userIdToUserNameMap[expense.paidFor] ?: expense.paidFor
        }
        expenseList = newList
        notifyDataSetChanged()
    }
}

