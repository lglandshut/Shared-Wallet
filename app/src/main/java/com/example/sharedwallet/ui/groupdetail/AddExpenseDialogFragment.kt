package com.example.sharedwallet.ui.groupdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.R
import com.example.sharedwallet.firebase.objects.ExpenseDO
import com.example.sharedwallet.firebase.objects.UserDO

class AddExpenseDialogFragment(private var userList: List<String>) : DialogFragment() {

    private lateinit var recyclerViewAdapter: UserDeptSplitAdapter
    private val viewModel: GroupDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val expenseAmount = view.findViewById<EditText>(R.id.expense_amount)
        val expenseText = view.findViewById<TextView>(R.id.expense_title)
        val expenseReason = view.findViewById<EditText>(R.id.expense_reason)
        val recyclerView = view.findViewById<RecyclerView>(R.id.user_expense_split_recyclerview)
        val distributionTypeRadioGroup = view.findViewById<RadioGroup>(R.id.distribution_type)

        view.visibility = View.VISIBLE
        expenseText.visibility = View.GONE
        expenseAmount.visibility = View.GONE
        recyclerView.visibility = View.GONE

        // RecyclerView konfigurieren
        recyclerViewAdapter = UserDeptSplitAdapter(userList.map { UserDebt(it) }.toList(), viewModel.userIdToUserNameMap)
        recyclerView.adapter = recyclerViewAdapter

        val cancelButton = view.findViewById<Button>(R.id.button_cancel)
        cancelButton.setOnClickListener {
            dismiss() // Close the dialog
        }

        val addButton = view.findViewById<Button>(R.id.button_add)
        addButton.setOnClickListener {
            when (distributionTypeRadioGroup.checkedRadioButtonId) {
                R.id.distribution_equal -> {
                    val expenseAmountPerUser = expenseAmount.text.toString().toDouble()/ (userList.size + 1)
                    val expenseList = mutableListOf<ExpenseDO>()
                    userList.forEach {
                        val expense = ExpenseDO(null, null, it,
                            expenseAmountPerUser, expenseReason.text.toString())
                        expenseList.add(expense)
                    }
                    viewModel.addExpense(expenseList)
                }

                R.id.distribution_custom -> {
                    val expenseList = mutableListOf<ExpenseDO>()

                }
            }
            dismiss() // Close the dialog
        }

        distributionTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.distribution_equal -> {
                    expenseAmount.visibility = View.VISIBLE
                    expenseReason.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }

                R.id.distribution_custom -> {
                    expenseAmount.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }
}
