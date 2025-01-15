package com.example.sharedwallet.ui.groupdetail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.R
import com.example.sharedwallet.firebase.objects.ExpenseDO
import java.text.SimpleDateFormat
import java.util.Locale

class GroupDetailExpenseAdapter(private var expenseList: List<ExpenseDO>, private var currentUser: String) :
    RecyclerView.Adapter<GroupDetailExpenseAdapter.ViewHolder>() {

        private var userIdToUserNameMap: Map<String, String> = emptyMap()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paidBy: TextView = itemView.findViewById(R.id.paidBy)
        val paidFor: TextView = itemView.findViewById(R.id.paidFor)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val icon: ImageView = itemView.findViewById(R.id.expense_row_icon)
            val timestamp: TextView = itemView.findViewById(R.id.expense_timestamp_and_reason)
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
        if (expense.paidBy != userIdToUserNameMap[currentUser]) {
            holder.amount.setTextColor(Color.RED)
            holder.icon.setImageResource(R.drawable.baseline_credit_card_off_24)
        }
        holder.timestamp.text = expense.date?.toDate()?.let { date ->
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            "${dateFormat.format(date)} - ${expense.debtReason}"
        } ?: ""
    }

    override fun getItemCount() = expenseList.size

    // Update data and notify adapter
    fun updateData(newList: List<ExpenseDO>, userIdToUserNameMap: Map<String, String>) {
        this.userIdToUserNameMap = userIdToUserNameMap
        newList.forEach { expense ->
            expense.paidBy = userIdToUserNameMap[expense.paidBy] ?: expense.paidBy
            expense.paidFor = userIdToUserNameMap[expense.paidFor] ?: expense.paidFor
        }
        expenseList = newList
        notifyDataSetChanged()
    }
}

